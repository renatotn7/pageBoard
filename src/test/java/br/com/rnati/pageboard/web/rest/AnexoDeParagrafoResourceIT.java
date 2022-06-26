package br.com.rnati.pageboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import br.com.rnati.pageboard.IntegrationTest;
import br.com.rnati.pageboard.domain.AnexoDeParagrafo;
import br.com.rnati.pageboard.domain.enumeration.TipoAnexoDeParagrafo;
import br.com.rnati.pageboard.repository.AnexoDeParagrafoRepository;
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
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link AnexoDeParagrafoResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class AnexoDeParagrafoResourceIT {

    private static final TipoAnexoDeParagrafo DEFAULT_TIPO = TipoAnexoDeParagrafo.EXPLICACAO;
    private static final TipoAnexoDeParagrafo UPDATED_TIPO = TipoAnexoDeParagrafo.SUMARIO;

    private static final String DEFAULT_VALUE = "AAAAAAAAAA";
    private static final String UPDATED_VALUE = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/anexo-de-paragrafos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private AnexoDeParagrafoRepository anexoDeParagrafoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private AnexoDeParagrafo anexoDeParagrafo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AnexoDeParagrafo createEntity(EntityManager em) {
        AnexoDeParagrafo anexoDeParagrafo = new AnexoDeParagrafo().tipo(DEFAULT_TIPO).value(DEFAULT_VALUE);
        return anexoDeParagrafo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static AnexoDeParagrafo createUpdatedEntity(EntityManager em) {
        AnexoDeParagrafo anexoDeParagrafo = new AnexoDeParagrafo().tipo(UPDATED_TIPO).value(UPDATED_VALUE);
        return anexoDeParagrafo;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(AnexoDeParagrafo.class).block();
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
        anexoDeParagrafo = createEntity(em);
    }

    @Test
    void createAnexoDeParagrafo() throws Exception {
        int databaseSizeBeforeCreate = anexoDeParagrafoRepository.findAll().collectList().block().size();
        // Create the AnexoDeParagrafo
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(anexoDeParagrafo))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the AnexoDeParagrafo in the database
        List<AnexoDeParagrafo> anexoDeParagrafoList = anexoDeParagrafoRepository.findAll().collectList().block();
        assertThat(anexoDeParagrafoList).hasSize(databaseSizeBeforeCreate + 1);
        AnexoDeParagrafo testAnexoDeParagrafo = anexoDeParagrafoList.get(anexoDeParagrafoList.size() - 1);
        assertThat(testAnexoDeParagrafo.getTipo()).isEqualTo(DEFAULT_TIPO);
        assertThat(testAnexoDeParagrafo.getValue()).isEqualTo(DEFAULT_VALUE);
    }

    @Test
    void createAnexoDeParagrafoWithExistingId() throws Exception {
        // Create the AnexoDeParagrafo with an existing ID
        anexoDeParagrafo.setId(1L);

        int databaseSizeBeforeCreate = anexoDeParagrafoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(anexoDeParagrafo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AnexoDeParagrafo in the database
        List<AnexoDeParagrafo> anexoDeParagrafoList = anexoDeParagrafoRepository.findAll().collectList().block();
        assertThat(anexoDeParagrafoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllAnexoDeParagrafos() {
        // Initialize the database
        anexoDeParagrafoRepository.save(anexoDeParagrafo).block();

        // Get all the anexoDeParagrafoList
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
            .value(hasItem(anexoDeParagrafo.getId().intValue()))
            .jsonPath("$.[*].tipo")
            .value(hasItem(DEFAULT_TIPO.toString()))
            .jsonPath("$.[*].value")
            .value(hasItem(DEFAULT_VALUE.toString()));
    }

    @Test
    void getAnexoDeParagrafo() {
        // Initialize the database
        anexoDeParagrafoRepository.save(anexoDeParagrafo).block();

        // Get the anexoDeParagrafo
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, anexoDeParagrafo.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(anexoDeParagrafo.getId().intValue()))
            .jsonPath("$.tipo")
            .value(is(DEFAULT_TIPO.toString()))
            .jsonPath("$.value")
            .value(is(DEFAULT_VALUE.toString()));
    }

    @Test
    void getNonExistingAnexoDeParagrafo() {
        // Get the anexoDeParagrafo
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewAnexoDeParagrafo() throws Exception {
        // Initialize the database
        anexoDeParagrafoRepository.save(anexoDeParagrafo).block();

        int databaseSizeBeforeUpdate = anexoDeParagrafoRepository.findAll().collectList().block().size();

        // Update the anexoDeParagrafo
        AnexoDeParagrafo updatedAnexoDeParagrafo = anexoDeParagrafoRepository.findById(anexoDeParagrafo.getId()).block();
        updatedAnexoDeParagrafo.tipo(UPDATED_TIPO).value(UPDATED_VALUE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedAnexoDeParagrafo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedAnexoDeParagrafo))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AnexoDeParagrafo in the database
        List<AnexoDeParagrafo> anexoDeParagrafoList = anexoDeParagrafoRepository.findAll().collectList().block();
        assertThat(anexoDeParagrafoList).hasSize(databaseSizeBeforeUpdate);
        AnexoDeParagrafo testAnexoDeParagrafo = anexoDeParagrafoList.get(anexoDeParagrafoList.size() - 1);
        assertThat(testAnexoDeParagrafo.getTipo()).isEqualTo(UPDATED_TIPO);
        assertThat(testAnexoDeParagrafo.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    void putNonExistingAnexoDeParagrafo() throws Exception {
        int databaseSizeBeforeUpdate = anexoDeParagrafoRepository.findAll().collectList().block().size();
        anexoDeParagrafo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, anexoDeParagrafo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(anexoDeParagrafo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AnexoDeParagrafo in the database
        List<AnexoDeParagrafo> anexoDeParagrafoList = anexoDeParagrafoRepository.findAll().collectList().block();
        assertThat(anexoDeParagrafoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchAnexoDeParagrafo() throws Exception {
        int databaseSizeBeforeUpdate = anexoDeParagrafoRepository.findAll().collectList().block().size();
        anexoDeParagrafo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(anexoDeParagrafo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AnexoDeParagrafo in the database
        List<AnexoDeParagrafo> anexoDeParagrafoList = anexoDeParagrafoRepository.findAll().collectList().block();
        assertThat(anexoDeParagrafoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamAnexoDeParagrafo() throws Exception {
        int databaseSizeBeforeUpdate = anexoDeParagrafoRepository.findAll().collectList().block().size();
        anexoDeParagrafo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(anexoDeParagrafo))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AnexoDeParagrafo in the database
        List<AnexoDeParagrafo> anexoDeParagrafoList = anexoDeParagrafoRepository.findAll().collectList().block();
        assertThat(anexoDeParagrafoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateAnexoDeParagrafoWithPatch() throws Exception {
        // Initialize the database
        anexoDeParagrafoRepository.save(anexoDeParagrafo).block();

        int databaseSizeBeforeUpdate = anexoDeParagrafoRepository.findAll().collectList().block().size();

        // Update the anexoDeParagrafo using partial update
        AnexoDeParagrafo partialUpdatedAnexoDeParagrafo = new AnexoDeParagrafo();
        partialUpdatedAnexoDeParagrafo.setId(anexoDeParagrafo.getId());

        partialUpdatedAnexoDeParagrafo.value(UPDATED_VALUE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAnexoDeParagrafo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAnexoDeParagrafo))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AnexoDeParagrafo in the database
        List<AnexoDeParagrafo> anexoDeParagrafoList = anexoDeParagrafoRepository.findAll().collectList().block();
        assertThat(anexoDeParagrafoList).hasSize(databaseSizeBeforeUpdate);
        AnexoDeParagrafo testAnexoDeParagrafo = anexoDeParagrafoList.get(anexoDeParagrafoList.size() - 1);
        assertThat(testAnexoDeParagrafo.getTipo()).isEqualTo(DEFAULT_TIPO);
        assertThat(testAnexoDeParagrafo.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    void fullUpdateAnexoDeParagrafoWithPatch() throws Exception {
        // Initialize the database
        anexoDeParagrafoRepository.save(anexoDeParagrafo).block();

        int databaseSizeBeforeUpdate = anexoDeParagrafoRepository.findAll().collectList().block().size();

        // Update the anexoDeParagrafo using partial update
        AnexoDeParagrafo partialUpdatedAnexoDeParagrafo = new AnexoDeParagrafo();
        partialUpdatedAnexoDeParagrafo.setId(anexoDeParagrafo.getId());

        partialUpdatedAnexoDeParagrafo.tipo(UPDATED_TIPO).value(UPDATED_VALUE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedAnexoDeParagrafo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedAnexoDeParagrafo))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the AnexoDeParagrafo in the database
        List<AnexoDeParagrafo> anexoDeParagrafoList = anexoDeParagrafoRepository.findAll().collectList().block();
        assertThat(anexoDeParagrafoList).hasSize(databaseSizeBeforeUpdate);
        AnexoDeParagrafo testAnexoDeParagrafo = anexoDeParagrafoList.get(anexoDeParagrafoList.size() - 1);
        assertThat(testAnexoDeParagrafo.getTipo()).isEqualTo(UPDATED_TIPO);
        assertThat(testAnexoDeParagrafo.getValue()).isEqualTo(UPDATED_VALUE);
    }

    @Test
    void patchNonExistingAnexoDeParagrafo() throws Exception {
        int databaseSizeBeforeUpdate = anexoDeParagrafoRepository.findAll().collectList().block().size();
        anexoDeParagrafo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, anexoDeParagrafo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(anexoDeParagrafo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AnexoDeParagrafo in the database
        List<AnexoDeParagrafo> anexoDeParagrafoList = anexoDeParagrafoRepository.findAll().collectList().block();
        assertThat(anexoDeParagrafoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchAnexoDeParagrafo() throws Exception {
        int databaseSizeBeforeUpdate = anexoDeParagrafoRepository.findAll().collectList().block().size();
        anexoDeParagrafo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(anexoDeParagrafo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the AnexoDeParagrafo in the database
        List<AnexoDeParagrafo> anexoDeParagrafoList = anexoDeParagrafoRepository.findAll().collectList().block();
        assertThat(anexoDeParagrafoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamAnexoDeParagrafo() throws Exception {
        int databaseSizeBeforeUpdate = anexoDeParagrafoRepository.findAll().collectList().block().size();
        anexoDeParagrafo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(anexoDeParagrafo))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the AnexoDeParagrafo in the database
        List<AnexoDeParagrafo> anexoDeParagrafoList = anexoDeParagrafoRepository.findAll().collectList().block();
        assertThat(anexoDeParagrafoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteAnexoDeParagrafo() {
        // Initialize the database
        anexoDeParagrafoRepository.save(anexoDeParagrafo).block();

        int databaseSizeBeforeDelete = anexoDeParagrafoRepository.findAll().collectList().block().size();

        // Delete the anexoDeParagrafo
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, anexoDeParagrafo.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<AnexoDeParagrafo> anexoDeParagrafoList = anexoDeParagrafoRepository.findAll().collectList().block();
        assertThat(anexoDeParagrafoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
