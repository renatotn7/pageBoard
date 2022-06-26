package br.com.rnati.pageboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import br.com.rnati.pageboard.IntegrationTest;
import br.com.rnati.pageboard.domain.Pergunta;
import br.com.rnati.pageboard.repository.EntityManager;
import br.com.rnati.pageboard.repository.PerguntaRepository;
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
 * Integration tests for the {@link PerguntaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PerguntaResourceIT {

    private static final String DEFAULT_QUESTAO = "AAAAAAAAAA";
    private static final String UPDATED_QUESTAO = "BBBBBBBBBB";

    private static final String DEFAULT_RESPOSTA = "AAAAAAAAAA";
    private static final String UPDATED_RESPOSTA = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/perguntas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PerguntaRepository perguntaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Pergunta pergunta;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pergunta createEntity(EntityManager em) {
        Pergunta pergunta = new Pergunta().questao(DEFAULT_QUESTAO).resposta(DEFAULT_RESPOSTA);
        return pergunta;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pergunta createUpdatedEntity(EntityManager em) {
        Pergunta pergunta = new Pergunta().questao(UPDATED_QUESTAO).resposta(UPDATED_RESPOSTA);
        return pergunta;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Pergunta.class).block();
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
        pergunta = createEntity(em);
    }

    @Test
    void createPergunta() throws Exception {
        int databaseSizeBeforeCreate = perguntaRepository.findAll().collectList().block().size();
        // Create the Pergunta
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pergunta))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Pergunta in the database
        List<Pergunta> perguntaList = perguntaRepository.findAll().collectList().block();
        assertThat(perguntaList).hasSize(databaseSizeBeforeCreate + 1);
        Pergunta testPergunta = perguntaList.get(perguntaList.size() - 1);
        assertThat(testPergunta.getQuestao()).isEqualTo(DEFAULT_QUESTAO);
        assertThat(testPergunta.getResposta()).isEqualTo(DEFAULT_RESPOSTA);
    }

    @Test
    void createPerguntaWithExistingId() throws Exception {
        // Create the Pergunta with an existing ID
        pergunta.setId(1L);

        int databaseSizeBeforeCreate = perguntaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pergunta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pergunta in the database
        List<Pergunta> perguntaList = perguntaRepository.findAll().collectList().block();
        assertThat(perguntaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPerguntasAsStream() {
        // Initialize the database
        perguntaRepository.save(pergunta).block();

        List<Pergunta> perguntaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Pergunta.class)
            .getResponseBody()
            .filter(pergunta::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(perguntaList).isNotNull();
        assertThat(perguntaList).hasSize(1);
        Pergunta testPergunta = perguntaList.get(0);
        assertThat(testPergunta.getQuestao()).isEqualTo(DEFAULT_QUESTAO);
        assertThat(testPergunta.getResposta()).isEqualTo(DEFAULT_RESPOSTA);
    }

    @Test
    void getAllPerguntas() {
        // Initialize the database
        perguntaRepository.save(pergunta).block();

        // Get all the perguntaList
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
            .value(hasItem(pergunta.getId().intValue()))
            .jsonPath("$.[*].questao")
            .value(hasItem(DEFAULT_QUESTAO.toString()))
            .jsonPath("$.[*].resposta")
            .value(hasItem(DEFAULT_RESPOSTA.toString()));
    }

    @Test
    void getPergunta() {
        // Initialize the database
        perguntaRepository.save(pergunta).block();

        // Get the pergunta
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pergunta.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pergunta.getId().intValue()))
            .jsonPath("$.questao")
            .value(is(DEFAULT_QUESTAO.toString()))
            .jsonPath("$.resposta")
            .value(is(DEFAULT_RESPOSTA.toString()));
    }

    @Test
    void getNonExistingPergunta() {
        // Get the pergunta
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPergunta() throws Exception {
        // Initialize the database
        perguntaRepository.save(pergunta).block();

        int databaseSizeBeforeUpdate = perguntaRepository.findAll().collectList().block().size();

        // Update the pergunta
        Pergunta updatedPergunta = perguntaRepository.findById(pergunta.getId()).block();
        updatedPergunta.questao(UPDATED_QUESTAO).resposta(UPDATED_RESPOSTA);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPergunta.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPergunta))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pergunta in the database
        List<Pergunta> perguntaList = perguntaRepository.findAll().collectList().block();
        assertThat(perguntaList).hasSize(databaseSizeBeforeUpdate);
        Pergunta testPergunta = perguntaList.get(perguntaList.size() - 1);
        assertThat(testPergunta.getQuestao()).isEqualTo(UPDATED_QUESTAO);
        assertThat(testPergunta.getResposta()).isEqualTo(UPDATED_RESPOSTA);
    }

    @Test
    void putNonExistingPergunta() throws Exception {
        int databaseSizeBeforeUpdate = perguntaRepository.findAll().collectList().block().size();
        pergunta.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pergunta.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pergunta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pergunta in the database
        List<Pergunta> perguntaList = perguntaRepository.findAll().collectList().block();
        assertThat(perguntaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPergunta() throws Exception {
        int databaseSizeBeforeUpdate = perguntaRepository.findAll().collectList().block().size();
        pergunta.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pergunta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pergunta in the database
        List<Pergunta> perguntaList = perguntaRepository.findAll().collectList().block();
        assertThat(perguntaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPergunta() throws Exception {
        int databaseSizeBeforeUpdate = perguntaRepository.findAll().collectList().block().size();
        pergunta.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pergunta))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pergunta in the database
        List<Pergunta> perguntaList = perguntaRepository.findAll().collectList().block();
        assertThat(perguntaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePerguntaWithPatch() throws Exception {
        // Initialize the database
        perguntaRepository.save(pergunta).block();

        int databaseSizeBeforeUpdate = perguntaRepository.findAll().collectList().block().size();

        // Update the pergunta using partial update
        Pergunta partialUpdatedPergunta = new Pergunta();
        partialUpdatedPergunta.setId(pergunta.getId());

        partialUpdatedPergunta.questao(UPDATED_QUESTAO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPergunta.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPergunta))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pergunta in the database
        List<Pergunta> perguntaList = perguntaRepository.findAll().collectList().block();
        assertThat(perguntaList).hasSize(databaseSizeBeforeUpdate);
        Pergunta testPergunta = perguntaList.get(perguntaList.size() - 1);
        assertThat(testPergunta.getQuestao()).isEqualTo(UPDATED_QUESTAO);
        assertThat(testPergunta.getResposta()).isEqualTo(DEFAULT_RESPOSTA);
    }

    @Test
    void fullUpdatePerguntaWithPatch() throws Exception {
        // Initialize the database
        perguntaRepository.save(pergunta).block();

        int databaseSizeBeforeUpdate = perguntaRepository.findAll().collectList().block().size();

        // Update the pergunta using partial update
        Pergunta partialUpdatedPergunta = new Pergunta();
        partialUpdatedPergunta.setId(pergunta.getId());

        partialUpdatedPergunta.questao(UPDATED_QUESTAO).resposta(UPDATED_RESPOSTA);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPergunta.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPergunta))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pergunta in the database
        List<Pergunta> perguntaList = perguntaRepository.findAll().collectList().block();
        assertThat(perguntaList).hasSize(databaseSizeBeforeUpdate);
        Pergunta testPergunta = perguntaList.get(perguntaList.size() - 1);
        assertThat(testPergunta.getQuestao()).isEqualTo(UPDATED_QUESTAO);
        assertThat(testPergunta.getResposta()).isEqualTo(UPDATED_RESPOSTA);
    }

    @Test
    void patchNonExistingPergunta() throws Exception {
        int databaseSizeBeforeUpdate = perguntaRepository.findAll().collectList().block().size();
        pergunta.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pergunta.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pergunta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pergunta in the database
        List<Pergunta> perguntaList = perguntaRepository.findAll().collectList().block();
        assertThat(perguntaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPergunta() throws Exception {
        int databaseSizeBeforeUpdate = perguntaRepository.findAll().collectList().block().size();
        pergunta.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pergunta))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pergunta in the database
        List<Pergunta> perguntaList = perguntaRepository.findAll().collectList().block();
        assertThat(perguntaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPergunta() throws Exception {
        int databaseSizeBeforeUpdate = perguntaRepository.findAll().collectList().block().size();
        pergunta.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pergunta))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pergunta in the database
        List<Pergunta> perguntaList = perguntaRepository.findAll().collectList().block();
        assertThat(perguntaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePergunta() {
        // Initialize the database
        perguntaRepository.save(pergunta).block();

        int databaseSizeBeforeDelete = perguntaRepository.findAll().collectList().block().size();

        // Delete the pergunta
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pergunta.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Pergunta> perguntaList = perguntaRepository.findAll().collectList().block();
        assertThat(perguntaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
