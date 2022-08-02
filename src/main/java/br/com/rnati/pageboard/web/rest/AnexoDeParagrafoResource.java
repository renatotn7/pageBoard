package br.com.rnati.pageboard.web.rest;


import br.com.rnati.pageboard.domain.AnexoDeParagrafo;
import br.com.rnati.pageboard.repository.AnexoDeParagrafoRepository;
import br.com.rnati.pageboard.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link br.com.rnati.pageboard.domain.AnexoDeParagrafo}.
 */
@RestController
@RequestMapping("/api")
@Transactional
@Order(1)
public class AnexoDeParagrafoResource {

    private final Logger log = LoggerFactory.getLogger(AnexoDeParagrafoResource.class);

    private static final String ENTITY_NAME = "anexoDeParagrafo";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final AnexoDeParagrafoRepository anexoDeParagrafoRepository;

    public AnexoDeParagrafoResource(AnexoDeParagrafoRepository anexoDeParagrafoRepository) {
        this.anexoDeParagrafoRepository = anexoDeParagrafoRepository;
    }

    /**
     * {@code POST  /anexo-de-paragrafos} : Create a new anexoDeParagrafo.
     *
     * @param anexoDeParagrafo the anexoDeParagrafo to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new anexoDeParagrafo, or with status {@code 400 (Bad Request)} if the anexoDeParagrafo has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/anexo-de-paragrafos")
    public Mono<ResponseEntity<AnexoDeParagrafo>> createAnexoDeParagrafo(@RequestBody AnexoDeParagrafo anexoDeParagrafo)
        throws URISyntaxException {
        log.debug("REST request to save AnexoDeParagrafo : {}", anexoDeParagrafo);
        if (anexoDeParagrafo.getId() != null) {
            throw new BadRequestAlertException("A new anexoDeParagrafo cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return anexoDeParagrafoRepository
            .save(anexoDeParagrafo)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/anexo-de-paragrafos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /anexo-de-paragrafos/:id} : Updates an existing anexoDeParagrafo.
     *
     * @param id the id of the anexoDeParagrafo to save.
     * @param anexoDeParagrafo the anexoDeParagrafo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated anexoDeParagrafo,
     * or with status {@code 400 (Bad Request)} if the anexoDeParagrafo is not valid,
     * or with status {@code 500 (Internal Server Error)} if the anexoDeParagrafo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/anexo-de-paragrafos/{id}")
    public Mono<ResponseEntity<AnexoDeParagrafo>> updateAnexoDeParagrafo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AnexoDeParagrafo anexoDeParagrafo
    ) throws URISyntaxException {
        log.debug("REST request to update AnexoDeParagrafo : {}, {}", id, anexoDeParagrafo);
        if (anexoDeParagrafo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, anexoDeParagrafo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return anexoDeParagrafoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return anexoDeParagrafoRepository
                    .save(anexoDeParagrafo)
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
     * {@code PATCH  /anexo-de-paragrafos/:id} : Partial updates given fields of an existing anexoDeParagrafo, field will ignore if it is null
     *
     * @param id the id of the anexoDeParagrafo to save.
     * @param anexoDeParagrafo the anexoDeParagrafo to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated anexoDeParagrafo,
     * or with status {@code 400 (Bad Request)} if the anexoDeParagrafo is not valid,
     * or with status {@code 404 (Not Found)} if the anexoDeParagrafo is not found,
     * or with status {@code 500 (Internal Server Error)} if the anexoDeParagrafo couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/anexo-de-paragrafos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<AnexoDeParagrafo>> partialUpdateAnexoDeParagrafo(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody AnexoDeParagrafo anexoDeParagrafo
    ) throws URISyntaxException {
        log.debug("REST request to partial update AnexoDeParagrafo partially : {}, {}", id, anexoDeParagrafo);
        if (anexoDeParagrafo.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, anexoDeParagrafo.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return anexoDeParagrafoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<AnexoDeParagrafo> result = anexoDeParagrafoRepository
                    .findById(anexoDeParagrafo.getId())
                    .map(existingAnexoDeParagrafo -> {
                        if (anexoDeParagrafo.getTipo() != null) {
                            existingAnexoDeParagrafo.setTipo(anexoDeParagrafo.getTipo());
                        }
                        if (anexoDeParagrafo.getValue() != null) {
                            existingAnexoDeParagrafo.setValue(anexoDeParagrafo.getValue());
                        }

                        return existingAnexoDeParagrafo;
                    })
                    .flatMap(anexoDeParagrafoRepository::save);

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
     * {@code GET  /anexo-de-paragrafos} : get all the anexoDeParagrafos.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of anexoDeParagrafos in body.
     */
    @GetMapping("/anexo-de-paragrafos")
    public Mono<ResponseEntity<List<AnexoDeParagrafo>>> getAllAnexoDeParagrafos(
        @org.springdoc.api.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        log.debug("REST request to get a page of AnexoDeParagrafos");
        return anexoDeParagrafoRepository
            .count()
            .zipWith(anexoDeParagrafoRepository.findAllBy(pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity
                    .ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            UriComponentsBuilder.fromHttpRequest(request),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /anexo-de-paragrafos/:id} : get the "id" anexoDeParagrafo.
     *
     * @param id the id of the anexoDeParagrafo to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the anexoDeParagrafo, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/anexo-de-paragrafos/{id}")
    public Mono<ResponseEntity<AnexoDeParagrafo>> getAnexoDeParagrafo(@PathVariable Long id) {
        log.debug("REST request to get AnexoDeParagrafo : {}", id);
        Mono<AnexoDeParagrafo> anexoDeParagrafo = anexoDeParagrafoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(anexoDeParagrafo);
    }

    /**
     * {@code DELETE  /anexo-de-paragrafos/:id} : delete the "id" anexoDeParagrafo.
     *
     * @param id the id of the anexoDeParagrafo to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/anexo-de-paragrafos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteAnexoDeParagrafo(@PathVariable Long id) {
        log.debug("REST request to delete AnexoDeParagrafo : {}", id);
        return anexoDeParagrafoRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
    @GetMapping("/anexo-de-paragrafos/findByParagrafo/{tipoAnexo}/{id}")
    public Mono<List<AnexoDeParagrafo>> findByParagrafo(@PathVariable String tipoAnexo,@PathVariable Long id) {
        log.debug("REST request to get Pergunta : {}", id);
        Mono<List<AnexoDeParagrafo>> anexo = anexoDeParagrafoRepository.findByParagrafo(id,tipoAnexo).collectList();
       
        
        return anexo;
    }
}
