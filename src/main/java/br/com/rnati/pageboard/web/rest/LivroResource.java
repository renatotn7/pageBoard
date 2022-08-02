package br.com.rnati.pageboard.web.rest;

import br.com.rnati.pageboard.domain.Livro;
import br.com.rnati.pageboard.repository.LivroRepository;
import br.com.rnati.pageboard.web.rest.errors.BadRequestAlertException;
import io.jsonwebtoken.lang.Collections;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
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
@Order
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
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with
     *         body the new livro, or with status {@code 400 (Bad Request)} if the
     *         livro has already an ID.
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
                                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME,
                                        result.getId().toString()))
                                .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    /**
     * {@code PUT  /livros/:id} : Updates an existing livro.
     *
     * @param id    the id of the livro to save.
     * @param livro the livro to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated livro,
     *         or with status {@code 400 (Bad Request)} if the livro is not valid,
     *         or with status {@code 500 (Internal Server Error)} if the livro
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/livros/{id}")
    public Mono<ResponseEntity<Livro>> updateLivro(@PathVariable(value = "id", required = false) final Long id,
            @RequestBody Livro livro)
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
                            .map(result -> ResponseEntity
                                    .ok()
                                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                                            result.getId().toString()))
                                    .body(result));
                });
    }

    /**
     * {@code PATCH  /livros/:id} : Partial updates given fields of an existing
     * livro, field will ignore if it is null
     *
     * @param id    the id of the livro to save.
     * @param livro the livro to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the updated livro,
     *         or with status {@code 400 (Bad Request)} if the livro is not valid,
     *         or with status {@code 404 (Not Found)} if the livro is not found,
     *         or with status {@code 500 (Internal Server Error)} if the livro
     *         couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/livros/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Livro>> partialUpdateLivro(
            @PathVariable(value = "id", required = false) final Long id,
            @RequestBody Livro livro) throws URISyntaxException {
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
                            .map(res -> ResponseEntity
                                    .ok()
                                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME,
                                            res.getId().toString()))
                                    .body(res));
                });
    }

    /**
     * {@code GET  /livros} : get all the livros.
     *
     * @param eagerload flag to eager load entities from relationships (This is
     *                  applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list
     *         of livros in body.
     */
    @GetMapping("/livros")
    public Mono<List<Livro>> getAllLivros(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Livros");
        return livroRepository.findAllWithEagerRelationships().collectList();
    }

    /**
     * {@code GET  /livros} : get all the livros as a stream.
     * 
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
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the livro, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/livros/{id}")
    public Mono<ResponseEntity<Livro>> getLivro(@PathVariable Long id) {
        log.debug("REST request to get Livro : {}", id);
        Mono<Livro> livro = livroRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(livro);
    }

    /**
     * {@code GET /livros/searchByTagsAnd/{tags} : get the "livros with tags" in
     * 'and' mode .
     *
     * @param tags the tags of the livros to retrieve.
     * 
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the livro, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/livros/searchByTagsAnd/{tags}")
    public Mono<ResponseEntity<List<Livro>>> getLivroByTagsAnd(@PathVariable String tags) {

        log.debug("REST request to get Livros by tags : {}", tags);
        System.out.println("tags******" + tags);
        Mono<List<Livro>> livros = livroRepository.findAllWithEagerRelationships().collectList();
        System.out.println("getLivrosPassed");

        Function<List<Livro>, List<Livro>> mapper = listLivro -> processaLivrosAnd(tags, listLivro);

        // Mono
        Mono<List<Livro>> livrosMonofinal = livros.map(mapper);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("2");

        System.out.println("***********fim");
        return ResponseUtil.wrapOrNotFound(livrosMonofinal);
    }

    public List<Livro> processaLivrosAnd(String tags, List<Livro> livros) {
        // List<Livro> livros1=livros.
        System.out.println("******************************************************" + tags);
        List<Livro> finalList = new ArrayList<Livro>();
        for (Livro livro : livros) {
            if (inAnd(livro.getTags(), tags)) {
                finalList.add(livro);
                System.out.println("*****************************************************add");
            } else {
                System.out.println("*****************************************************remove");
                // livros.remove(livro);
            }
        }
        return finalList;
    }

    /**
     * {@code GET /livros/searchByTexto/{texto} : get the "livros with texto"
     * 
     *
     * @param texto the texto of the livros to retrieve.
     * 
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the livro, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/livros/searchByTexto/{texto}")
    public Mono<ResponseEntity<List<Livro>>> findByLikeTexto(@PathVariable String texto) {
        Mono<List<Livro>> livros = livroRepository.findByLikeTexto(texto).collectList();
        return ResponseUtil.wrapOrNotFound(livros);
    }

    /**
     * {@code GET /livros/searchByTagsAnd/{tags} : get the "livros with tags" in
     * 'or' mode .
     *
     * @param tags the tags of the livros to retrieve.
     * 
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body
     *         the livro, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/livros/searchByTagsOr/{tags}")
    public Mono<ResponseEntity<List<Livro>>> getLivroByTagsOr(@PathVariable String tags) {
        log.debug("REST request to get Livros by tags : {}", tags);
        System.out.println("tags******" + tags);
        Mono<List<Livro>> livros = livroRepository.findAllWithEagerRelationships().collectList();
        System.out.println("getLivrosPassed");

        Function<List<Livro>, List<Livro>> mapper = listLivro -> processaLivrosOr(tags, listLivro);

        // Mono
        Mono<List<Livro>> livrosMonofinal = livros.map(mapper);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        System.out.println("2");

        System.out.println("***********fim");
        return ResponseUtil.wrapOrNotFound(livrosMonofinal);
    }

    public List<Livro> processaLivrosOr(String tags, List<Livro> livros) {
        // List<Livro> livros1=livros.
        System.out.println("******************************************************" + tags);
        List<Livro> finalList = new ArrayList<Livro>();
        for (Livro livro : livros) {
            if (inOr(livro.getTags(), tags)) {
                finalList.add(livro);
                System.out.println("*****************************************************add");
            } else {
                System.out.println("*****************************************************remove");
                // livros.remove(livro);
            }
        }
        return finalList;
    }

    public Boolean inAnd(String tagsLivro, String tagsin) {
        String[] atagsLivro = tagsLivro.split(";");
        String[] atagsin = tagsin.split(",");

        List<String> listTagsLivro = (List<String>) Collections.arrayToList(atagsLivro);

        List<String> listTagsin = (List<String>) Collections.arrayToList(atagsin);
        Boolean nencontrou = true;
        for (String tag : listTagsin) {
            if (!listTagsLivro.contains(tag)) {
                nencontrou = true;
                break;
            }

            nencontrou = false;
        }
        if (nencontrou) {
            return false;
        }
        if (!nencontrou) {
            return true;
        }
        return nencontrou;
    }

    public Boolean inOr(String tagsLivro, String tagsin) {
        String[] atagsLivro = tagsLivro.split(";");
        String[] atagsin = tagsin.split(",");
        System.out.println("or");
        List<String> listTagsLivro = (List<String>) Collections.arrayToList(atagsLivro);
        List<String> listTagsin = (List<String>) Collections.arrayToList(atagsin);
        for (String tag : listTagsin) {
            if (listTagsLivro.contains(tag)) {
                return true;
            }
        }
        return false;

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
                .map(result -> ResponseEntity
                        .noContent()
                        .headers(
                                HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build());
    }
}
