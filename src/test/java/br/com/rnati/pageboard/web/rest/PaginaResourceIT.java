package br.com.rnati.pageboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import br.com.rnati.pageboard.IntegrationTest;
import br.com.rnati.pageboard.domain.Pagina;
import br.com.rnati.pageboard.repository.EntityManager;
import br.com.rnati.pageboard.repository.PaginaRepository;
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
 * Integration tests for the {@link PaginaResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PaginaResourceIT {

    private static final Integer DEFAULT_NUMERO = 1;
    private static final Integer UPDATED_NUMERO = 2;

    private static final String DEFAULT_TEXTO = "AAAAAAAAAA";
    private static final String UPDATED_TEXTO = "BBBBBBBBBB";

    private static final String DEFAULT_PLANO_DE_AULA = "AAAAAAAAAA";
    private static final String UPDATED_PLANO_DE_AULA = "BBBBBBBBBB";

    private static final byte[] DEFAULT_IMAGEM = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGEM = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_IMAGEM_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGEM_CONTENT_TYPE = "image/png";

    private static final String ENTITY_API_URL = "/api/paginas";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PaginaRepository paginaRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Pagina pagina;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pagina createEntity(EntityManager em) {
        Pagina pagina = new Pagina()
            .numero(DEFAULT_NUMERO)
            .texto(DEFAULT_TEXTO)
            .planoDeAula(DEFAULT_PLANO_DE_AULA)
            .imagem(DEFAULT_IMAGEM)
            .imagemContentType(DEFAULT_IMAGEM_CONTENT_TYPE);
        return pagina;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Pagina createUpdatedEntity(EntityManager em) {
        Pagina pagina = new Pagina()
            .numero(UPDATED_NUMERO)
            .texto(UPDATED_TEXTO)
            .planoDeAula(UPDATED_PLANO_DE_AULA)
            .imagem(UPDATED_IMAGEM)
            .imagemContentType(UPDATED_IMAGEM_CONTENT_TYPE);
        return pagina;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Pagina.class).block();
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
        pagina = createEntity(em);
    }

    @Test
    void createPagina() throws Exception {
        int databaseSizeBeforeCreate = paginaRepository.findAll().collectList().block().size();
        // Create the Pagina
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pagina))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the Pagina in the database
        List<Pagina> paginaList = paginaRepository.findAll().collectList().block();
        assertThat(paginaList).hasSize(databaseSizeBeforeCreate + 1);
        Pagina testPagina = paginaList.get(paginaList.size() - 1);
        assertThat(testPagina.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testPagina.getTexto()).isEqualTo(DEFAULT_TEXTO);
        assertThat(testPagina.getPlanoDeAula()).isEqualTo(DEFAULT_PLANO_DE_AULA);
        assertThat(testPagina.getImagem()).isEqualTo(DEFAULT_IMAGEM);
        assertThat(testPagina.getImagemContentType()).isEqualTo(DEFAULT_IMAGEM_CONTENT_TYPE);
    }

    @Test
    void createPaginaWithExistingId() throws Exception {
        // Create the Pagina with an existing ID
        pagina.setId(1L);

        int databaseSizeBeforeCreate = paginaRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pagina))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pagina in the database
        List<Pagina> paginaList = paginaRepository.findAll().collectList().block();
        assertThat(paginaList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPaginasAsStream() {
        // Initialize the database
        paginaRepository.save(pagina).block();

        List<Pagina> paginaList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(Pagina.class)
            .getResponseBody()
            .filter(pagina::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(paginaList).isNotNull();
        assertThat(paginaList).hasSize(1);
        Pagina testPagina = paginaList.get(0);
        assertThat(testPagina.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testPagina.getTexto()).isEqualTo(DEFAULT_TEXTO);
        assertThat(testPagina.getPlanoDeAula()).isEqualTo(DEFAULT_PLANO_DE_AULA);
        assertThat(testPagina.getImagem()).isEqualTo(DEFAULT_IMAGEM);
        assertThat(testPagina.getImagemContentType()).isEqualTo(DEFAULT_IMAGEM_CONTENT_TYPE);
    }

    @Test
    void getAllPaginas() {
        // Initialize the database
        paginaRepository.save(pagina).block();

        // Get all the paginaList
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
            .value(hasItem(pagina.getId().intValue()))
            .jsonPath("$.[*].numero")
            .value(hasItem(DEFAULT_NUMERO))
            .jsonPath("$.[*].texto")
            .value(hasItem(DEFAULT_TEXTO.toString()))
            .jsonPath("$.[*].planoDeAula")
            .value(hasItem(DEFAULT_PLANO_DE_AULA))
            .jsonPath("$.[*].imagemContentType")
            .value(hasItem(DEFAULT_IMAGEM_CONTENT_TYPE))
            .jsonPath("$.[*].imagem")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGEM)));
    }

    @Test
    void getPagina() {
        // Initialize the database
        paginaRepository.save(pagina).block();

        // Get the pagina
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pagina.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pagina.getId().intValue()))
            .jsonPath("$.numero")
            .value(is(DEFAULT_NUMERO))
            .jsonPath("$.texto")
            .value(is(DEFAULT_TEXTO.toString()))
            .jsonPath("$.planoDeAula")
            .value(is(DEFAULT_PLANO_DE_AULA))
            .jsonPath("$.imagemContentType")
            .value(is(DEFAULT_IMAGEM_CONTENT_TYPE))
            .jsonPath("$.imagem")
            .value(is(Base64Utils.encodeToString(DEFAULT_IMAGEM)));
    }

    @Test
    void getNonExistingPagina() {
        // Get the pagina
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPagina() throws Exception {
        // Initialize the database
        paginaRepository.save(pagina).block();

        int databaseSizeBeforeUpdate = paginaRepository.findAll().collectList().block().size();

        // Update the pagina
        Pagina updatedPagina = paginaRepository.findById(pagina.getId()).block();
        updatedPagina
            .numero(UPDATED_NUMERO)
            .texto(UPDATED_TEXTO)
            .planoDeAula(UPDATED_PLANO_DE_AULA)
            .imagem(UPDATED_IMAGEM)
            .imagemContentType(UPDATED_IMAGEM_CONTENT_TYPE);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPagina.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPagina))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pagina in the database
        List<Pagina> paginaList = paginaRepository.findAll().collectList().block();
        assertThat(paginaList).hasSize(databaseSizeBeforeUpdate);
        Pagina testPagina = paginaList.get(paginaList.size() - 1);
        assertThat(testPagina.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testPagina.getTexto()).isEqualTo(UPDATED_TEXTO);
        assertThat(testPagina.getPlanoDeAula()).isEqualTo(UPDATED_PLANO_DE_AULA);
        assertThat(testPagina.getImagem()).isEqualTo(UPDATED_IMAGEM);
        assertThat(testPagina.getImagemContentType()).isEqualTo(UPDATED_IMAGEM_CONTENT_TYPE);
    }

    @Test
    void putNonExistingPagina() throws Exception {
        int databaseSizeBeforeUpdate = paginaRepository.findAll().collectList().block().size();
        pagina.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pagina.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pagina))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pagina in the database
        List<Pagina> paginaList = paginaRepository.findAll().collectList().block();
        assertThat(paginaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPagina() throws Exception {
        int databaseSizeBeforeUpdate = paginaRepository.findAll().collectList().block().size();
        pagina.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pagina))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pagina in the database
        List<Pagina> paginaList = paginaRepository.findAll().collectList().block();
        assertThat(paginaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPagina() throws Exception {
        int databaseSizeBeforeUpdate = paginaRepository.findAll().collectList().block().size();
        pagina.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pagina))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pagina in the database
        List<Pagina> paginaList = paginaRepository.findAll().collectList().block();
        assertThat(paginaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePaginaWithPatch() throws Exception {
        // Initialize the database
        paginaRepository.save(pagina).block();

        int databaseSizeBeforeUpdate = paginaRepository.findAll().collectList().block().size();

        // Update the pagina using partial update
        Pagina partialUpdatedPagina = new Pagina();
        partialUpdatedPagina.setId(pagina.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPagina.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPagina))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pagina in the database
        List<Pagina> paginaList = paginaRepository.findAll().collectList().block();
        assertThat(paginaList).hasSize(databaseSizeBeforeUpdate);
        Pagina testPagina = paginaList.get(paginaList.size() - 1);
        assertThat(testPagina.getNumero()).isEqualTo(DEFAULT_NUMERO);
        assertThat(testPagina.getTexto()).isEqualTo(DEFAULT_TEXTO);
        assertThat(testPagina.getPlanoDeAula()).isEqualTo(DEFAULT_PLANO_DE_AULA);
        assertThat(testPagina.getImagem()).isEqualTo(DEFAULT_IMAGEM);
        assertThat(testPagina.getImagemContentType()).isEqualTo(DEFAULT_IMAGEM_CONTENT_TYPE);
    }

    @Test
    void fullUpdatePaginaWithPatch() throws Exception {
        // Initialize the database
        paginaRepository.save(pagina).block();

        int databaseSizeBeforeUpdate = paginaRepository.findAll().collectList().block().size();

        // Update the pagina using partial update
        Pagina partialUpdatedPagina = new Pagina();
        partialUpdatedPagina.setId(pagina.getId());

        partialUpdatedPagina
            .numero(UPDATED_NUMERO)
            .texto(UPDATED_TEXTO)
            .planoDeAula(UPDATED_PLANO_DE_AULA)
            .imagem(UPDATED_IMAGEM)
            .imagemContentType(UPDATED_IMAGEM_CONTENT_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPagina.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPagina))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Pagina in the database
        List<Pagina> paginaList = paginaRepository.findAll().collectList().block();
        assertThat(paginaList).hasSize(databaseSizeBeforeUpdate);
        Pagina testPagina = paginaList.get(paginaList.size() - 1);
        assertThat(testPagina.getNumero()).isEqualTo(UPDATED_NUMERO);
        assertThat(testPagina.getTexto()).isEqualTo(UPDATED_TEXTO);
        assertThat(testPagina.getPlanoDeAula()).isEqualTo(UPDATED_PLANO_DE_AULA);
        assertThat(testPagina.getImagem()).isEqualTo(UPDATED_IMAGEM);
        assertThat(testPagina.getImagemContentType()).isEqualTo(UPDATED_IMAGEM_CONTENT_TYPE);
    }

    @Test
    void patchNonExistingPagina() throws Exception {
        int databaseSizeBeforeUpdate = paginaRepository.findAll().collectList().block().size();
        pagina.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pagina.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pagina))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pagina in the database
        List<Pagina> paginaList = paginaRepository.findAll().collectList().block();
        assertThat(paginaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPagina() throws Exception {
        int databaseSizeBeforeUpdate = paginaRepository.findAll().collectList().block().size();
        pagina.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pagina))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Pagina in the database
        List<Pagina> paginaList = paginaRepository.findAll().collectList().block();
        assertThat(paginaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPagina() throws Exception {
        int databaseSizeBeforeUpdate = paginaRepository.findAll().collectList().block().size();
        pagina.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pagina))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Pagina in the database
        List<Pagina> paginaList = paginaRepository.findAll().collectList().block();
        assertThat(paginaList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePagina() {
        // Initialize the database
        paginaRepository.save(pagina).block();

        int databaseSizeBeforeDelete = paginaRepository.findAll().collectList().block().size();

        // Delete the pagina
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pagina.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<Pagina> paginaList = paginaRepository.findAll().collectList().block();
        assertThat(paginaList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
