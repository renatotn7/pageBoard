package br.com.rnati.pageboard.web.rest;

import br.com.rnati.pageboard.domain.Usuario;
import br.com.rnati.pageboard.repository.UsuarioRepository;
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
 * REST controller for managing {@link br.com.rnati.pageboard.domain.Usuario}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class UsuarioResource {

    private final Logger log = LoggerFactory.getLogger(UsuarioResource.class);

    private static final String ENTITY_NAME = "usuario";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final UsuarioRepository usuarioRepository;

    public UsuarioResource(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * {@code POST  /usuarios} : Create a new usuario.
     *
     * @param usuario the usuario to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new usuario, or with status {@code 400 (Bad Request)} if the usuario has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/usuarios")
    public Mono<ResponseEntity<Usuario>> createUsuario(@RequestBody Usuario usuario) throws URISyntaxException {
        log.debug("REST request to save Usuario : {}", usuario);
        if (usuario.getId() != null) {
            throw new BadRequestAlertException("A new usuario cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return usuarioRepository
            .save(usuario)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/usuarios/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /usuarios/:id} : Updates an existing usuario.
     *
     * @param id the id of the usuario to save.
     * @param usuario the usuario to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated usuario,
     * or with status {@code 400 (Bad Request)} if the usuario is not valid,
     * or with status {@code 500 (Internal Server Error)} if the usuario couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/usuarios/{id}")
    public Mono<ResponseEntity<Usuario>> updateUsuario(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Usuario usuario
    ) throws URISyntaxException {
        log.debug("REST request to update Usuario : {}, {}", id, usuario);
        if (usuario.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, usuario.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return usuarioRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return usuarioRepository
                    .save(usuario)
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
     * {@code PATCH  /usuarios/:id} : Partial updates given fields of an existing usuario, field will ignore if it is null
     *
     * @param id the id of the usuario to save.
     * @param usuario the usuario to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated usuario,
     * or with status {@code 400 (Bad Request)} if the usuario is not valid,
     * or with status {@code 404 (Not Found)} if the usuario is not found,
     * or with status {@code 500 (Internal Server Error)} if the usuario couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/usuarios/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Usuario>> partialUpdateUsuario(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Usuario usuario
    ) throws URISyntaxException {
        log.debug("REST request to partial update Usuario partially : {}, {}", id, usuario);
        if (usuario.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, usuario.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return usuarioRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Usuario> result = usuarioRepository
                    .findById(usuario.getId())
                    .map(existingUsuario -> {
                        if (usuario.getLogin() != null) {
                            existingUsuario.setLogin(usuario.getLogin());
                        }
                        if (usuario.getEmail() != null) {
                            existingUsuario.setEmail(usuario.getEmail());
                        }
                        if (usuario.getFoto() != null) {
                            existingUsuario.setFoto(usuario.getFoto());
                        }
                        if (usuario.getFotoContentType() != null) {
                            existingUsuario.setFotoContentType(usuario.getFotoContentType());
                        }

                        return existingUsuario;
                    })
                    .flatMap(usuarioRepository::save);

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
     * {@code GET  /usuarios} : get all the usuarios.
     *
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of usuarios in body.
     */
    @GetMapping("/usuarios")
    public Mono<List<Usuario>> getAllUsuarios(@RequestParam(required = false, defaultValue = "false") boolean eagerload) {
        log.debug("REST request to get all Usuarios");
        return usuarioRepository.findAllWithEagerRelationships().collectList();
    }

    /**
     * {@code GET  /usuarios} : get all the usuarios as a stream.
     * @return the {@link Flux} of usuarios.
     */
    @GetMapping(value = "/usuarios", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Usuario> getAllUsuariosAsStream() {
        log.debug("REST request to get all Usuarios as a stream");
        return usuarioRepository.findAll();
    }

    /**
     * {@code GET  /usuarios/:id} : get the "id" usuario.
     *
     * @param id the id of the usuario to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the usuario, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/usuarios/{id}")
    public Mono<ResponseEntity<Usuario>> getUsuario(@PathVariable Long id) {
        log.debug("REST request to get Usuario : {}", id);
        Mono<Usuario> usuario = usuarioRepository.findOneWithEagerRelationships(id);
        return ResponseUtil.wrapOrNotFound(usuario);
    }

    /**
     * {@code DELETE  /usuarios/:id} : delete the "id" usuario.
     *
     * @param id the id of the usuario to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/usuarios/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteUsuario(@PathVariable Long id) {
        log.debug("REST request to delete Usuario : {}", id);
        return usuarioRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }
}
