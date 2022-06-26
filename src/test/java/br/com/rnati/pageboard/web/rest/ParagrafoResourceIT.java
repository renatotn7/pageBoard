package br.com.rnati.pageboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import br.com.rnati.pageboard.IntegrationTest;
import br.com.rnati.pageboard.domain.Paragrafo;
import br.com.rnati.pageboard.repository.EntityManager;
import br.com.rnati.pageboard.repository.ParagrafoRepository;
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
 * Integration tests for the {@link ParagrafoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ParagrafoResourceIT {

    private static final Integer DEFAULT_NUMERO = 1;
    private static final Integer UPDATED_NUMERO = 2;

    private static final String DEFAULT_TEXTO = "AAAAAAAAAA";
    private static final String UPDATED_TEXTO = "BBBBBBBBBB";

    private static final String DEFAULT_RESUMO = "AAAAAAAAAA";
    private static final String UPDATED_RESUMO = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/paragrafos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ParagrafoRepository paragrafoRepository;

    @Mock
    private ParagrafoRepository paragrafoRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Paragrafo paragrafo;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Paragrafo createEntity(EntityManager em) {
        Paragrafo paragrafo = new Paragrafo().numero(DEFAULT_NUMERO).texto(DEFAULT_TEXTO).resumo(DEFAULT_RESUMO);
        return paragrafo;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Paragrafo createUpdatedEntity(EntityManager em) {
        Paragrafo paragrafo = new Paragrafo().numero(UPDATED_NUMERO).texto(UPDATED_TEXTO).resumo(UPDATED_RESUMO);
        return paragrafo;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Paragrafo.class).block();
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
        paragrafo = createEntity(em);
    }

    @Test
    void createParagrafo() throws Exception {
        int databaseSizeBeforeCreate = paragrafoRepository.findAll().collectList().block().size();
        // Create the Paragrafo
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paragrafo))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Paragrafo in the database
        List<Paragrafo> paragrafoList = paragrafoRepository.findAll().collectList().block();
        assertThat(paragrafoList).hasSize(databaseSizeBeforeCreate + 1);
        Paragrafo testParagrafo = paragrafoList.get(paragrafoList.size() - 1);
        assertThat(testParagrafo.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testParagrafo.getTexto()).isEqualTo(DEFAULT_TEXTO);
        assertThat(testParagrafo.getResumo()).isEqualTo(DEFAULT_RESUMO);
    }

    @Test
    void createParagrafoWithExistingId() throws Exception {
        // Create the Paragrafo with an existing ID
        paragrafo.setId(1L);

        int databaseSizeBeforeCreate = paragrafoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paragrafo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Paragrafo in the database
        List<Paragrafo> paragrafoList = paragrafoRepository.findAll().collectList().block();
        assertThat(paragrafoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllParagrafosAsStream() {
        // Initialize the database
        paragrafoRepository.save(paragrafo).block();

        List<Paragrafo> paragrafoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Paragrafo.class)
            .getResponseBody()
            .filter(paragrafo::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(paragrafoList).isNotNull();
        assertThat(paragrafoList).hasSize(1);
        Paragrafo testParagrafo = paragrafoList.get(0);
        assertThat(testParagrafo.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testParagrafo.getTexto()).isEqualTo(DEFAULT_TEXTO);
        assertThat(testParagrafo.getResumo()).isEqualTo(DEFAULT_RESUMO);
    }

    @Test
    void getAllParagrafos() {
        // Initialize the database
        paragrafoRepository.save(paragrafo).block();

        // Get all the paragrafoList
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
            .value(hasItem(paragrafo.getId().intValue()))
            .jsonPath("$.[*].numero")
            .value(hasItem(DEFAULT_NUMERO))
            .jsonPath("$.[*].texto")
            .value(hasItem(DEFAULT_TEXTO.toString()))
            .jsonPath("$.[*].resumo")
            .value(hasItem(DEFAULT_RESUMO.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllParagrafosWithEagerRelationshipsIsEnabled() {
        when(paragrafoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(paragrafoRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllParagrafosWithEagerRelationshipsIsNotEnabled() {
        when(paragrafoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(paragrafoRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getParagrafo() {
        // Initialize the database
        paragrafoRepository.save(paragrafo).block();

        // Get the paragrafo
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, paragrafo.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(paragrafo.getId().intValue()))
            .jsonPath("$.numero")
            .value(is(DEFAULT_NUMERO))
            .jsonPath("$.texto")
            .value(is(DEFAULT_TEXTO.toString()))
            .jsonPath("$.resumo")
            .value(is(DEFAULT_RESUMO.toString()));
    }

    @Test
    void getNonExistingParagrafo() {
        // Get the paragrafo
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewParagrafo() throws Exception {
        // Initialize the database
        paragrafoRepository.save(paragrafo).block();

        int databaseSizeBeforeUpdate = paragrafoRepository.findAll().collectList().block().size();

        // Update the paragrafo
        Paragrafo updatedParagrafo = paragrafoRepository.findById(paragrafo.getId()).block();
        updatedParagrafo.numero(UPDATED_NUMERO).texto(UPDATED_TEXTO).resumo(UPDATED_RESUMO);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedParagrafo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedParagrafo))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Paragrafo in the database
        List<Paragrafo> paragrafoList = paragrafoRepository.findAll().collectList().block();
        assertThat(paragrafoList).hasSize(databaseSizeBeforeUpdate);
        Paragrafo testParagrafo = paragrafoList.get(paragrafoList.size() - 1);
        assertThat(testParagrafo.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testParagrafo.getTexto()).isEqualTo(UPDATED_TEXTO);
        assertThat(testParagrafo.getResumo()).isEqualTo(UPDATED_RESUMO);
    }

    @Test
    void putNonExistingParagrafo() throws Exception {
        int databaseSizeBeforeUpdate = paragrafoRepository.findAll().collectList().block().size();
        paragrafo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, paragrafo.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paragrafo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Paragrafo in the database
        List<Paragrafo> paragrafoList = paragrafoRepository.findAll().collectList().block();
        assertThat(paragrafoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchParagrafo() throws Exception {
        int databaseSizeBeforeUpdate = paragrafoRepository.findAll().collectList().block().size();
        paragrafo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paragrafo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Paragrafo in the database
        List<Paragrafo> paragrafoList = paragrafoRepository.findAll().collectList().block();
        assertThat(paragrafoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamParagrafo() throws Exception {
        int databaseSizeBeforeUpdate = paragrafoRepository.findAll().collectList().block().size();
        paragrafo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(paragrafo))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Paragrafo in the database
        List<Paragrafo> paragrafoList = paragrafoRepository.findAll().collectList().block();
        assertThat(paragrafoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateParagrafoWithPatch() throws Exception {
        // Initialize the database
        paragrafoRepository.save(paragrafo).block();

        int databaseSizeBeforeUpdate = paragrafoRepository.findAll().collectList().block().size();

        // Update the paragrafo using partial update
        Paragrafo partialUpdatedParagrafo = new Paragrafo();
        partialUpdatedParagrafo.setId(paragrafo.getId());

        partialUpdatedParagrafo.texto(UPDATED_TEXTO).resumo(UPDATED_RESUMO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedParagrafo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedParagrafo))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Paragrafo in the database
        List<Paragrafo> paragrafoList = paragrafoRepository.findAll().collectList().block();
        assertThat(paragrafoList).hasSize(databaseSizeBeforeUpdate);
        Paragrafo testParagrafo = paragrafoList.get(paragrafoList.size() - 1);
        assertThat(testParagrafo.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testParagrafo.getTexto()).isEqualTo(UPDATED_TEXTO);
        assertThat(testParagrafo.getResumo()).isEqualTo(UPDATED_RESUMO);
    }

    @Test
    void fullUpdateParagrafoWithPatch() throws Exception {
        // Initialize the database
        paragrafoRepository.save(paragrafo).block();

        int databaseSizeBeforeUpdate = paragrafoRepository.findAll().collectList().block().size();

        // Update the paragrafo using partial update
        Paragrafo partialUpdatedParagrafo = new Paragrafo();
        partialUpdatedParagrafo.setId(paragrafo.getId());

        partialUpdatedParagrafo.numero(UPDATED_NUMERO).texto(UPDATED_TEXTO).resumo(UPDATED_RESUMO);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedParagrafo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedParagrafo))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Paragrafo in the database
        List<Paragrafo> paragrafoList = paragrafoRepository.findAll().collectList().block();
        assertThat(paragrafoList).hasSize(databaseSizeBeforeUpdate);
        Paragrafo testParagrafo = paragrafoList.get(paragrafoList.size() - 1);
        assertThat(testParagrafo.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testParagrafo.getTexto()).isEqualTo(UPDATED_TEXTO);
        assertThat(testParagrafo.getResumo()).isEqualTo(UPDATED_RESUMO);
    }

    @Test
    void patchNonExistingParagrafo() throws Exception {
        int databaseSizeBeforeUpdate = paragrafoRepository.findAll().collectList().block().size();
        paragrafo.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, paragrafo.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paragrafo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Paragrafo in the database
        List<Paragrafo> paragrafoList = paragrafoRepository.findAll().collectList().block();
        assertThat(paragrafoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchParagrafo() throws Exception {
        int databaseSizeBeforeUpdate = paragrafoRepository.findAll().collectList().block().size();
        paragrafo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paragrafo))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Paragrafo in the database
        List<Paragrafo> paragrafoList = paragrafoRepository.findAll().collectList().block();
        assertThat(paragrafoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamParagrafo() throws Exception {
        int databaseSizeBeforeUpdate = paragrafoRepository.findAll().collectList().block().size();
        paragrafo.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(paragrafo))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Paragrafo in the database
        List<Paragrafo> paragrafoList = paragrafoRepository.findAll().collectList().block();
        assertThat(paragrafoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteParagrafo() {
        // Initialize the database
        paragrafoRepository.save(paragrafo).block();

        int databaseSizeBeforeDelete = paragrafoRepository.findAll().collectList().block().size();

        // Delete the paragrafo
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, paragrafo.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Paragrafo> paragrafoList = paragrafoRepository.findAll().collectList().block();
        assertThat(paragrafoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
