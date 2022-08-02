package br.com.rnati.pageboard.web.rest;

import br.com.rnati.pageboard.domain.Pergunta;
import br.com.rnati.pageboard.repository.PerguntaRepository;
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
 * REST controller for managing {@link br.com.rnati.pageboard.domain.Pergunta}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class PerguntaResource {

    private final Logger log = LoggerFactory.getLogger(PerguntaResource.class);

    private static final String ENTITY_NAME = "pergunta";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PerguntaRepository perguntaRepository;

    public PerguntaResource(PerguntaRepository perguntaRepository) {
        this.perguntaRepository = perguntaRepository;
    }

    /**
     * {@code POST  /perguntas} : Create a new pergunta.
     *
     * @param pergunta the pergunta to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pergunta, or with status {@code 400 (Bad Request)} if the pergunta has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/perguntas")
    public Mono<ResponseEntity<Pergunta>> createPergunta(@RequestBody Pergunta pergunta) throws URISyntaxException {
        log.debug("REST request to save Pergunta : {}", pergunta);
        if (pergunta.getId() != null) {
            throw new BadRequestAlertException("A new pergunta cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return perguntaRepository
            .save(pergunta)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/perguntas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /perguntas/:id} : Updates an existing pergunta.
     *
     * @param id the id of the pergunta to save.
     * @param pergunta the pergunta to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pergunta,
     * or with status {@code 400 (Bad Request)} if the pergunta is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pergunta couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/perguntas/{id}")
    public Mono<ResponseEntity<Pergunta>> updatePergunta(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Pergunta pergunta
    ) throws URISyntaxException {
        log.debug("REST request to update Pergunta : {}, {}", id, pergunta);
        if (pergunta.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pergunta.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return perguntaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return perguntaRepository
                    .save(pergunta)
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
     * {@code PATCH  /perguntas/:id} : Partial updates given fields of an existing pergunta, field will ignore if it is null
     *
     * @param id the id of the pergunta to save.
     * @param pergunta the pergunta to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pergunta,
     * or with status {@code 400 (Bad Request)} if the pergunta is not valid,
     * or with status {@code 404 (Not Found)} if the pergunta is not found,
     * or with status {@code 500 (Internal Server Error)} if the pergunta couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/perguntas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Pergunta>> partialUpdatePergunta(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Pergunta pergunta
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pergunta partially : {}, {}", id, pergunta);
        if (pergunta.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pergunta.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return perguntaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Pergunta> result = perguntaRepository
                    .findById(pergunta.getId())
                    .map(existingPergunta -> {
                        if (pergunta.getQuestao() != null) {
                            existingPergunta.setQuestao(pergunta.getQuestao());
                        }
                        if (pergunta.getResposta() != null) {
                            existingPergunta.setResposta(pergunta.getResposta());
                        }

                        return existingPergunta;
                    })
                    .flatMap(perguntaRepository::save);

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
     * {@code GET  /perguntas} : get all the perguntas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of perguntas in body.
     */
    @GetMapping("/perguntas")
    public Mono<List<Pergunta>> getAllPerguntas() {
        log.debug("REST request to get all Perguntas");
        return perguntaRepository.findAll().collectList();
    }

    /**
     * {@code GET  /perguntas} : get all the perguntas as a stream.
     * @return the {@link Flux} of perguntas.
     */
    @GetMapping(value = "/perguntas", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Pergunta> getAllPerguntasAsStream() {
        log.debug("REST request to get all Perguntas as a stream");
        return perguntaRepository.findAll();
    }

    /**
     * {@code GET  /perguntas/:id} : get the "id" pergunta.
     *
     * @param id the id of the pergunta to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pergunta, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/perguntas/{id}")
    public Mono<ResponseEntity<Pergunta>> getPergunta(@PathVariable Long id) {
        log.debug("REST request to get Pergunta : {}", id);
        Mono<Pergunta> pergunta = perguntaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pergunta);
    }
    @GetMapping("/perguntas/findByParagrafo/{id}")
    public Mono<List<Pergunta>> findByParagrafo(@PathVariable Long id) {
        log.debug("REST request to get Pergunta : {}", id);
        Mono<List<Pergunta>> pergunta = perguntaRepository.findByParagrafo(id).collectList();
       
        
        return pergunta;
    }
    /**
     * {@code DELETE  /perguntas/:id} : delete the "id" pergunta.
     *
     * @param id the id of the pergunta to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/perguntas/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePergunta(@PathVariable Long id) {
        log.debug("REST request to delete Pergunta : {}", id);
        return perguntaRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
