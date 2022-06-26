package br.com.rnati.pageboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import br.com.rnati.pageboard.IntegrationTest;
import br.com.rnati.pageboard.domain.Projeto;
import br.com.rnati.pageboard.repository.EntityManager;
import br.com.rnati.pageboard.repository.ProjetoRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link ProjetoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProjetoResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRICAO = "AAAAAAAAAA";
    private static final String UPDATED_DESCRICAO = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGEM = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGEM = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGEM_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGEM_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_TAGS = "AAAAAAAAAA";
    private static final String UPDATED_TAGS = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/projetos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Projeto projeto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Projeto createEntity(EntityManager em) {
        Projeto projeto = new Projeto()
            .nome(DEFAULT_NOME)
            .descricao(DEFAULT_DESCRICAO)
            .imagem(DEFAULT_IMAGEM)
            .imagemContentType(DEFAULT_IMAGEM_CONTENT_TYPE)
            .tags(DEFAULT_TAGS);
        return projeto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Projeto createUpdatedEntity(EntityManager em) {
        Projeto projeto = new Projeto()
            .nome(UPDATED_NOME)
            .descricao(UPDATED_DESCRICAO)
            .imagem(UPDATED_IMAGEM)
            .imagemContentType(UPDATED_IMAGEM_CONTENT_TYPE)
            .tags(UPDATED_TAGS);
        return projeto;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Projeto.class).block();
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
        projeto = createEntity(em);
    }

    @Test
    void createProjeto() throws Exception {
        int databaseSizeBeforeCreate = projetoRepository.findAll().collectList().block().size();
        // Create the Projeto
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projeto))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Projeto in the database
        List<Projeto> projetoList = projetoRepository.findAll().collectList().block();
        assertThat(projetoList).hasSize(databaseSizeBeforeCreate + 1);
        Projeto testProjeto = projetoList.get(projetoList.size() - 1);
        assertThat(testProjeto.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testProjeto.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testProjeto.getImagem()).isEqualTo(DEFAULT_IMAGEM);
        assertThat(testProjeto.getImagemContentType()).isEqualTo(DEFAULT_IMAGEM_CONTENT_TYPE);
        assertThat(testProjeto.getTags()).isEqualTo(DEFAULT_TAGS);
    }

    @Test
    void createProjetoWithExistingId() throws Exception {
        // Create the Projeto with an existing ID
        projeto.setId(1L);

        int databaseSizeBeforeCreate = projetoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projeto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Projeto in the database
        List<Projeto> projetoList = projetoRepository.findAll().collectList().block();
        assertThat(projetoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllProjetosAsStream() {
        // Initialize the database
        projetoRepository.save(projeto).block();

        List<Projeto> projetoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Projeto.class)
            .getResponseBody()
            .filter(projeto::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(projetoList).isNotNull();
        assertThat(projetoList).hasSize(1);
        Projeto testProjeto = projetoList.get(0);
        assertThat(testProjeto.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testProjeto.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testProjeto.getImagem()).isEqualTo(DEFAULT_IMAGEM);
        assertThat(testProjeto.getImagemContentType()).isEqualTo(DEFAULT_IMAGEM_CONTENT_TYPE);
        assertThat(testProjeto.getTags()).isEqualTo(DEFAULT_TAGS);
    }

    @Test
    void getAllProjetos() {
        // Initialize the database
        projetoRepository.save(projeto).block();

        // Get all the projetoList
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
            .value(hasItem(projeto.getId().intValue()))
            .jsonPath("$.[*].nome")
            .value(hasItem(DEFAULT_NOME))
            .jsonPath("$.[*].descricao")
            .value(hasItem(DEFAULT_DESCRICAO))
            .jsonPath("$.[*].imagemContentType")
            .value(hasItem(DEFAULT_IMAGEM_CONTENT_TYPE))
            .jsonPath("$.[*].imagem")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGEM)))
            .jsonPath("$.[*].tags")
            .value(hasItem(DEFAULT_TAGS.toString()));
    }

    @Test
    void getProjeto() {
        // Initialize the database
        projetoRepository.save(projeto).block();

        // Get the projeto
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, projeto.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(projeto.getId().intValue()))
            .jsonPath("$.nome")
            .value(is(DEFAULT_NOME))
            .jsonPath("$.descricao")
            .value(is(DEFAULT_DESCRICAO))
            .jsonPath("$.imagemContentType")
            .value(is(DEFAULT_IMAGEM_CONTENT_TYPE))
            .jsonPath("$.imagem")
            .value(is(Base64Utils.encodeToString(DEFAULT_IMAGEM)))
            .jsonPath("$.tags")
            .value(is(DEFAULT_TAGS.toString()));
    }

    @Test
    void getNonExistingProjeto() {
        // Get the projeto
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewProjeto() throws Exception {
        // Initialize the database
        projetoRepository.save(projeto).block();

        int databaseSizeBeforeUpdate = projetoRepository.findAll().collectList().block().size();

        // Update the projeto
        Projeto updatedProjeto = projetoRepository.findById(projeto.getId()).block();
        updatedProjeto
            .nome(UPDATED_NOME)
            .descricao(UPDATED_DESCRICAO)
            .imagem(UPDATED_IMAGEM)
            .imagemContentType(UPDATED_IMAGEM_CONTENT_TYPE)
            .tags(UPDATED_TAGS);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedProjeto.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedProjeto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Projeto in the database
        List<Projeto> projetoList = projetoRepository.findAll().collectList().block();
        assertThat(projetoList).hasSize(databaseSizeBeforeUpdate);
        Projeto testProjeto = projetoList.get(projetoList.size() - 1);
        assertThat(testProjeto.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testProjeto.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testProjeto.getImagem()).isEqualTo(UPDATED_IMAGEM);
        assertThat(testProjeto.getImagemContentType()).isEqualTo(UPDATED_IMAGEM_CONTENT_TYPE);
        assertThat(testProjeto.getTags()).isEqualTo(UPDATED_TAGS);
    }

    @Test
    void putNonExistingProjeto() throws Exception {
        int databaseSizeBeforeUpdate = projetoRepository.findAll().collectList().block().size();
        projeto.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projeto.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projeto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Projeto in the database
        List<Projeto> projetoList = projetoRepository.findAll().collectList().block();
        assertThat(projetoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProjeto() throws Exception {
        int databaseSizeBeforeUpdate = projetoRepository.findAll().collectList().block().size();
        projeto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projeto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Projeto in the database
        List<Projeto> projetoList = projetoRepository.findAll().collectList().block();
        assertThat(projetoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProjeto() throws Exception {
        int databaseSizeBeforeUpdate = projetoRepository.findAll().collectList().block().size();
        projeto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(projeto))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Projeto in the database
        List<Projeto> projetoList = projetoRepository.findAll().collectList().block();
        assertThat(projetoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProjetoWithPatch() throws Exception {
        // Initialize the database
        projetoRepository.save(projeto).block();

        int databaseSizeBeforeUpdate = projetoRepository.findAll().collectList().block().size();

        // Update the projeto using partial update
        Projeto partialUpdatedProjeto = new Projeto();
        partialUpdatedProjeto.setId(projeto.getId());

        partialUpdatedProjeto.nome(UPDATED_NOME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjeto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProjeto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Projeto in the database
        List<Projeto> projetoList = projetoRepository.findAll().collectList().block();
        assertThat(projetoList).hasSize(databaseSizeBeforeUpdate);
        Projeto testProjeto = projetoList.get(projetoList.size() - 1);
        assertThat(testProjeto.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testProjeto.getDescricao()).isEqualTo(DEFAULT_DESCRICAO);
        assertThat(testProjeto.getImagem()).isEqualTo(DEFAULT_IMAGEM);
        assertThat(testProjeto.getImagemContentType()).isEqualTo(DEFAULT_IMAGEM_CONTENT_TYPE);
        assertThat(testProjeto.getTags()).isEqualTo(DEFAULT_TAGS);
    }

    @Test
    void fullUpdateProjetoWithPatch() throws Exception {
        // Initialize the database
        projetoRepository.save(projeto).block();

        int databaseSizeBeforeUpdate = projetoRepository.findAll().collectList().block().size();

        // Update the projeto using partial update
        Projeto partialUpdatedProjeto = new Projeto();
        partialUpdatedProjeto.setId(projeto.getId());

        partialUpdatedProjeto
            .nome(UPDATED_NOME)
            .descricao(UPDATED_DESCRICAO)
            .imagem(UPDATED_IMAGEM)
            .imagemContentType(UPDATED_IMAGEM_CONTENT_TYPE)
            .tags(UPDATED_TAGS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjeto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedProjeto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Projeto in the database
        List<Projeto> projetoList = projetoRepository.findAll().collectList().block();
        assertThat(projetoList).hasSize(databaseSizeBeforeUpdate);
        Projeto testProjeto = projetoList.get(projetoList.size() - 1);
        assertThat(testProjeto.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testProjeto.getDescricao()).isEqualTo(UPDATED_DESCRICAO);
        assertThat(testProjeto.getImagem()).isEqualTo(UPDATED_IMAGEM);
        assertThat(testProjeto.getImagemContentType()).isEqualTo(UPDATED_IMAGEM_CONTENT_TYPE);
        assertThat(testProjeto.getTags()).isEqualTo(UPDATED_TAGS);
    }

    @Test
    void patchNonExistingProjeto() throws Exception {
        int databaseSizeBeforeUpdate = projetoRepository.findAll().collectList().block().size();
        projeto.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, projeto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projeto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Projeto in the database
        List<Projeto> projetoList = projetoRepository.findAll().collectList().block();
        assertThat(projetoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProjeto() throws Exception {
        int databaseSizeBeforeUpdate = projetoRepository.findAll().collectList().block().size();
        projeto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projeto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Projeto in the database
        List<Projeto> projetoList = projetoRepository.findAll().collectList().block();
        assertThat(projetoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProjeto() throws Exception {
        int databaseSizeBeforeUpdate = projetoRepository.findAll().collectList().block().size();
        projeto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(projeto))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Projeto in the database
        List<Projeto> projetoList = projetoRepository.findAll().collectList().block();
        assertThat(projetoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProjeto() {
        // Initialize the database
        projetoRepository.save(projeto).block();

        int databaseSizeBeforeDelete = projetoRepository.findAll().collectList().block().size();

        // Delete the projeto
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, projeto.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Projeto> projetoList = projetoRepository.findAll().collectList().block();
        assertThat(projetoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
