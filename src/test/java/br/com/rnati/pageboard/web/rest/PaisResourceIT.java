package br.com.rnati.pageboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import br.com.rnati.pageboard.IntegrationTest;
import br.com.rnati.pageboard.domain.Pais;
import br.com.rnati.pageboard.repository.EntityManager;
import br.com.rnati.pageboard.repository.PaisRepository;
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

/**
 * Integration tests for the {@link PaisResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PaisResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_SIGLA = "AAAAAAAAAA";
    private static final String UPDATED_SIGLA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/pais";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaisRepository paisRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Pais pais;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pais createEntity(EntityManager em) {
        Pais pais = new Pais().nome(DEFAULT_NOME).sigla(DEFAULT_SIGLA);
        return pais;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pais createUpdatedEntity(EntityManager em) {
        Pais pais = new Pais().nome(UPDATED_NOME).sigla(UPDATED_SIGLA);
        return pais;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Pais.class).block();
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
        pais = createEntity(em);
    }

    @Test
    void createPais() throws Exception {
        int databaseSizeBeforeCreate = paisRepository.findAll().collectList().block().size();
        // Create the Pais
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pais))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Pais in the database
        List<Pais> paisList = paisRepository.findAll().collectList().block();
        assertThat(paisList).hasSize(databaseSizeBeforeCreate + 1);
        Pais testPais = paisList.get(paisList.size() - 1);
        assertThat(testPais.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testPais.getSigla()).isEqualTo(DEFAULT_SIGLA);
    }

    @Test
    void createPaisWithExistingId() throws Exception {
        // Create the Pais with an existing ID
        pais.setId(1L);

        int databaseSizeBeforeCreate = paisRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pais))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pais in the database
        List<Pais> paisList = paisRepository.findAll().collectList().block();
        assertThat(paisList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPaisAsStream() {
        // Initialize the database
        paisRepository.save(pais).block();

        List<Pais> paisList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Pais.class)
            .getResponseBody()
            .filter(pais::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(paisList).isNotNull();
        assertThat(paisList).hasSize(1);
        Pais testPais = paisList.get(0);
        assertThat(testPais.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testPais.getSigla()).isEqualTo(DEFAULT_SIGLA);
    }

    @Test
    void getAllPais() {
        // Initialize the database
        paisRepository.save(pais).block();

        // Get all the paisList
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
            .value(hasItem(pais.getId().intValue()))
            .jsonPath("$.[*].nome")
            .value(hasItem(DEFAULT_NOME))
            .jsonPath("$.[*].sigla")
            .value(hasItem(DEFAULT_SIGLA));
    }

    @Test
    void getPais() {
        // Initialize the database
        paisRepository.save(pais).block();

        // Get the pais
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pais.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pais.getId().intValue()))
            .jsonPath("$.nome")
            .value(is(DEFAULT_NOME))
            .jsonPath("$.sigla")
            .value(is(DEFAULT_SIGLA));
    }

    @Test
    void getNonExistingPais() {
        // Get the pais
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPais() throws Exception {
        // Initialize the database
        paisRepository.save(pais).block();

        int databaseSizeBeforeUpdate = paisRepository.findAll().collectList().block().size();

        // Update the pais
        Pais updatedPais = paisRepository.findById(pais.getId()).block();
        updatedPais.nome(UPDATED_NOME).sigla(UPDATED_SIGLA);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPais.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPais))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pais in the database
        List<Pais> paisList = paisRepository.findAll().collectList().block();
        assertThat(paisList).hasSize(databaseSizeBeforeUpdate);
        Pais testPais = paisList.get(paisList.size() - 1);
        assertThat(testPais.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testPais.getSigla()).isEqualTo(UPDATED_SIGLA);
    }

    @Test
    void putNonExistingPais() throws Exception {
        int databaseSizeBeforeUpdate = paisRepository.findAll().collectList().block().size();
        pais.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pais.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pais))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pais in the database
        List<Pais> paisList = paisRepository.findAll().collectList().block();
        assertThat(paisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPais() throws Exception {
        int databaseSizeBeforeUpdate = paisRepository.findAll().collectList().block().size();
        pais.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pais))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pais in the database
        List<Pais> paisList = paisRepository.findAll().collectList().block();
        assertThat(paisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPais() throws Exception {
        int databaseSizeBeforeUpdate = paisRepository.findAll().collectList().block().size();
        pais.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pais))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pais in the database
        List<Pais> paisList = paisRepository.findAll().collectList().block();
        assertThat(paisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePaisWithPatch() throws Exception {
        // Initialize the database
        paisRepository.save(pais).block();

        int databaseSizeBeforeUpdate = paisRepository.findAll().collectList().block().size();

        // Update the pais using partial update
        Pais partialUpdatedPais = new Pais();
        partialUpdatedPais.setId(pais.getId());

        partialUpdatedPais.nome(UPDATED_NOME).sigla(UPDATED_SIGLA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPais.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPais))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pais in the database
        List<Pais> paisList = paisRepository.findAll().collectList().block();
        assertThat(paisList).hasSize(databaseSizeBeforeUpdate);
        Pais testPais = paisList.get(paisList.size() - 1);
        assertThat(testPais.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testPais.getSigla()).isEqualTo(UPDATED_SIGLA);
    }

    @Test
    void fullUpdatePaisWithPatch() throws Exception {
        // Initialize the database
        paisRepository.save(pais).block();

        int databaseSizeBeforeUpdate = paisRepository.findAll().collectList().block().size();

        // Update the pais using partial update
        Pais partialUpdatedPais = new Pais();
        partialUpdatedPais.setId(pais.getId());

        partialUpdatedPais.nome(UPDATED_NOME).sigla(UPDATED_SIGLA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPais.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPais))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pais in the database
        List<Pais> paisList = paisRepository.findAll().collectList().block();
        assertThat(paisList).hasSize(databaseSizeBeforeUpdate);
        Pais testPais = paisList.get(paisList.size() - 1);
        assertThat(testPais.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testPais.getSigla()).isEqualTo(UPDATED_SIGLA);
    }

    @Test
    void patchNonExistingPais() throws Exception {
        int databaseSizeBeforeUpdate = paisRepository.findAll().collectList().block().size();
        pais.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pais.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pais))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pais in the database
        List<Pais> paisList = paisRepository.findAll().collectList().block();
        assertThat(paisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPais() throws Exception {
        int databaseSizeBeforeUpdate = paisRepository.findAll().collectList().block().size();
        pais.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pais))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pais in the database
        List<Pais> paisList = paisRepository.findAll().collectList().block();
        assertThat(paisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPais() throws Exception {
        int databaseSizeBeforeUpdate = paisRepository.findAll().collectList().block().size();
        pais.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pais))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pais in the database
        List<Pais> paisList = paisRepository.findAll().collectList().block();
        assertThat(paisList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePais() {
        // Initialize the database
        paisRepository.save(pais).block();

        int databaseSizeBeforeDelete = paisRepository.findAll().collectList().block().size();

        // Delete the pais
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pais.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Pais> paisList = paisRepository.findAll().collectList().block();
        assertThat(paisList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
