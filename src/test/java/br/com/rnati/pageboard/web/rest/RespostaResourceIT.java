package br.com.rnati.pageboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import br.com.rnati.pageboard.IntegrationTest;
import br.com.rnati.pageboard.domain.Resposta;
import br.com.rnati.pageboard.repository.EntityManager;
import br.com.rnati.pageboard.repository.RespostaRepository;
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
 * Integration tests for the {@link RespostaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class RespostaResourceIT {

    private static final String DEFAULT_TEXTO = "AAAAAAAAAA";
    private static final String UPDATED_TEXTO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/respostas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private RespostaRepository respostaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Resposta resposta;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resposta createEntity(EntityManager em) {
        Resposta resposta = new Resposta().texto(DEFAULT_TEXTO);
        return resposta;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Resposta createUpdatedEntity(EntityManager em) {
        Resposta resposta = new Resposta().texto(UPDATED_TEXTO);
        return resposta;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Resposta.class).block();
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
        resposta = createEntity(em);
    }

    @Test
    void createResposta() throws Exception {
        int databaseSizeBeforeCreate = respostaRepository.findAll().collectList().block().size();
        // Create the Resposta
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resposta))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Resposta in the database
        List<Resposta> respostaList = respostaRepository.findAll().collectList().block();
        assertThat(respostaList).hasSize(databaseSizeBeforeCreate + 1);
        Resposta testResposta = respostaList.get(respostaList.size() - 1);
        assertThat(testResposta.getTexto()).isEqualTo(DEFAULT_TEXTO);
    }

    @Test
    void createRespostaWithExistingId() throws Exception {
        // Create the Resposta with an existing ID
        resposta.setId(1L);

        int databaseSizeBeforeCreate = respostaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resposta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Resposta in the database
        List<Resposta> respostaList = respostaRepository.findAll().collectList().block();
        assertThat(respostaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllRespostasAsStream() {
        // Initialize the database
        respostaRepository.save(resposta).block();

        List<Resposta> respostaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Resposta.class)
            .getResponseBody()
            .filter(resposta::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(respostaList).isNotNull();
        assertThat(respostaList).hasSize(1);
        Resposta testResposta = respostaList.get(0);
        assertThat(testResposta.getTexto()).isEqualTo(DEFAULT_TEXTO);
    }

    @Test
    void getAllRespostas() {
        // Initialize the database
        respostaRepository.save(resposta).block();

        // Get all the respostaList
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
            .value(hasItem(resposta.getId().intValue()))
            .jsonPath("$.[*].texto")
            .value(hasItem(DEFAULT_TEXTO.toString()));
    }

    @Test
    void getResposta() {
        // Initialize the database
        respostaRepository.save(resposta).block();

        // Get the resposta
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, resposta.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(resposta.getId().intValue()))
            .jsonPath("$.texto")
            .value(is(DEFAULT_TEXTO.toString()));
    }

    @Test
    void getNonExistingResposta() {
        // Get the resposta
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewResposta() throws Exception {
        // Initialize the database
        respostaRepository.save(resposta).block();

        int databaseSizeBeforeUpdate = respostaRepository.findAll().collectList().block().size();

        // Update the resposta
        Resposta updatedResposta = respostaRepository.findById(resposta.getId()).block();
        updatedResposta.texto(UPDATED_TEXTO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedResposta.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedResposta))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Resposta in the database
        List<Resposta> respostaList = respostaRepository.findAll().collectList().block();
        assertThat(respostaList).hasSize(databaseSizeBeforeUpdate);
        Resposta testResposta = respostaList.get(respostaList.size() - 1);
        assertThat(testResposta.getTexto()).isEqualTo(UPDATED_TEXTO);
    }

    @Test
    void putNonExistingResposta() throws Exception {
        int databaseSizeBeforeUpdate = respostaRepository.findAll().collectList().block().size();
        resposta.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, resposta.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resposta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Resposta in the database
        List<Resposta> respostaList = respostaRepository.findAll().collectList().block();
        assertThat(respostaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchResposta() throws Exception {
        int databaseSizeBeforeUpdate = respostaRepository.findAll().collectList().block().size();
        resposta.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resposta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Resposta in the database
        List<Resposta> respostaList = respostaRepository.findAll().collectList().block();
        assertThat(respostaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamResposta() throws Exception {
        int databaseSizeBeforeUpdate = respostaRepository.findAll().collectList().block().size();
        resposta.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(resposta))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Resposta in the database
        List<Resposta> respostaList = respostaRepository.findAll().collectList().block();
        assertThat(respostaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateRespostaWithPatch() throws Exception {
        // Initialize the database
        respostaRepository.save(resposta).block();

        int databaseSizeBeforeUpdate = respostaRepository.findAll().collectList().block().size();

        // Update the resposta using partial update
        Resposta partialUpdatedResposta = new Resposta();
        partialUpdatedResposta.setId(resposta.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedResposta.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedResposta))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Resposta in the database
        List<Resposta> respostaList = respostaRepository.findAll().collectList().block();
        assertThat(respostaList).hasSize(databaseSizeBeforeUpdate);
        Resposta testResposta = respostaList.get(respostaList.size() - 1);
        assertThat(testResposta.getTexto()).isEqualTo(DEFAULT_TEXTO);
    }

    @Test
    void fullUpdateRespostaWithPatch() throws Exception {
        // Initialize the database
        respostaRepository.save(resposta).block();

        int databaseSizeBeforeUpdate = respostaRepository.findAll().collectList().block().size();

        // Update the resposta using partial update
        Resposta partialUpdatedResposta = new Resposta();
        partialUpdatedResposta.setId(resposta.getId());

        partialUpdatedResposta.texto(UPDATED_TEXTO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedResposta.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedResposta))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Resposta in the database
        List<Resposta> respostaList = respostaRepository.findAll().collectList().block();
        assertThat(respostaList).hasSize(databaseSizeBeforeUpdate);
        Resposta testResposta = respostaList.get(respostaList.size() - 1);
        assertThat(testResposta.getTexto()).isEqualTo(UPDATED_TEXTO);
    }

    @Test
    void patchNonExistingResposta() throws Exception {
        int databaseSizeBeforeUpdate = respostaRepository.findAll().collectList().block().size();
        resposta.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, resposta.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(resposta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Resposta in the database
        List<Resposta> respostaList = respostaRepository.findAll().collectList().block();
        assertThat(respostaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchResposta() throws Exception {
        int databaseSizeBeforeUpdate = respostaRepository.findAll().collectList().block().size();
        resposta.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(resposta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Resposta in the database
        List<Resposta> respostaList = respostaRepository.findAll().collectList().block();
        assertThat(respostaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamResposta() throws Exception {
        int databaseSizeBeforeUpdate = respostaRepository.findAll().collectList().block().size();
        resposta.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(resposta))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Resposta in the database
        List<Resposta> respostaList = respostaRepository.findAll().collectList().block();
        assertThat(respostaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteResposta() {
        // Initialize the database
        respostaRepository.save(resposta).block();

        int databaseSizeBeforeDelete = respostaRepository.findAll().collectList().block().size();

        // Delete the resposta
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, resposta.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Resposta> respostaList = respostaRepository.findAll().collectList().block();
        assertThat(respostaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
