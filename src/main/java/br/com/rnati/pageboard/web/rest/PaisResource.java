package br.com.rnati.pageboard.web.rest;

import br.com.rnati.pageboard.domain.Pais;
import br.com.rnati.pageboard.repository.PaisRepository;
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
 * REST controller for managing {@link br.com.rnati.pageboard.domain.Pais}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PaisResource {

    private final Logger log = LoggerFactory.getLogger(PaisResource.class);

    private static final String ENTITY_NAME = "pais";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PaisRepository paisRepository;

    public PaisResource(PaisRepository paisRepository) {
        this.paisRepository = paisRepository;
    }

    /**
     * {@code POST  /pais} : Create a new pais.
     *
     * @param pais the pais to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pais, or with status {@code 400 (Bad Request)} if the pais has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/pais")
    public Mono<ResponseEntity<Pais>> createPais(@RequestBody Pais pais) throws URISyntaxException {
        log.debug("REST request to save Pais : {}", pais);
        if (pais.getId() != null) {
            throw new BadRequestAlertException("A new pais cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return paisRepository
            .save(pais)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/pais/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /pais/:id} : Updates an existing pais.
     *
     * @param id the id of the pais to save.
     * @param pais the pais to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pais,
     * or with status {@code 400 (Bad Request)} if the pais is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pais couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/pais/{id}")
    public Mono<ResponseEntity<Pais>> updatePais(@PathVariable(value = "id", required = false) final Long id, @RequestBody Pais pais)
        throws URISyntaxException {
        log.debug("REST request to update Pais : {}, {}", id, pais);
        if (pais.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pais.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return paisRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return paisRepository
                    .save(pais)
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
     * {@code PATCH  /pais/:id} : Partial updates given fields of an existing pais, field will ignore if it is null
     *
     * @param id the id of the pais to save.
     * @param pais the pais to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pais,
     * or with status {@code 400 (Bad Request)} if the pais is not valid,
     * or with status {@code 404 (Not Found)} if the pais is not found,
     * or with status {@code 500 (Internal Server Error)} if the pais couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/pais/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Pais>> partialUpdatePais(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Pais pais
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pais partially : {}, {}", id, pais);
        if (pais.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pais.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return paisRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Pais> result = paisRepository
                    .findById(pais.getId())
                    .map(existingPais -> {
                        if (pais.getNome() != null) {
                            existingPais.setNome(pais.getNome());
                        }
                        if (pais.getSigla() != null) {
                            existingPais.setSigla(pais.getSigla());
                        }

                        return existingPais;
                    })
                    .flatMap(paisRepository::save);

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
     * {@code GET  /pais} : get all the pais.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of pais in body.
     */
    @GetMapping("/pais")
    public Mono<List<Pais>> getAllPais() {
        log.debug("REST request to get all Pais");
        return paisRepository.findAll().collectList();
    }

    /**
     * {@code GET  /pais} : get all the pais as a stream.
     * @return the {@link Flux} of pais.
     */
    @GetMapping(value = "/pais", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Pais> getAllPaisAsStream() {
        log.debug("REST request to get all Pais as a stream");
        return paisRepository.findAll();
    }

    /**
     * {@code GET  /pais/:id} : get the "id" pais.
     *
     * @param id the id of the pais to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pais, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/pais/{id}")
    public Mono<ResponseEntity<Pais>> getPais(@PathVariable Long id) {
        log.debug("REST request to get Pais : {}", id);
        Mono<Pais> pais = paisRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pais);
    }

    /**
     * {@code DELETE  /pais/:id} : delete the "id" pais.
     *
     * @param id the id of the pais to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/pais/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePais(@PathVariable Long id) {
        log.debug("REST request to delete Pais : {}", id);
        return paisRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
