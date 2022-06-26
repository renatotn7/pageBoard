package br.com.rnati.pageboard.web.rest;

import br.com.rnati.pageboard.domain.PageBoard;
import br.com.rnati.pageboard.repository.PageBoardRepository;
import br.com.rnati.pageboard.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link br.com.rnati.pageboard.domain.PageBoard}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PageBoardResource {

    private final Logger log = LoggerFactory.getLogger(PageBoardResource.class);

    private static final String ENTITY_NAME = "pageBoard";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PageBoardRepository pageBoardRepository;

    public PageBoardResource(PageBoardRepository pageBoardRepository) {
        this.pageBoardRepository = pageBoardRepository;
    }

    /**
     * {@code POST  /page-boards} : Create a new pageBoard.
     *
     * @param pageBoard the pageBoard to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pageBoard, or with status {@code 400 (Bad Request)} if the pageBoard has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/page-boards")
    public Mono<ResponseEntity<PageBoard>> createPageBoard(@RequestBody PageBoard pageBoard) throws URISyntaxException {
        log.debug("REST request to save PageBoard : {}", pageBoard);
        if (pageBoard.getId() != null) {
            throw new BadRequestAlertException("A new pageBoard cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return pageBoardRepository
            .save(pageBoard)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/page-boards/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /page-boards/:id} : Updates an existing pageBoard.
     *
     * @param id the id of the pageBoard to save.
     * @param pageBoard the pageBoard to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pageBoard,
     * or with status {@code 400 (Bad Request)} if the pageBoard is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pageBoard couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/page-boards/{id}")
    public Mono<ResponseEntity<PageBoard>> updatePageBoard(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PageBoard pageBoard
    ) throws URISyntaxException {
        log.debug("REST request to update PageBoard : {}, {}", id, pageBoard);
        if (pageBoard.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pageBoard.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pageBoardRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                // no save call needed as we have no fields that can be updated
                return pageBoardRepository
                    .findById(pageBoard.getId())
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /page-boards/:id} : Partial updates given fields of an existing pageBoard, field will ignore if it is null
     *
     * @param id the id of the pageBoard to save.
     * @param pageBoard the pageBoard to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pageBoard,
     * or with status {@code 400 (Bad Request)} if the pageBoard is not valid,
     * or with status {@code 404 (Not Found)} if the pageBoard is not found,
     * or with status {@code 500 (Internal Server Error)} if the pageBoard couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/page-boards/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<PageBoard>> partialUpdatePageBoard(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody PageBoard pageBoard
    ) throws URISyntaxException {
        log.debug("REST request to partial update PageBoard partially : {}, {}", id, pageBoard);
        if (pageBoard.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pageBoard.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return pageBoardRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<PageBoard> result = pageBoardRepository
                    .findById(pageBoard.getId())
                    .map(existingPageBoard -> {
                        return existingPageBoard;
                    })// .flatMap(pageBoardRepository::save)
                ;

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /page-boards} : get all the pageBoards.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pageBoards in body.
     */
    @GetMapping("/page-boards")
    public Mono<List<PageBoard>> getAllPageBoards() {
        log.debug("REST request to get all PageBoards");
        return pageBoardRepository.findAll().collectList();
    }

    /**
     * {@code GET  /page-boards} : get all the pageBoards as a stream.
     * @return the {@link Flux} of pageBoards.
     */
    @GetMapping(value = "/page-boards", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<PageBoard> getAllPageBoardsAsStream() {
        log.debug("REST request to get all PageBoards as a stream");
        return pageBoardRepository.findAll();
    }

    /**
     * {@code GET  /page-boards/:id} : get the "id" pageBoard.
     *
     * @param id the id of the pageBoard to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pageBoard, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/page-boards/{id}")
    public Mono<ResponseEntity<PageBoard>> getPageBoard(@PathVariable Long id) {
        log.debug("REST request to get PageBoard : {}", id);
        Mono<PageBoard> pageBoard = pageBoardRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pageBoard);
    }

    /**
     * {@code DELETE  /page-boards/:id} : delete the "id" pageBoard.
     *
     * @param id the id of the pageBoard to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/page-boards/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePageBoard(@PathVariable Long id) {
        log.debug("REST request to delete PageBoard : {}", id);
        return pageBoardRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
