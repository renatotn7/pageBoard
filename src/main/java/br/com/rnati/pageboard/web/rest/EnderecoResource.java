package br.com.rnati.pageboard.web.rest;

import br.com.rnati.pageboard.domain.Endereco;
import br.com.rnati.pageboard.repository.EnderecoRepository;
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
 * REST controller for managing {@link br.com.rnati.pageboard.domain.Endereco}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class EnderecoResource {

    private final Logger log = LoggerFactory.getLogger(EnderecoResource.class);

    private static final String ENTITY_NAME = "endereco";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EnderecoRepository enderecoRepository;

    public EnderecoResource(EnderecoRepository enderecoRepository) {
        this.enderecoRepository = enderecoRepository;
    }

    /**
     * {@code POST  /enderecos} : Create a new endereco.
     *
     * @param endereco the endereco to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new endereco, or with status {@code 400 (Bad Request)} if the endereco has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/enderecos")
    public Mono<ResponseEntity<Endereco>> createEndereco(@RequestBody Endereco endereco) throws URISyntaxException {
        log.debug("REST request to save Endereco : {}", endereco);
        if (endereco.getId() != null) {
            throw new BadRequestAlertException("A new endereco cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return enderecoRepository
            .save(endereco)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/enderecos/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /enderecos/:id} : Updates an existing endereco.
     *
     * @param id the id of the endereco to save.
     * @param endereco the endereco to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated endereco,
     * or with status {@code 400 (Bad Request)} if the endereco is not valid,
     * or with status {@code 500 (Internal Server Error)} if the endereco couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/enderecos/{id}")
    public Mono<ResponseEntity<Endereco>> updateEndereco(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Endereco endereco
    ) throws URISyntaxException {
        log.debug("REST request to update Endereco : {}, {}", id, endereco);
        if (endereco.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, endereco.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return enderecoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return enderecoRepository
                    .save(endereco)
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
     * {@code PATCH  /enderecos/:id} : Partial updates given fields of an existing endereco, field will ignore if it is null
     *
     * @param id the id of the endereco to save.
     * @param endereco the endereco to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated endereco,
     * or with status {@code 400 (Bad Request)} if the endereco is not valid,
     * or with status {@code 404 (Not Found)} if the endereco is not found,
     * or with status {@code 500 (Internal Server Error)} if the endereco couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/enderecos/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Endereco>> partialUpdateEndereco(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Endereco endereco
    ) throws URISyntaxException {
        log.debug("REST request to partial update Endereco partially : {}, {}", id, endereco);
        if (endereco.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, endereco.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return enderecoRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Endereco> result = enderecoRepository
                    .findById(endereco.getId())
                    .map(existingEndereco -> {
                        if (endereco.getLogradouro() != null) {
                            existingEndereco.setLogradouro(endereco.getLogradouro());
                        }
                        if (endereco.getNumero() != null) {
                            existingEndereco.setNumero(endereco.getNumero());
                        }
                        if (endereco.getComplemento() != null) {
                            existingEndereco.setComplemento(endereco.getComplemento());
                        }
                        if (endereco.getBairro() != null) {
                            existingEndereco.setBairro(endereco.getBairro());
                        }
                        if (endereco.getcEP() != null) {
                            existingEndereco.setcEP(endereco.getcEP());
                        }

                        return existingEndereco;
                    })
                    .flatMap(enderecoRepository::save);

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
     * {@code GET  /enderecos} : get all the enderecos.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of enderecos in body.
     */
    @GetMapping("/enderecos")
    public Mono<List<Endereco>> getAllEnderecos(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Enderecos");
        return enderecoRepository.findAllWithEagerRelationships().collectList();
    }

    /**
     * {@code GET  /enderecos} : get all the enderecos as a stream.
     * @return the {@link Flux} of enderecos.
     */
    @GetMapping(value = "/enderecos", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Endereco> getAllEnderecosAsStream() {
        log.debug("REST request to get all Enderecos as a stream");
        return enderecoRepository.findAll();
    }

    /**
     * {@code GET  /enderecos/:id} : get the "id" endereco.
     *
     * @param id the id of the endereco to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the endereco, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/enderecos/{id}")
    public Mono<ResponseEntity<Endereco>> getEndereco(@PathVariable Long id) {
        log.debug("REST request to get Endereco : {}", id);
        Mono<Endereco> endereco = enderecoRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(endereco);
    }

    /**
     * {@code DELETE  /enderecos/:id} : delete the "id" endereco.
     *
     * @param id the id of the endereco to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/enderecos/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteEndereco(@PathVariable Long id) {
        log.debug("REST request to delete Endereco : {}", id);
        return enderecoRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
