package br.com.rnati.pageboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import br.com.rnati.pageboard.IntegrationTest;
import br.com.rnati.pageboard.domain.Estado;
import br.com.rnati.pageboard.repository.EntityManager;
import br.com.rnati.pageboard.repository.EstadoRepository;
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
 * Integration tests for the {@link EstadoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EstadoResourceIT {

    private static final String DEFAULT_NOME = "AAAAAAAAAA";
    private static final String UPDATED_NOME = "BBBBBBBBBB";

    private static final String DEFAULT_UF = "AAAAAAAAAA";
    private static final String UPDATED_UF = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/estados";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EstadoRepository estadoRepository;

    @Mock
    private EstadoRepository estadoRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Estado estado;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Estado createEntity(EntityManager em) {
        Estado estado = new Estado().nome(DEFAULT_NOME).uf(DEFAULT_UF);
        return estado;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Estado createUpdatedEntity(EntityManager em) {
        Estado estado = new Estado().nome(UPDATED_NOME).uf(UPDATED_UF);
        return estado;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Estado.class).block();
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
        estado = createEntity(em);
    }

    @Test
    void createEstado() throws Exception {
        int databaseSizeBeforeCreate = estadoRepository.findAll().collectList().block().size();
        // Create the Estado
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(estado))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Estado in the database
        List<Estado> estadoList = estadoRepository.findAll().collectList().block();
        assertThat(estadoList).hasSize(databaseSizeBeforeCreate + 1);
        Estado testEstado = estadoList.get(estadoList.size() - 1);
        assertThat(testEstado.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testEstado.getUf()).isEqualTo(DEFAULT_UF);
    }

    @Test
    void createEstadoWithExistingId() throws Exception {
        // Create the Estado with an existing ID
        estado.setId(1L);

        int databaseSizeBeforeCreate = estadoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(estado))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Estado in the database
        List<Estado> estadoList = estadoRepository.findAll().collectList().block();
        assertThat(estadoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllEstadosAsStream() {
        // Initialize the database
        estadoRepository.save(estado).block();

        List<Estado> estadoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Estado.class)
            .getResponseBody()
            .filter(estado::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(estadoList).isNotNull();
        assertThat(estadoList).hasSize(1);
        Estado testEstado = estadoList.get(0);
        assertThat(testEstado.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testEstado.getUf()).isEqualTo(DEFAULT_UF);
    }

    @Test
    void getAllEstados() {
        // Initialize the database
        estadoRepository.save(estado).block();

        // Get all the estadoList
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
            .value(hasItem(estado.getId().intValue()))
            .jsonPath("$.[*].nome")
            .value(hasItem(DEFAULT_NOME))
            .jsonPath("$.[*].uf")
            .value(hasItem(DEFAULT_UF));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEstadosWithEagerRelationshipsIsEnabled() {
        when(estadoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(estadoRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEstadosWithEagerRelationshipsIsNotEnabled() {
        when(estadoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(estadoRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getEstado() {
        // Initialize the database
        estadoRepository.save(estado).block();

        // Get the estado
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, estado.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(estado.getId().intValue()))
            .jsonPath("$.nome")
            .value(is(DEFAULT_NOME))
            .jsonPath("$.uf")
            .value(is(DEFAULT_UF));
    }

    @Test
    void getNonExistingEstado() {
        // Get the estado
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewEstado() throws Exception {
        // Initialize the database
        estadoRepository.save(estado).block();

        int databaseSizeBeforeUpdate = estadoRepository.findAll().collectList().block().size();

        // Update the estado
        Estado updatedEstado = estadoRepository.findById(estado.getId()).block();
        updatedEstado.nome(UPDATED_NOME).uf(UPDATED_UF);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEstado.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedEstado))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Estado in the database
        List<Estado> estadoList = estadoRepository.findAll().collectList().block();
        assertThat(estadoList).hasSize(databaseSizeBeforeUpdate);
        Estado testEstado = estadoList.get(estadoList.size() - 1);
        assertThat(testEstado.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testEstado.getUf()).isEqualTo(UPDATED_UF);
    }

    @Test
    void putNonExistingEstado() throws Exception {
        int databaseSizeBeforeUpdate = estadoRepository.findAll().collectList().block().size();
        estado.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, estado.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(estado))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Estado in the database
        List<Estado> estadoList = estadoRepository.findAll().collectList().block();
        assertThat(estadoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEstado() throws Exception {
        int databaseSizeBeforeUpdate = estadoRepository.findAll().collectList().block().size();
        estado.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(estado))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Estado in the database
        List<Estado> estadoList = estadoRepository.findAll().collectList().block();
        assertThat(estadoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEstado() throws Exception {
        int databaseSizeBeforeUpdate = estadoRepository.findAll().collectList().block().size();
        estado.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(estado))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Estado in the database
        List<Estado> estadoList = estadoRepository.findAll().collectList().block();
        assertThat(estadoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEstadoWithPatch() throws Exception {
        // Initialize the database
        estadoRepository.save(estado).block();

        int databaseSizeBeforeUpdate = estadoRepository.findAll().collectList().block().size();

        // Update the estado using partial update
        Estado partialUpdatedEstado = new Estado();
        partialUpdatedEstado.setId(estado.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEstado.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEstado))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Estado in the database
        List<Estado> estadoList = estadoRepository.findAll().collectList().block();
        assertThat(estadoList).hasSize(databaseSizeBeforeUpdate);
        Estado testEstado = estadoList.get(estadoList.size() - 1);
        assertThat(testEstado.getNome()).isEqualTo(DEFAULT_NOME);
        assertThat(testEstado.getUf()).isEqualTo(DEFAULT_UF);
    }

    @Test
    void fullUpdateEstadoWithPatch() throws Exception {
        // Initialize the database
        estadoRepository.save(estado).block();

        int databaseSizeBeforeUpdate = estadoRepository.findAll().collectList().block().size();

        // Update the estado using partial update
        Estado partialUpdatedEstado = new Estado();
        partialUpdatedEstado.setId(estado.getId());

        partialUpdatedEstado.nome(UPDATED_NOME).uf(UPDATED_UF);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEstado.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEstado))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Estado in the database
        List<Estado> estadoList = estadoRepository.findAll().collectList().block();
        assertThat(estadoList).hasSize(databaseSizeBeforeUpdate);
        Estado testEstado = estadoList.get(estadoList.size() - 1);
        assertThat(testEstado.getNome()).isEqualTo(UPDATED_NOME);
        assertThat(testEstado.getUf()).isEqualTo(UPDATED_UF);
    }

    @Test
    void patchNonExistingEstado() throws Exception {
        int databaseSizeBeforeUpdate = estadoRepository.findAll().collectList().block().size();
        estado.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, estado.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(estado))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Estado in the database
        List<Estado> estadoList = estadoRepository.findAll().collectList().block();
        assertThat(estadoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEstado() throws Exception {
        int databaseSizeBeforeUpdate = estadoRepository.findAll().collectList().block().size();
        estado.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(estado))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Estado in the database
        List<Estado> estadoList = estadoRepository.findAll().collectList().block();
        assertThat(estadoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEstado() throws Exception {
        int databaseSizeBeforeUpdate = estadoRepository.findAll().collectList().block().size();
        estado.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(estado))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Estado in the database
        List<Estado> estadoList = estadoRepository.findAll().collectList().block();
        assertThat(estadoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEstado() {
        // Initialize the database
        estadoRepository.save(estado).block();

        int databaseSizeBeforeDelete = estadoRepository.findAll().collectList().block().size();

        // Delete the estado
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, estado.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Estado> estadoList = estadoRepository.findAll().collectList().block();
        assertThat(estadoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
