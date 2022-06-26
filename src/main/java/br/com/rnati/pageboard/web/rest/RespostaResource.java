package br.com.rnati.pageboard.web.rest;

import br.com.rnati.pageboard.domain.Resposta;
import br.com.rnati.pageboard.repository.RespostaRepository;
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
 * REST controller for managing {@link br.com.rnati.pageboard.domain.Resposta}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class RespostaResource {

    private final Logger log = LoggerFactory.getLogger(RespostaResource.class);

    private static final String ENTITY_NAME = "resposta";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RespostaRepository respostaRepository;

    public RespostaResource(RespostaRepository respostaRepository) {
        this.respostaRepository = respostaRepository;
    }

    /**
     * {@code POST  /respostas} : Create a new resposta.
     *
     * @param resposta the resposta to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new resposta, or with status {@code 400 (Bad Request)} if the resposta has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/respostas")
    public Mono<ResponseEntity<Resposta>> createResposta(@RequestBody Resposta resposta) throws URISyntaxException {
        log.debug("REST request to save Resposta : {}", resposta);
        if (resposta.getId() != null) {
            throw new BadRequestAlertException("A new resposta cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return respostaRepository
            .save(resposta)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/respostas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /respostas/:id} : Updates an existing resposta.
     *
     * @param id the id of the resposta to save.
     * @param resposta the resposta to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resposta,
     * or with status {@code 400 (Bad Request)} if the resposta is not valid,
     * or with status {@code 500 (Internal Server Error)} if the resposta couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/respostas/{id}")
    public Mono<ResponseEntity<Resposta>> updateResposta(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Resposta resposta
    ) throws URISyntaxException {
        log.debug("REST request to update Resposta : {}, {}", id, resposta);
        if (resposta.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resposta.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return respostaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return respostaRepository
                    .save(resposta)
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
     * {@code PATCH  /respostas/:id} : Partial updates given fields of an existing resposta, field will ignore if it is null
     *
     * @param id the id of the resposta to save.
     * @param resposta the resposta to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated resposta,
     * or with status {@code 400 (Bad Request)} if the resposta is not valid,
     * or with status {@code 404 (Not Found)} if the resposta is not found,
     * or with status {@code 500 (Internal Server Error)} if the resposta couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/respostas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Resposta>> partialUpdateResposta(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Resposta resposta
    ) throws URISyntaxException {
        log.debug("REST request to partial update Resposta partially : {}, {}", id, resposta);
        if (resposta.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, resposta.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return respostaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Resposta> result = respostaRepository
                    .findById(resposta.getId())
                    .map(existingResposta -> {
                        if (resposta.getTexto() != null) {
                            existingResposta.setTexto(resposta.getTexto());
                        }

                        return existingResposta;
                    })
                    .flatMap(respostaRepository::save);

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
     * {@code GET  /respostas} : get all the respostas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of respostas in body.
     */
    @GetMapping("/respostas")
    public Mono<List<Resposta>> getAllRespostas() {
        log.debug("REST request to get all Respostas");
        return respostaRepository.findAll().collectList();
    }

    /**
     * {@code GET  /respostas} : get all the respostas as a stream.
     * @return the {@link Flux} of respostas.
     */
    @GetMapping(value = "/respostas", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Resposta> getAllRespostasAsStream() {
        log.debug("REST request to get all Respostas as a stream");
        return respostaRepository.findAll();
    }

    /**
     * {@code GET  /respostas/:id} : get the "id" resposta.
     *
     * @param id the id of the resposta to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the resposta, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/respostas/{id}")
    public Mono<ResponseEntity<Resposta>> getResposta(@PathVariable Long id) {
        log.debug("REST request to get Resposta : {}", id);
        Mono<Resposta> resposta = respostaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(resposta);
    }

    /**
     * {@code DELETE  /respostas/:id} : delete the "id" resposta.
     *
     * @param id the id of the resposta to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/respostas/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteResposta(@PathVariable Long id) {
        log.debug("REST request to delete Resposta : {}", id);
        return respostaRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
