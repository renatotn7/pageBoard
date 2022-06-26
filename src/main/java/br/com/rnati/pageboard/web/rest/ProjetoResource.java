package br.com.rnati.pageboard.web.rest;

import br.com.rnati.pageboard.domain.Projeto;
import br.com.rnati.pageboard.repository.ProjetoRepository;
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
 * REST controller for managing {@link br.com.rnati.pageboard.domain.Projeto}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class ProjetoResource {

    private final Logger log = LoggerFactory.getLogger(ProjetoResource.class);

    private static final String ENTITY_NAME = "projeto";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjetoRepository projetoRepository;

    public ProjetoResource(ProjetoRepository projetoRepository) {
        this.projetoRepository = projetoRepository;
    }

    /**
     * {@code POST  /projetos} : Create a new projeto.
     *
     * @param projeto the projeto to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projeto, or with status {@code 400 (Bad Request)} if the projeto has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/projetos")
    public Mono<ResponseEntity<Projeto>> createProjeto(@RequestBody Projeto projeto) throws URISyntaxException {
        log.debug("REST request to save Projeto : {}", projeto);
        if (projeto.getId() != null) {
            throw new BadRequestAlertException("A new projeto cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return projetoRepository
            .save(projeto)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/projetos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /projetos/:id} : Updates an existing projeto.
     *
     * @param id the id of the projeto to save.
     * @param projeto the projeto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projeto,
     * or with status {@code 400 (Bad Request)} if the projeto is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projeto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/projetos/{id}")
    public Mono<ResponseEntity<Projeto>> updateProjeto(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Projeto projeto
    ) throws URISyntaxException {
        log.debug("REST request to update Projeto : {}, {}", id, projeto);
        if (projeto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projeto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projetoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return projetoRepository
                    .save(projeto)
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
     * {@code PATCH  /projetos/:id} : Partial updates given fields of an existing projeto, field will ignore if it is null
     *
     * @param id the id of the projeto to save.
     * @param projeto the projeto to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projeto,
     * or with status {@code 400 (Bad Request)} if the projeto is not valid,
     * or with status {@code 404 (Not Found)} if the projeto is not found,
     * or with status {@code 500 (Internal Server Error)} if the projeto couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/projetos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Projeto>> partialUpdateProjeto(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Projeto projeto
    ) throws URISyntaxException {
        log.debug("REST request to partial update Projeto partially : {}, {}", id, projeto);
        if (projeto.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projeto.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projetoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Projeto> result = projetoRepository
                    .findById(projeto.getId())
                    .map(existingProjeto -> {
                        if (projeto.getNome() != null) {
                            existingProjeto.setNome(projeto.getNome());
                        }
                        if (projeto.getDescricao() != null) {
                            existingProjeto.setDescricao(projeto.getDescricao());
                        }
                        if (projeto.getImagem() != null) {
                            existingProjeto.setImagem(projeto.getImagem());
                        }
                        if (projeto.getImagemContentType() != null) {
                            existingProjeto.setImagemContentType(projeto.getImagemContentType());
                        }
                        if (projeto.getTags() != null) {
                            existingProjeto.setTags(projeto.getTags());
                        }

                        return existingProjeto;
                    })
                    .flatMap(projetoRepository::save);

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
     * {@code GET  /projetos} : get all the projetos.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projetos in body.
     */
    @GetMapping("/projetos")
    public Mono<List<Projeto>> getAllProjetos() {
        log.debug("REST request to get all Projetos");
        return projetoRepository.findAll().collectList();
    }

    /**
     * {@code GET  /projetos} : get all the projetos as a stream.
     * @return the {@link Flux} of projetos.
     */
    @GetMapping(value = "/projetos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Projeto> getAllProjetosAsStream() {
        log.debug("REST request to get all Projetos as a stream");
        return projetoRepository.findAll();
    }

    /**
     * {@code GET  /projetos/:id} : get the "id" projeto.
     *
     * @param id the id of the projeto to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projeto, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/projetos/{id}")
    public Mono<ResponseEntity<Projeto>> getProjeto(@PathVariable Long id) {
        log.debug("REST request to get Projeto : {}", id);
        Mono<Projeto> projeto = projetoRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(projeto);
    }

    /**
     * {@code DELETE  /projetos/:id} : delete the "id" projeto.
     *
     * @param id the id of the projeto to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/projetos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteProjeto(@PathVariable Long id) {
        log.debug("REST request to delete Projeto : {}", id);
        return projetoRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
