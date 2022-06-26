package br.com.rnati.pageboard.web.rest;

import br.com.rnati.pageboard.domain.Livro;
import br.com.rnati.pageboard.repository.LivroRepository;
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
 * REST controller for managing {@link br.com.rnati.pageboard.domain.Livro}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class LivroResource {

    private final Logger log = LoggerFactory.getLogger(LivroResource.class);

    private static final String ENTITY_NAME = "livro";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LivroRepository livroRepository;

    public LivroResource(LivroRepository livroRepository) {
        this.livroRepository = livroRepository;
    }

    /**
     * {@code POST  /livros} : Create a new livro.
     *
     * @param livro the livro to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new livro, or with status {@code 400 (Bad Request)} if the livro has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/livros")
    public Mono<ResponseEntity<Livro>> createLivro(@RequestBody Livro livro) throws URISyntaxException {
        log.debug("REST request to save Livro : {}", livro);
        if (livro.getId() != null) {
            throw new BadRequestAlertException("A new livro cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return livroRepository
            .save(livro)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/livros/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /livros/:id} : Updates an existing livro.
     *
     * @param id the id of the livro to save.
     * @param livro the livro to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated livro,
     * or with status {@code 400 (Bad Request)} if the livro is not valid,
     * or with status {@code 500 (Internal Server Error)} if the livro couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/livros/{id}")
    public Mono<ResponseEntity<Livro>> updateLivro(@PathVariable(value = "id", required = false) final Long id, @RequestBody Livro livro)
        throws URISyntaxException {
        log.debug("REST request to update Livro : {}, {}", id, livro);
        if (livro.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, livro.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return livroRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return livroRepository
                    .save(livro)
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
     * {@code PATCH  /livros/:id} : Partial updates given fields of an existing livro, field will ignore if it is null
     *
     * @param id the id of the livro to save.
     * @param livro the livro to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated livro,
     * or with status {@code 400 (Bad Request)} if the livro is not valid,
     * or with status {@code 404 (Not Found)} if the livro is not found,
     * or with status {@code 500 (Internal Server Error)} if the livro couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/livros/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Livro>> partialUpdateLivro(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Livro livro
    ) throws URISyntaxException {
        log.debug("REST request to partial update Livro partially : {}, {}", id, livro);
        if (livro.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, livro.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return livroRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Livro> result = livroRepository
                    .findById(livro.getId())
                    .map(existingLivro -> {
                        if (livro.getNomeLivro() != null) {
                            existingLivro.setNomeLivro(livro.getNomeLivro());
                        }
                        if (livro.getEditora() != null) {
                            existingLivro.setEditora(livro.getEditora());
                        }
                        if (livro.getAutor() != null) {
                            existingLivro.setAutor(livro.getAutor());
                        }
                        if (livro.getAnoDePublicacao() != null) {
                            existingLivro.setAnoDePublicacao(livro.getAnoDePublicacao());
                        }
                        if (livro.getTags() != null) {
                            existingLivro.setTags(livro.getTags());
                        }

                        return existingLivro;
                    })
                    .flatMap(livroRepository::save);

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
     * {@code GET  /livros} : get all the livros.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of livros in body.
     */
    @GetMapping("/livros")
    public Mono<List<Livro>> getAllLivros(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Livros");
        return livroRepository.findAllWithEagerRelationships().collectList();
    }

    /**
     * {@code GET  /livros} : get all the livros as a stream.
     * @return the {@link Flux} of livros.
     */
    @GetMapping(value = "/livros", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Livro> getAllLivrosAsStream() {
        log.debug("REST request to get all Livros as a stream");
        return livroRepository.findAll();
    }

    /**
     * {@code GET  /livros/:id} : get the "id" livro.
     *
     * @param id the id of the livro to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the livro, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/livros/{id}")
    public Mono<ResponseEntity<Livro>> getLivro(@PathVariable Long id) {
        log.debug("REST request to get Livro : {}", id);
        Mono<Livro> livro = livroRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(livro);
    }

    /**
     * {@code DELETE  /livros/:id} : delete the "id" livro.
     *
     * @param id the id of the livro to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/livros/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteLivro(@PathVariable Long id) {
        log.debug("REST request to delete Livro : {}", id);
        return livroRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
