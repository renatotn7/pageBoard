package br.com.rnati.pageboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import br.com.rnati.pageboard.IntegrationTest;
import br.com.rnati.pageboard.domain.Cidade;
import br.com.rnati.pageboard.repository.CidadeRepository;
import br.com.rnati.pageboard.repository.EntityManager;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Integration tests for the {@link CidadeResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CidadeResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/cidades";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CidadeRepository cidadeRepository;

    @Mock
    private CidadeRepository cidadeRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Cidade cidade;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cidade createEntity(EntityManager em) {
        Cidade cidade = new Cidade().nome(DEFAULT_NOME);
        return cidade;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Cidade createUpdatedEntity(EntityManager em) {
        Cidade cidade = new Cidade().nome(UPDATED_NOME);
        return cidade;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Cidade.class).block();
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
        cidade = createEntity(em);
    }

    @Test
    void createCidade() throws Exception {
        int databaseSizeBeforeCreate = cidadeRepository.findAll().collectList().block().size();
        // Create the Cidade
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cidade))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Cidade in the database
        List<Cidade> cidadeList = cidadeRepository.findAll().collectList().block();
        assertThat(cidadeList).hasSize(databaseSizeBeforeCreate + 1);
        Cidade testCidade = cidadeList.get(cidadeList.size() - 1);
        assertThat(testCidade.getNome()).isEqualTo(DEFAULT_NOME);
    }

    @Test
    void createCidadeWithExistingId() throws Exception {
        // Create the Cidade with an existing ID
        cidade.setId(1L);

        int databaseSizeBeforeCreate = cidadeRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cidade))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cidade in the database
        List<Cidade> cidadeList = cidadeRepository.findAll().collectList().block();
        assertThat(cidadeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllCidadesAsStream() {
        // Initialize the database
        cidadeRepository.save(cidade).block();

        List<Cidade> cidadeList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Cidade.class)
            .getResponseBody()
            .filter(cidade::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(cidadeList).isNotNull();
        assertThat(cidadeList).hasSize(1);
        Cidade testCidade = cidadeList.get(0);
        assertThat(testCidade.getNome()).isEqualTo(DEFAULT_NOME);
    }

    @Test
    void getAllCidades() {
        // Initialize the database
        cidadeRepository.save(cidade).block();

        // Get all the cidadeList
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
            .value(hasItem(cidade.getId().intValue()))
            .jsonPath("$.[*].nome")
            .value(hasItem(DEFAULT_NOME));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCidadesWithEagerRelationshipsIsEnabled() {
        when(cidadeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(cidadeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllCidadesWithEagerRelationshipsIsNotEnabled() {
        when(cidadeRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(cidadeRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getCidade() {
        // Initialize the database
        cidadeRepository.save(cidade).block();

        // Get the cidade
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, cidade.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(cidade.getId().intValue()))
            .jsonPath("$.nome")
            .value(is(DEFAULT_NOME));
    }

    @Test
    void getNonExistingCidade() {
        // Get the cidade
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewCidade() throws Exception {
        // Initialize the database
        cidadeRepository.save(cidade).block();

        int databaseSizeBeforeUpdate = cidadeRepository.findAll().collectList().block().size();

        // Update the cidade
        Cidade updatedCidade = cidadeRepository.findById(cidade.getId()).block();
        updatedCidade.nome(UPDATED_NOME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedCidade.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedCidade))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cidade in the database
        List<Cidade> cidadeList = cidadeRepository.findAll().collectList().block();
        assertThat(cidadeList).hasSize(databaseSizeBeforeUpdate);
        Cidade testCidade = cidadeList.get(cidadeList.size() - 1);
        assertThat(testCidade.getNome()).isEqualTo(UPDATED_NOME);
    }

    @Test
    void putNonExistingCidade() throws Exception {
        int databaseSizeBeforeUpdate = cidadeRepository.findAll().collectList().block().size();
        cidade.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, cidade.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cidade))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cidade in the database
        List<Cidade> cidadeList = cidadeRepository.findAll().collectList().block();
        assertThat(cidadeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchCidade() throws Exception {
        int databaseSizeBeforeUpdate = cidadeRepository.findAll().collectList().block().size();
        cidade.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cidade))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cidade in the database
        List<Cidade> cidadeList = cidadeRepository.findAll().collectList().block();
        assertThat(cidadeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamCidade() throws Exception {
        int databaseSizeBeforeUpdate = cidadeRepository.findAll().collectList().block().size();
        cidade.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(cidade))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cidade in the database
        List<Cidade> cidadeList = cidadeRepository.findAll().collectList().block();
        assertThat(cidadeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCidadeWithPatch() throws Exception {
        // Initialize the database
        cidadeRepository.save(cidade).block();

        int databaseSizeBeforeUpdate = cidadeRepository.findAll().collectList().block().size();

        // Update the cidade using partial update
        Cidade partialUpdatedCidade = new Cidade();
        partialUpdatedCidade.setId(cidade.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCidade.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCidade))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cidade in the database
        List<Cidade> cidadeList = cidadeRepository.findAll().collectList().block();
        assertThat(cidadeList).hasSize(databaseSizeBeforeUpdate);
        Cidade testCidade = cidadeList.get(cidadeList.size() - 1);
        assertThat(testCidade.getNome()).isEqualTo(DEFAULT_NOME);
    }

    @Test
    void fullUpdateCidadeWithPatch() throws Exception {
        // Initialize the database
        cidadeRepository.save(cidade).block();

        int databaseSizeBeforeUpdate = cidadeRepository.findAll().collectList().block().size();

        // Update the cidade using partial update
        Cidade partialUpdatedCidade = new Cidade();
        partialUpdatedCidade.setId(cidade.getId());

        partialUpdatedCidade.nome(UPDATED_NOME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedCidade.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedCidade))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Cidade in the database
        List<Cidade> cidadeList = cidadeRepository.findAll().collectList().block();
        assertThat(cidadeList).hasSize(databaseSizeBeforeUpdate);
        Cidade testCidade = cidadeList.get(cidadeList.size() - 1);
        assertThat(testCidade.getNome()).isEqualTo(UPDATED_NOME);
    }

    @Test
    void patchNonExistingCidade() throws Exception {
        int databaseSizeBeforeUpdate = cidadeRepository.findAll().collectList().block().size();
        cidade.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, cidade.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cidade))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cidade in the database
        List<Cidade> cidadeList = cidadeRepository.findAll().collectList().block();
        assertThat(cidadeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchCidade() throws Exception {
        int databaseSizeBeforeUpdate = cidadeRepository.findAll().collectList().block().size();
        cidade.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cidade))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Cidade in the database
        List<Cidade> cidadeList = cidadeRepository.findAll().collectList().block();
        assertThat(cidadeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamCidade() throws Exception {
        int databaseSizeBeforeUpdate = cidadeRepository.findAll().collectList().block().size();
        cidade.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(cidade))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Cidade in the database
        List<Cidade> cidadeList = cidadeRepository.findAll().collectList().block();
        assertThat(cidadeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteCidade() {
        // Initialize the database
        cidadeRepository.save(cidade).block();

        int databaseSizeBeforeDelete = cidadeRepository.findAll().collectList().block().size();

        // Delete the cidade
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, cidade.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Cidade> cidadeList = cidadeRepository.findAll().collectList().block();
        assertThat(cidadeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
