package br.com.rnati.pageboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import br.com.rnati.pageboard.IntegrationTest;
import br.com.rnati.pageboard.domain.Assunto;
import br.com.rnati.pageboard.repository.AssuntoRepository;
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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link AssuntoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AssuntoResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/assuntos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AssuntoRepository assuntoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Assunto assunto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assunto createEntity(EntityManager em) {
        Assunto assunto = new Assunto().nome(DEFAULT_NOME);
        return assunto;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Assunto createUpdatedEntity(EntityManager em) {
        Assunto assunto = new Assunto().nome(UPDATED_NOME);
        return assunto;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Assunto.class).block();
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
        assunto = createEntity(em);
    }

    @Test
    void createAssunto() throws Exception {
        int databaseSizeBeforeCreate = assuntoRepository.findAll().collectList().block().size();
        // Create the Assunto
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(assunto))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Assunto in the database
        List<Assunto> assuntoList = assuntoRepository.findAll().collectList().block();
        assertThat(assuntoList).hasSize(databaseSizeBeforeCreate + 1);
        Assunto testAssunto = assuntoList.get(assuntoList.size() - 1);
        assertThat(testAssunto.getNome()).isEqualTo(DEFAULT_NOME);
    }

    @Test
    void createAssuntoWithExistingId() throws Exception {
        // Create the Assunto with an existing ID
        assunto.setId(1L);

        int databaseSizeBeforeCreate = assuntoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(assunto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Assunto in the database
        List<Assunto> assuntoList = assuntoRepository.findAll().collectList().block();
        assertThat(assuntoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllAssuntosAsStream() {
        // Initialize the database
        assuntoRepository.save(assunto).block();

        List<Assunto> assuntoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Assunto.class)
            .getResponseBody()
            .filter(assunto::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(assuntoList).isNotNull();
        assertThat(assuntoList).hasSize(1);
        Assunto testAssunto = assuntoList.get(0);
        assertThat(testAssunto.getNome()).isEqualTo(DEFAULT_NOME);
    }

    @Test
    void getAllAssuntos() {
        // Initialize the database
        assuntoRepository.save(assunto).block();

        // Get all the assuntoList
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
            .value(hasItem(assunto.getId().intValue()))
            .jsonPath("$.[*].nome")
            .value(hasItem(DEFAULT_NOME));
    }

    @Test
    void getAssunto() {
        // Initialize the database
        assuntoRepository.save(assunto).block();

        // Get the assunto
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, assunto.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(assunto.getId().intValue()))
            .jsonPath("$.nome")
            .value(is(DEFAULT_NOME));
    }

    @Test
    void getNonExistingAssunto() {
        // Get the assunto
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewAssunto() throws Exception {
        // Initialize the database
        assuntoRepository.save(assunto).block();

        int databaseSizeBeforeUpdate = assuntoRepository.findAll().collectList().block().size();

        // Update the assunto
        Assunto updatedAssunto = assuntoRepository.findById(assunto.getId()).block();
        updatedAssunto.nome(UPDATED_NOME);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAssunto.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedAssunto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Assunto in the database
        List<Assunto> assuntoList = assuntoRepository.findAll().collectList().block();
        assertThat(assuntoList).hasSize(databaseSizeBeforeUpdate);
        Assunto testAssunto = assuntoList.get(assuntoList.size() - 1);
        assertThat(testAssunto.getNome()).isEqualTo(UPDATED_NOME);
    }

    @Test
    void putNonExistingAssunto() throws Exception {
        int databaseSizeBeforeUpdate = assuntoRepository.findAll().collectList().block().size();
        assunto.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, assunto.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(assunto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Assunto in the database
        List<Assunto> assuntoList = assuntoRepository.findAll().collectList().block();
        assertThat(assuntoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAssunto() throws Exception {
        int databaseSizeBeforeUpdate = assuntoRepository.findAll().collectList().block().size();
        assunto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(assunto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Assunto in the database
        List<Assunto> assuntoList = assuntoRepository.findAll().collectList().block();
        assertThat(assuntoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAssunto() throws Exception {
        int databaseSizeBeforeUpdate = assuntoRepository.findAll().collectList().block().size();
        assunto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(assunto))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Assunto in the database
        List<Assunto> assuntoList = assuntoRepository.findAll().collectList().block();
        assertThat(assuntoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAssuntoWithPatch() throws Exception {
        // Initialize the database
        assuntoRepository.save(assunto).block();

        int databaseSizeBeforeUpdate = assuntoRepository.findAll().collectList().block().size();

        // Update the assunto using partial update
        Assunto partialUpdatedAssunto = new Assunto();
        partialUpdatedAssunto.setId(assunto.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAssunto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAssunto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Assunto in the database
        List<Assunto> assuntoList = assuntoRepository.findAll().collectList().block();
        assertThat(assuntoList).hasSize(databaseSizeBeforeUpdate);
        Assunto testAssunto = assuntoList.get(assuntoList.size() - 1);
        assertThat(testAssunto.getNome()).isEqualTo(DEFAULT_NOME);
    }

    @Test
    void fullUpdateAssuntoWithPatch() throws Exception {
        // Initialize the database
        assuntoRepository.save(assunto).block();

        int databaseSizeBeforeUpdate = assuntoRepository.findAll().collectList().block().size();

        // Update the assunto using partial update
        Assunto partialUpdatedAssunto = new Assunto();
        partialUpdatedAssunto.setId(assunto.getId());

        partialUpdatedAssunto.nome(UPDATED_NOME);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAssunto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAssunto))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Assunto in the database
        List<Assunto> assuntoList = assuntoRepository.findAll().collectList().block();
        assertThat(assuntoList).hasSize(databaseSizeBeforeUpdate);
        Assunto testAssunto = assuntoList.get(assuntoList.size() - 1);
        assertThat(testAssunto.getNome()).isEqualTo(UPDATED_NOME);
    }

    @Test
    void patchNonExistingAssunto() throws Exception {
        int databaseSizeBeforeUpdate = assuntoRepository.findAll().collectList().block().size();
        assunto.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, assunto.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(assunto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Assunto in the database
        List<Assunto> assuntoList = assuntoRepository.findAll().collectList().block();
        assertThat(assuntoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAssunto() throws Exception {
        int databaseSizeBeforeUpdate = assuntoRepository.findAll().collectList().block().size();
        assunto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(assunto))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Assunto in the database
        List<Assunto> assuntoList = assuntoRepository.findAll().collectList().block();
        assertThat(assuntoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAssunto() throws Exception {
        int databaseSizeBeforeUpdate = assuntoRepository.findAll().collectList().block().size();
        assunto.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(assunto))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Assunto in the database
        List<Assunto> assuntoList = assuntoRepository.findAll().collectList().block();
        assertThat(assuntoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAssunto() {
        // Initialize the database
        assuntoRepository.save(assunto).block();

        int databaseSizeBeforeDelete = assuntoRepository.findAll().collectList().block().size();

        // Delete the assunto
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, assunto.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Assunto> assuntoList = assuntoRepository.findAll().collectList().block();
        assertThat(assuntoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
