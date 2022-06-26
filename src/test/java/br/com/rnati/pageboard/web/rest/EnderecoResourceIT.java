package br.com.rnati.pageboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;

import br.com.rnati.pageboard.IntegrationTest;
import br.com.rnati.pageboard.domain.Endereco;
import br.com.rnati.pageboard.repository.EnderecoRepository;
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
 * Integration tests for the {@link EnderecoResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EnderecoResourceIT {

    private static final String DEFAULT_LOGRADOURO = "AAAAAAAAAA";
    private static final String UPDATED_LOGRADOURO = "BBBBBBBBBB";

    private static final Integer DEFAULT_NUMERO = 1;
    private static final Integer UPDATED_NUMERO = 2;

    private static final String DEFAULT_COMPLEMENTO = "AAAAAAAAAA";
    private static final String UPDATED_COMPLEMENTO = "BBBBBBBBBB";

    private static final String DEFAULT_BAIRRO = "AAAAAAAAAA";
    private static final String UPDATED_BAIRRO = "BBBBBBBBBB";

    private static final String DEFAULT_C_EP = "AAAAAAAAAA";
    private static final String UPDATED_C_EP = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/enderecos";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Mock
    private EnderecoRepository enderecoRepositoryMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Endereco endereco;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Endereco createEntity(EntityManager em) {
        Endereco endereco = new Endereco()
            .logradouro(DEFAULT_LOGRADOURO)
            .numero(DEFAULT_NUMERO)
            .complemento(DEFAULT_COMPLEMENTO)
            .bairro(DEFAULT_BAIRRO)
            .cEP(DEFAULT_C_EP);
        return endereco;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Endereco createUpdatedEntity(EntityManager em) {
        Endereco endereco = new Endereco()
            .logradouro(UPDATED_LOGRADOURO)
            .numero(UPDATED_NUMERO)
            .complemento(UPDATED_COMPLEMENTO)
            .bairro(UPDATED_BAIRRO)
            .cEP(UPDATED_C_EP);
        return endereco;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Endereco.class).block();
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
        endereco = createEntity(em);
    }

    @Test
    void createEndereco() throws Exception {
        int databaseSizeBeforeCreate = enderecoRepository.findAll().collectList().block().size();
        // Create the Endereco
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(endereco))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll().collectList().block();
        assertThat(enderecoList).hasSize(databaseSizeBeforeCreate + 1);
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getLogradouro()).isEqualTo(DEFAULT_LOGRADOURO);
        assertThat(testEndereco.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testEndereco.getComplemento()).isEqualTo(DEFAULT_COMPLEMENTO);
        assertThat(testEndereco.getBairro()).isEqualTo(DEFAULT_BAIRRO);
        assertThat(testEndereco.getcEP()).isEqualTo(DEFAULT_C_EP);
    }

    @Test
    void createEnderecoWithExistingId() throws Exception {
        // Create the Endereco with an existing ID
        endereco.setId(1L);

        int databaseSizeBeforeCreate = enderecoRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(endereco))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll().collectList().block();
        assertThat(enderecoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllEnderecosAsStream() {
        // Initialize the database
        enderecoRepository.save(endereco).block();

        List<Endereco> enderecoList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Endereco.class)
            .getResponseBody()
            .filter(endereco::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(enderecoList).isNotNull();
        assertThat(enderecoList).hasSize(1);
        Endereco testEndereco = enderecoList.get(0);
        assertThat(testEndereco.getLogradouro()).isEqualTo(DEFAULT_LOGRADOURO);
        assertThat(testEndereco.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testEndereco.getComplemento()).isEqualTo(DEFAULT_COMPLEMENTO);
        assertThat(testEndereco.getBairro()).isEqualTo(DEFAULT_BAIRRO);
        assertThat(testEndereco.getcEP()).isEqualTo(DEFAULT_C_EP);
    }

    @Test
    void getAllEnderecos() {
        // Initialize the database
        enderecoRepository.save(endereco).block();

        // Get all the enderecoList
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
            .value(hasItem(endereco.getId().intValue()))
            .jsonPath("$.[*].logradouro")
            .value(hasItem(DEFAULT_LOGRADOURO))
            .jsonPath("$.[*].numero")
            .value(hasItem(DEFAULT_NUMERO))
            .jsonPath("$.[*].complemento")
            .value(hasItem(DEFAULT_COMPLEMENTO))
            .jsonPath("$.[*].bairro")
            .value(hasItem(DEFAULT_BAIRRO))
            .jsonPath("$.[*].cEP")
            .value(hasItem(DEFAULT_C_EP));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnderecosWithEagerRelationshipsIsEnabled() {
        when(enderecoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(enderecoRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllEnderecosWithEagerRelationshipsIsNotEnabled() {
        when(enderecoRepositoryMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(enderecoRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getEndereco() {
        // Initialize the database
        enderecoRepository.save(endereco).block();

        // Get the endereco
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, endereco.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(endereco.getId().intValue()))
            .jsonPath("$.logradouro")
            .value(is(DEFAULT_LOGRADOURO))
            .jsonPath("$.numero")
            .value(is(DEFAULT_NUMERO))
            .jsonPath("$.complemento")
            .value(is(DEFAULT_COMPLEMENTO))
            .jsonPath("$.bairro")
            .value(is(DEFAULT_BAIRRO))
            .jsonPath("$.cEP")
            .value(is(DEFAULT_C_EP));
    }

    @Test
    void getNonExistingEndereco() {
        // Get the endereco
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewEndereco() throws Exception {
        // Initialize the database
        enderecoRepository.save(endereco).block();

        int databaseSizeBeforeUpdate = enderecoRepository.findAll().collectList().block().size();

        // Update the endereco
        Endereco updatedEndereco = enderecoRepository.findById(endereco.getId()).block();
        updatedEndereco
            .logradouro(UPDATED_LOGRADOURO)
            .numero(UPDATED_NUMERO)
            .complemento(UPDATED_COMPLEMENTO)
            .bairro(UPDATED_BAIRRO)
            .cEP(UPDATED_C_EP);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEndereco.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedEndereco))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll().collectList().block();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getLogradouro()).isEqualTo(UPDATED_LOGRADOURO);
        assertThat(testEndereco.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testEndereco.getComplemento()).isEqualTo(UPDATED_COMPLEMENTO);
        assertThat(testEndereco.getBairro()).isEqualTo(UPDATED_BAIRRO);
        assertThat(testEndereco.getcEP()).isEqualTo(UPDATED_C_EP);
    }

    @Test
    void putNonExistingEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().collectList().block().size();
        endereco.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, endereco.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(endereco))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll().collectList().block();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().collectList().block().size();
        endereco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(endereco))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll().collectList().block();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().collectList().block().size();
        endereco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(endereco))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll().collectList().block();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEnderecoWithPatch() throws Exception {
        // Initialize the database
        enderecoRepository.save(endereco).block();

        int databaseSizeBeforeUpdate = enderecoRepository.findAll().collectList().block().size();

        // Update the endereco using partial update
        Endereco partialUpdatedEndereco = new Endereco();
        partialUpdatedEndereco.setId(endereco.getId());

        partialUpdatedEndereco.complemento(UPDATED_COMPLEMENTO).bairro(UPDATED_BAIRRO).cEP(UPDATED_C_EP);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEndereco.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEndereco))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll().collectList().block();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getLogradouro()).isEqualTo(DEFAULT_LOGRADOURO);
        assertThat(testEndereco.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testEndereco.getComplemento()).isEqualTo(UPDATED_COMPLEMENTO);
        assertThat(testEndereco.getBairro()).isEqualTo(UPDATED_BAIRRO);
        assertThat(testEndereco.getcEP()).isEqualTo(UPDATED_C_EP);
    }

    @Test
    void fullUpdateEnderecoWithPatch() throws Exception {
        // Initialize the database
        enderecoRepository.save(endereco).block();

        int databaseSizeBeforeUpdate = enderecoRepository.findAll().collectList().block().size();

        // Update the endereco using partial update
        Endereco partialUpdatedEndereco = new Endereco();
        partialUpdatedEndereco.setId(endereco.getId());

        partialUpdatedEndereco
            .logradouro(UPDATED_LOGRADOURO)
            .numero(UPDATED_NUMERO)
            .complemento(UPDATED_COMPLEMENTO)
            .bairro(UPDATED_BAIRRO)
            .cEP(UPDATED_C_EP);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEndereco.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEndereco))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll().collectList().block();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
        Endereco testEndereco = enderecoList.get(enderecoList.size() - 1);
        assertThat(testEndereco.getLogradouro()).isEqualTo(UPDATED_LOGRADOURO);
        assertThat(testEndereco.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testEndereco.getComplemento()).isEqualTo(UPDATED_COMPLEMENTO);
        assertThat(testEndereco.getBairro()).isEqualTo(UPDATED_BAIRRO);
        assertThat(testEndereco.getcEP()).isEqualTo(UPDATED_C_EP);
    }

    @Test
    void patchNonExistingEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().collectList().block().size();
        endereco.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, endereco.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(endereco))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll().collectList().block();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().collectList().block().size();
        endereco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(endereco))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll().collectList().block();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEndereco() throws Exception {
        int databaseSizeBeforeUpdate = enderecoRepository.findAll().collectList().block().size();
        endereco.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(endereco))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Endereco in the database
        List<Endereco> enderecoList = enderecoRepository.findAll().collectList().block();
        assertThat(enderecoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEndereco() {
        // Initialize the database
        enderecoRepository.save(endereco).block();

        int databaseSizeBeforeDelete = enderecoRepository.findAll().collectList().block().size();

        // Delete the endereco
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, endereco.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Endereco> enderecoList = enderecoRepository.findAll().collectList().block();
        assertThat(enderecoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
