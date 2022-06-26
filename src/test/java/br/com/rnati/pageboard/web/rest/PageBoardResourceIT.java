package br.com.rnati.pageboard.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import br.com.rnati.pageboard.IntegrationTest;
import br.com.rnati.pageboard.domain.PageBoard;
import br.com.rnati.pageboard.repository.EntityManager;
import br.com.rnati.pageboard.repository.PageBoardRepository;
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
 * Integration tests for the {@link PageBoardResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class PageBoardResourceIT {

    private static final String ENTITY_API_URL = "/api/page-boards";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private PageBoardRepository pageBoardRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private PageBoard pageBoard;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PageBoard createEntity(EntityManager em) {
        PageBoard pageBoard = new PageBoard();
        return pageBoard;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static PageBoard createUpdatedEntity(EntityManager em) {
        PageBoard pageBoard = new PageBoard();
        return pageBoard;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(PageBoard.class).block();
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
        pageBoard = createEntity(em);
    }

    @Test
    void createPageBoard() throws Exception {
        int databaseSizeBeforeCreate = pageBoardRepository.findAll().collectList().block().size();
        // Create the PageBoard
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pageBoard))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the PageBoard in the database
        List<PageBoard> pageBoardList = pageBoardRepository.findAll().collectList().block();
        assertThat(pageBoardList).hasSize(databaseSizeBeforeCreate + 1);
        PageBoard testPageBoard = pageBoardList.get(pageBoardList.size() - 1);
    }

    @Test
    void createPageBoardWithExistingId() throws Exception {
        // Create the PageBoard with an existing ID
        pageBoard.setId(1L);

        int databaseSizeBeforeCreate = pageBoardRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pageBoard))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PageBoard in the database
        List<PageBoard> pageBoardList = pageBoardRepository.findAll().collectList().block();
        assertThat(pageBoardList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllPageBoardsAsStream() {
        // Initialize the database
        pageBoardRepository.save(pageBoard).block();

        List<PageBoard> pageBoardList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(PageBoard.class)
            .getResponseBody()
            .filter(pageBoard::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(pageBoardList).isNotNull();
        assertThat(pageBoardList).hasSize(1);
        PageBoard testPageBoard = pageBoardList.get(0);
    }

    @Test
    void getAllPageBoards() {
        // Initialize the database
        pageBoardRepository.save(pageBoard).block();

        // Get all the pageBoardList
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
            .value(hasItem(pageBoard.getId().intValue()));
    }

    @Test
    void getPageBoard() {
        // Initialize the database
        pageBoardRepository.save(pageBoard).block();

        // Get the pageBoard
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, pageBoard.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(pageBoard.getId().intValue()));
    }

    @Test
    void getNonExistingPageBoard() {
        // Get the pageBoard
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewPageBoard() throws Exception {
        // Initialize the database
        pageBoardRepository.save(pageBoard).block();

        int databaseSizeBeforeUpdate = pageBoardRepository.findAll().collectList().block().size();

        // Update the pageBoard
        PageBoard updatedPageBoard = pageBoardRepository.findById(pageBoard.getId()).block();

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedPageBoard.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedPageBoard))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PageBoard in the database
        List<PageBoard> pageBoardList = pageBoardRepository.findAll().collectList().block();
        assertThat(pageBoardList).hasSize(databaseSizeBeforeUpdate);
        PageBoard testPageBoard = pageBoardList.get(pageBoardList.size() - 1);
    }

    @Test
    void putNonExistingPageBoard() throws Exception {
        int databaseSizeBeforeUpdate = pageBoardRepository.findAll().collectList().block().size();
        pageBoard.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, pageBoard.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pageBoard))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PageBoard in the database
        List<PageBoard> pageBoardList = pageBoardRepository.findAll().collectList().block();
        assertThat(pageBoardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchPageBoard() throws Exception {
        int databaseSizeBeforeUpdate = pageBoardRepository.findAll().collectList().block().size();
        pageBoard.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pageBoard))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PageBoard in the database
        List<PageBoard> pageBoardList = pageBoardRepository.findAll().collectList().block();
        assertThat(pageBoardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamPageBoard() throws Exception {
        int databaseSizeBeforeUpdate = pageBoardRepository.findAll().collectList().block().size();
        pageBoard.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(pageBoard))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PageBoard in the database
        List<PageBoard> pageBoardList = pageBoardRepository.findAll().collectList().block();
        assertThat(pageBoardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdatePageBoardWithPatch() throws Exception {
        // Initialize the database
        pageBoardRepository.save(pageBoard).block();

        int databaseSizeBeforeUpdate = pageBoardRepository.findAll().collectList().block().size();

        // Update the pageBoard using partial update
        PageBoard partialUpdatedPageBoard = new PageBoard();
        partialUpdatedPageBoard.setId(pageBoard.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPageBoard.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPageBoard))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PageBoard in the database
        List<PageBoard> pageBoardList = pageBoardRepository.findAll().collectList().block();
        assertThat(pageBoardList).hasSize(databaseSizeBeforeUpdate);
        PageBoard testPageBoard = pageBoardList.get(pageBoardList.size() - 1);
    }

    @Test
    void fullUpdatePageBoardWithPatch() throws Exception {
        // Initialize the database
        pageBoardRepository.save(pageBoard).block();

        int databaseSizeBeforeUpdate = pageBoardRepository.findAll().collectList().block().size();

        // Update the pageBoard using partial update
        PageBoard partialUpdatedPageBoard = new PageBoard();
        partialUpdatedPageBoard.setId(pageBoard.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedPageBoard.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedPageBoard))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the PageBoard in the database
        List<PageBoard> pageBoardList = pageBoardRepository.findAll().collectList().block();
        assertThat(pageBoardList).hasSize(databaseSizeBeforeUpdate);
        PageBoard testPageBoard = pageBoardList.get(pageBoardList.size() - 1);
    }

    @Test
    void patchNonExistingPageBoard() throws Exception {
        int databaseSizeBeforeUpdate = pageBoardRepository.findAll().collectList().block().size();
        pageBoard.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, pageBoard.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pageBoard))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PageBoard in the database
        List<PageBoard> pageBoardList = pageBoardRepository.findAll().collectList().block();
        assertThat(pageBoardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchPageBoard() throws Exception {
        int databaseSizeBeforeUpdate = pageBoardRepository.findAll().collectList().block().size();
        pageBoard.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pageBoard))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the PageBoard in the database
        List<PageBoard> pageBoardList = pageBoardRepository.findAll().collectList().block();
        assertThat(pageBoardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamPageBoard() throws Exception {
        int databaseSizeBeforeUpdate = pageBoardRepository.findAll().collectList().block().size();
        pageBoard.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(pageBoard))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the PageBoard in the database
        List<PageBoard> pageBoardList = pageBoardRepository.findAll().collectList().block();
        assertThat(pageBoardList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deletePageBoard() {
        // Initialize the database
        pageBoardRepository.save(pageBoard).block();

        int databaseSizeBeforeDelete = pageBoardRepository.findAll().collectList().block().size();

        // Delete the pageBoard
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, pageBoard.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<PageBoard> pageBoardList = pageBoardRepository.findAll().collectList().block();
        assertThat(pageBoardList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
