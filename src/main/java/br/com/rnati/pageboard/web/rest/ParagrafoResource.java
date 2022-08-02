package br.com.rnati.pageboard.web.rest;

import br.com.rnati.pageboard.domain.Paragrafo;
import br.com.rnati.pageboard.repository.ParagrafoRepository;
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
 * REST controller for managing {@link br.com.rnati.pageboard.domain.Paragrafo}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ParagrafoResource {

    private final Logger log = LoggerFactory.getLogger(ParagrafoResource.class);

    private static final String ENTITY_NAME = "paragrafo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ParagrafoRepository paragrafoRepository;

    public ParagrafoResource(ParagrafoRepository paragrafoRepository) {
        this.paragrafoRepository = paragrafoRepository;
    }

    /**
     * {@code POST  /paragrafos} : Create a new paragrafo.
     *
     * @param paragrafo the paragrafo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new paragrafo, or with status {@code 400 (Bad Request)} if the paragrafo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/paragrafos")
    public Mono<ResponseEntity<Paragrafo>> createParagrafo(@RequestBody Paragrafo paragrafo) throws URISyntaxException {
        log.debug("REST request to save Paragrafo : {}", paragrafo);
        if (paragrafo.getId() != null) {
            throw new BadRequestAlertException("A new paragrafo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return paragrafoRepository
            .save(paragrafo)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/paragrafos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /paragrafos/:id} : Updates an existing paragrafo.
     *
     * @param id the id of the paragrafo to save.
     * @param paragrafo the paragrafo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paragrafo,
     * or with status {@code 400 (Bad Request)} if the paragrafo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the paragrafo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/paragrafos/{id}")
    public Mono<ResponseEntity<Paragrafo>> updateParagrafo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Paragrafo paragrafo
    ) throws URISyntaxException {
        log.debug("REST request to update Paragrafo : {}, {}", id, paragrafo);
        if (paragrafo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paragrafo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return paragrafoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return paragrafoRepository
                    .save(paragrafo)
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
     * {@code PATCH  /paragrafos/:id} : Partial updates given fields of an existing paragrafo, field will ignore if it is null
     *
     * @param id the id of the paragrafo to save.
     * @param paragrafo the paragrafo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated paragrafo,
     * or with status {@code 400 (Bad Request)} if the paragrafo is not valid,
     * or with status {@code 404 (Not Found)} if the paragrafo is not found,
     * or with status {@code 500 (Internal Server Error)} if the paragrafo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/paragrafos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Paragrafo>> partialUpdateParagrafo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Paragrafo paragrafo
    ) throws URISyntaxException {
        log.debug("REST request to partial update Paragrafo partially : {}, {}", id, paragrafo);
        if (paragrafo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, paragrafo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return paragrafoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Paragrafo> result = paragrafoRepository
                    .findById(paragrafo.getId())
                    .map(existingParagrafo -> {
                        if (paragrafo.getNumero() != null) {
                            existingParagrafo.setNumero(paragrafo.getNumero());
                        }
                        if (paragrafo.getTexto() != null) {
                            existingParagrafo.setTexto(paragrafo.getTexto());
                        }
                        if (paragrafo.getResumo() != null) {
                            existingParagrafo.setResumo(paragrafo.getResumo());
                        }

                        return existingParagrafo;
                    })
                    .flatMap(paragrafoRepository::save);

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
     * {@code GET  /paragrafos} : get all the paragrafos.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of paragrafos in body.
     */
    @GetMapping("/paragrafos")
    public Mono<List<Paragrafo>> getAllParagrafos(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Paragrafos");
        return paragrafoRepository.findAllWithEagerRelationships().collectList();
    }

    /**
     * {@code GET  /paragrafos} : get all the paragrafos as a stream.
     * @return the {@link Flux} of paragrafos.
     */
    @GetMapping(value = "/paragrafos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Paragrafo> getAllParagrafosAsStream() {
        log.debug("REST request to get all Paragrafos as a stream");
        return paragrafoRepository.findAll();
    }

    /**
     * {@code GET  /paragrafos/:id} : get the "id" paragrafo.
     *
     * @param id the id of the paragrafo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the paragrafo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/paragrafos/{id}")
    public Mono<ResponseEntity<Paragrafo>> getParagrafo(@PathVariable Long id) {
        log.debug("REST request to get Paragrafo : {}", id);
        Mono<Paragrafo> paragrafo = paragrafoRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(paragrafo);
    }

    /**
     * {@code DELETE  /paragrafos/:id} : delete the "id" paragrafo.
     *
     * @param id the id of the paragrafo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/paragrafos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteParagrafo(@PathVariable Long id) {
        log.debug("REST request to delete Paragrafo : {}", id);
        return paragrafoRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
    @GetMapping("/paragrafos/findByPagina/{id}")
    public Mono<List<Paragrafo>> findByPagina(@PathVariable Long id) {
        log.debug("REST request to get Paragrafo : {}", id);
        Mono<List<Paragrafo>> paragrafo = paragrafoRepository.findByPagina(id).collectList();
       
        
        return paragrafo;
    }
}
