package br.com.rnati.pageboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import br.com.rnati.pageboard.IntegrationTest;
import br.com.rnati.pageboard.domain.Livro;
import br.com.rnati.pageboard.repository.EntityManager;
import br.com.rnati.pageboard.repository.LivroRepository;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link LivroResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class LivroResourceIT {

    private static final String DEFAULT_NOME_LIVRO = "AAAAAAAAAA";
    private static final String UPDATED_NOME_LIVRO = "BBBBBBBBBB";

    private static final String DEFAULT_EDITORA = "AAAAAAAAAA";
    private static final String UPDATED_EDITORA = "BBBBBBBBBB";

    private static final String DEFAULT_AUTOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTOR = "BBBBBBBBBB";

    private static final Integer DEFAULT_ANO_DE_PUBLICACAO = 1;
    private static final Integer UPDATED_ANO_DE_PUBLICACAO = 2;

    private static final String DEFAULT_TAGS = "AAAAAAAAAA";
    private static final String UPDATED_TAGS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/livros";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private LivroRepository livroRepository;

    @Mock
    private LivroRepository livroRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Livro livro;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Livro createEntity(EntityManager em) {
        Livro livro = new Livro()
            .nomeLivro(DEFAULT_NOME_LIVRO)
            .editora(DEFAULT_EDITORA)
            .autor(DEFAULT_AUTOR)
            .anoDePublicacao(DEFAULT_ANO_DE_PUBLICACAO)
            .tags(DEFAULT_TAGS);
        return livro;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Livro createUpdatedEntity(EntityManager em) {
        Livro livro = new Livro()
            .nomeLivro(UPDATED_NOME_LIVRO)
            .editora(UPDATED_EDITORA)
            .autor(UPDATED_AUTOR)
            .anoDePublicacao(UPDATED_ANO_DE_PUBLICACAO)
            .tags(UPDATED_TAGS);
        return livro;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_livro__projeto").block();
            em.deleteAll(Livro.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        livro = createEntity(em);
    }

    @Test
    void createLivro() throws Exception {
        int databaseSizeBeforeCreate = livroRepository.findAll().collectList().block().size();
        // Create the Livro
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(livro))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Livro in the database
        List<Livro> livroList = livroRepository.findAll().collectList().block();
        assertThat(livroList).hasSize(databaseSizeBeforeCreate + 1);
        Livro testLivro = livroList.get(livroList.size() - 1);
        assertThat(testLivro.getNomeLivro()).isEqualTo(DEFAULT_NOME_LIVRO);
        assertThat(testLivro.getEditora()).isEqualTo(DEFAULT_EDITORA);
        assertThat(testLivro.getAutor()).isEqualTo(DEFAULT_AUTOR);
        assertThat(testLivro.getAnoDePublicacao()).isEqualTo(DEFAULT_ANO_DE_PUBLICACAO);
        assertThat(testLivro.getTags()).isEqualTo(DEFAULT_TAGS);
    }

    @Test
    void createLivroWithExistingId() throws Exception {
        // Create the Livro with an existing ID
        livro.setId(1L);

        int databaseSizeBeforeCreate = livroRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(livro))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Livro in the database
        List<Livro> livroList = livroRepository.findAll().collectList().block();
        assertThat(livroList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllLivrosAsStream() {
        // Initialize the database
        livroRepository.save(livro).block();

        List<Livro> livroList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Livro.class)
            .getResponseBody()
            .filter(livro::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(livroList).isNotNull();
        assertThat(livroList).hasSize(1);
        Livro testLivro = livroList.get(0);
        assertThat(testLivro.getNomeLivro()).isEqualTo(DEFAULT_NOME_LIVRO);
        assertThat(testLivro.getEditora()).isEqualTo(DEFAULT_EDITORA);
        assertThat(testLivro.getAutor()).isEqualTo(DEFAULT_AUTOR);
        assertThat(testLivro.getAnoDePublicacao()).isEqualTo(DEFAULT_ANO_DE_PUBLICACAO);
        assertThat(testLivro.getTags()).isEqualTo(DEFAULT_TAGS);
    }

    @Test
    void getAllLivros() {
        // Initialize the database
        livroRepository.save(livro).block();

        // Get all the livroList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(livro.getId().intValue()))
            .jsonPath("$.[*].nomeLivro")
            .value(hasItem(DEFAULT_NOME_LIVRO))
            .jsonPath("$.[*].editora")
            .value(hasItem(DEFAULT_EDITORA))
            .jsonPath("$.[*].autor")
            .value(hasItem(DEFAULT_AUTOR))
            .jsonPath("$.[*].anoDePublicacao")
            .value(hasItem(DEFAULT_ANO_DE_PUBLICACAO))
            .jsonPath("$.[*].tags")
            .value(hasItem(DEFAULT_TAGS.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLivrosWithEagerRelationshipsIsEnabled() {
        when(livroRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(livroRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLivrosWithEagerRelationshipsIsNotEnabled() {
        when(livroRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(livroRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getLivro() {
        // Initialize the database
        livroRepository.save(livro).block();

        // Get the livro
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, livro.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(livro.getId().intValue()))
            .jsonPath("$.nomeLivro")
            .value(is(DEFAULT_NOME_LIVRO))
            .jsonPath("$.editora")
            .value(is(DEFAULT_EDITORA))
            .jsonPath("$.autor")
            .value(is(DEFAULT_AUTOR))
            .jsonPath("$.anoDePublicacao")
            .value(is(DEFAULT_ANO_DE_PUBLICACAO))
            .jsonPath("$.tags")
            .value(is(DEFAULT_TAGS.toString()));
    }

    @Test
    void getNonExistingLivro() {
        // Get the livro
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewLivro() throws Exception {
        // Initialize the database
        livroRepository.save(livro).block();

        int databaseSizeBeforeUpdate = livroRepository.findAll().collectList().block().size();

        // Update the livro
        Livro updatedLivro = livroRepository.findById(livro.getId()).block();
        updatedLivro
            .nomeLivro(UPDATED_NOME_LIVRO)
            .editora(UPDATED_EDITORA)
            .autor(UPDATED_AUTOR)
            .anoDePublicacao(UPDATED_ANO_DE_PUBLICACAO)
            .tags(UPDATED_TAGS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedLivro.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedLivro))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Livro in the database
        List<Livro> livroList = livroRepository.findAll().collectList().block();
        assertThat(livroList).hasSize(databaseSizeBeforeUpdate);
        Livro testLivro = livroList.get(livroList.size() - 1);
        assertThat(testLivro.getNomeLivro()).isEqualTo(UPDATED_NOME_LIVRO);
        assertThat(testLivro.getEditora()).isEqualTo(UPDATED_EDITORA);
        assertThat(testLivro.getAutor()).isEqualTo(UPDATED_AUTOR);
        assertThat(testLivro.getAnoDePublicacao()).isEqualTo(UPDATED_ANO_DE_PUBLICACAO);
        assertThat(testLivro.getTags()).isEqualTo(UPDATED_TAGS);
    }

    @Test
    void putNonExistingLivro() throws Exception {
        int databaseSizeBeforeUpdate = livroRepository.findAll().collectList().block().size();
        livro.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, livro.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(livro))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Livro in the database
        List<Livro> livroList = livroRepository.findAll().collectList().block();
        assertThat(livroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchLivro() throws Exception {
        int databaseSizeBeforeUpdate = livroRepository.findAll().collectList().block().size();
        livro.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(livro))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Livro in the database
        List<Livro> livroList = livroRepository.findAll().collectList().block();
        assertThat(livroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamLivro() throws Exception {
        int databaseSizeBeforeUpdate = livroRepository.findAll().collectList().block().size();
        livro.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(livro))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Livro in the database
        List<Livro> livroList = livroRepository.findAll().collectList().block();
        assertThat(livroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateLivroWithPatch() throws Exception {
        // Initialize the database
        livroRepository.save(livro).block();

        int databaseSizeBeforeUpdate = livroRepository.findAll().collectList().block().size();

        // Update the livro using partial update
        Livro partialUpdatedLivro = new Livro();
        partialUpdatedLivro.setId(livro.getId());

        partialUpdatedLivro.editora(UPDATED_EDITORA).tags(UPDATED_TAGS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLivro.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedLivro))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Livro in the database
        List<Livro> livroList = livroRepository.findAll().collectList().block();
        assertThat(livroList).hasSize(databaseSizeBeforeUpdate);
        Livro testLivro = livroList.get(livroList.size() - 1);
        assertThat(testLivro.getNomeLivro()).isEqualTo(DEFAULT_NOME_LIVRO);
        assertThat(testLivro.getEditora()).isEqualTo(UPDATED_EDITORA);
        assertThat(testLivro.getAutor()).isEqualTo(DEFAULT_AUTOR);
        assertThat(testLivro.getAnoDePublicacao()).isEqualTo(DEFAULT_ANO_DE_PUBLICACAO);
        assertThat(testLivro.getTags()).isEqualTo(UPDATED_TAGS);
    }

    @Test
    void fullUpdateLivroWithPatch() throws Exception {
        // Initialize the database
        livroRepository.save(livro).block();

        int databaseSizeBeforeUpdate = livroRepository.findAll().collectList().block().size();

        // Update the livro using partial update
        Livro partialUpdatedLivro = new Livro();
        partialUpdatedLivro.setId(livro.getId());

        partialUpdatedLivro
            .nomeLivro(UPDATED_NOME_LIVRO)
            .editora(UPDATED_EDITORA)
            .autor(UPDATED_AUTOR)
            .anoDePublicacao(UPDATED_ANO_DE_PUBLICACAO)
            .tags(UPDATED_TAGS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedLivro.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedLivro))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Livro in the database
        List<Livro> livroList = livroRepository.findAll().collectList().block();
        assertThat(livroList).hasSize(databaseSizeBeforeUpdate);
        Livro testLivro = livroList.get(livroList.size() - 1);
        assertThat(testLivro.getNomeLivro()).isEqualTo(UPDATED_NOME_LIVRO);
        assertThat(testLivro.getEditora()).isEqualTo(UPDATED_EDITORA);
        assertThat(testLivro.getAutor()).isEqualTo(UPDATED_AUTOR);
        assertThat(testLivro.getAnoDePublicacao()).isEqualTo(UPDATED_ANO_DE_PUBLICACAO);
        assertThat(testLivro.getTags()).isEqualTo(UPDATED_TAGS);
    }

    @Test
    void patchNonExistingLivro() throws Exception {
        int databaseSizeBeforeUpdate = livroRepository.findAll().collectList().block().size();
        livro.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, livro.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(livro))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Livro in the database
        List<Livro> livroList = livroRepository.findAll().collectList().block();
        assertThat(livroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchLivro() throws Exception {
        int databaseSizeBeforeUpdate = livroRepository.findAll().collectList().block().size();
        livro.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(livro))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Livro in the database
        List<Livro> livroList = livroRepository.findAll().collectList().block();
        assertThat(livroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamLivro() throws Exception {
        int databaseSizeBeforeUpdate = livroRepository.findAll().collectList().block().size();
        livro.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(livro))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Livro in the database
        List<Livro> livroList = livroRepository.findAll().collectList().block();
        assertThat(livroList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteLivro() {
        // Initialize the database
        livroRepository.save(livro).block();

        int databaseSizeBeforeDelete = livroRepository.findAll().collectList().block().size();

        // Delete the livro
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, livro.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Livro> livroList = livroRepository.findAll().collectList().block();
        assertThat(livroList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
