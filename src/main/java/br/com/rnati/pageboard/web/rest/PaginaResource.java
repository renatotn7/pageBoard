package br.com.rnati.pageboard.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import br.com.rnati.pageboard.client.service.NucleoClientService;
import br.com.rnati.pageboard.domain.Pagina;
import br.com.rnati.pageboard.domain.PaginaEParagrafo;
import br.com.rnati.pageboard.domain.Paragrafo;
import br.com.rnati.pageboard.enumerator.TipoAnexo;
import br.com.rnati.pageboard.repository.AnexoDeParagrafoRepository;
import br.com.rnati.pageboard.repository.PaginaRepository;
import br.com.rnati.pageboard.repository.ParagrafoRepository;
import br.com.rnati.pageboard.repository.PerguntaRepository;
import br.com.rnati.pageboard.web.rest.errors.BadRequestAlertException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link br.com.rnati.pageboard.domain.Pagina}.
 */
@RestController
@RequestMapping("/api")
@Transactional
@Order(1)
public class PaginaResource {
	NucleoClientService nucleoService;
    private final Logger log = LoggerFactory.getLogger(PaginaResource.class);

    private static final String ENTITY_NAME = "pagina";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final PaginaRepository paginaRepository;
    private final ParagrafoRepository paragrafoRepository;
    private final PerguntaRepository perguntaRepository;
    private final AnexoDeParagrafoRepository anexoRepository;
    public PaginaResource(PaginaRepository paginaRepository,ParagrafoRepository paragrafoRepository, PerguntaRepository perguntaRepository,AnexoDeParagrafoRepository anexoRepository) {
        this.paginaRepository = paginaRepository;
        this.paragrafoRepository=paragrafoRepository;
        this.perguntaRepository=perguntaRepository;
        this.anexoRepository=anexoRepository;
       nucleoService=new NucleoClientService(paginaRepository,paragrafoRepository,perguntaRepository,anexoRepository);
    }

    @PostMapping("/paginas/bloco/perguntasDiscursivas")
    public Mono<Paragrafo> perguntasDiscursivas(@RequestBody Paragrafo paragrafo) {
    	//getPagina
    	//GetParagrafo (deve ser criados quando o texto for salvo)
    	Paragrafo p = nucleoService.prepareToGetFromNucleo(paragrafo, TipoAnexo.PERGUNTARESPOSTADISC);
    	
    	return	Mono.just(p);
    
    }
    @PostMapping("/paginas/bloco/resumo")
    public Mono<Paragrafo> resumo(@RequestBody Paragrafo paragrafo) {
    	//getPagina
    	//GetParagrafo (deve ser criados quando o texto for salvo)
    	
    	return	Mono.just(nucleoService.prepareToGetFromNucleo(paragrafo, TipoAnexo.TXTSIMPLIFICADO));
    
    }
    @PostMapping("/paginas/bloco/textoSimplificado")
    public Mono<Paragrafo> textoSimplificado(@RequestBody Paragrafo paragrafo) {
    	//getPagina
    	//GetParagrafo (deve ser criados quando o texto for salvo)
    	
    	return	Mono.just(nucleoService.prepareToGetFromNucleo(paragrafo, TipoAnexo.TXTSIMPLIFICADO));
    
    }
    @PostMapping("/paginas/bloco/topicos")
    public Mono<Paragrafo> txtTopicos(@RequestBody Paragrafo paragrafo) {
    	//getPagina
    	//GetParagrafo (deve ser criados quando o texto for salvo)
    	
    	return	Mono.just(nucleoService.prepareToGetFromNucleo(paragrafo, TipoAnexo.TXTTOPICOS));
    
    }
    @PostMapping("/paginas/bloco/explicaEmTitulos")
    public Mono<Paragrafo> txtEmTitulos(@RequestBody Paragrafo paragrafo) {
    	//getPagina
    	//GetParagrafo (deve ser criados quando o texto for salvo)
    	
    	return	Mono.just(nucleoService.prepareToGetFromNucleo(paragrafo, TipoAnexo.EXPLICATEXTOCTIT));
    
    }

    @PostMapping("paginas/bloco/criaBlocosDeTexto")
    public Mono<List<Paragrafo> > criaBlocosDeTexto(@RequestBody Pagina pagina){
    		System.out.println("***********************");
    		
        	return nucleoService.getBlocos(pagina);

    }
    @PostMapping("/paragrafo/perguntasMultiplaEscolhas")
    public Mono<Paragrafo> perguntasME(@RequestBody Paragrafo paragrafo) {
    	//getPagina
    	//GetParagrafo (deve ser criados quando o texto for salvo)
    	
    	return Mono.just(nucleoService.prepareToGetFromNucleo(paragrafo, TipoAnexo.PERGUNTARESPOSTAMULT));
    	//return null;
    }
    
    /**
     * {@code POST  /paginas} : Create a new pagina.
     *
     * @param pagina the pagina to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new pagina, or with status {@code 400 (Bad Request)} if the pagina has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
  
    
    
    @PostMapping("/paginas")
    public Mono<ResponseEntity<Pagina>> createPagina(@RequestBody Pagina pagina) throws URISyntaxException {
        log.debug("REST request to save Pagina : {}", pagina);
        if (pagina.getId() != null) {
            throw new BadRequestAlertException("A new pagina cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return paginaRepository
            .save(pagina)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/paginas/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /paginas/:id} : Updates an existing pagina.
     *
     * @param id the id of the pagina to save.
     * @param pagina the pagina to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pagina,
     * or with status {@code 400 (Bad Request)} if the pagina is not valid,
     * or with status {@code 500 (Internal Server Error)} if the pagina couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/paginas/{id}")
    public Mono<ResponseEntity<Pagina>> updatePagina(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Pagina pagina
    ) throws URISyntaxException {
        log.debug("REST request to update Pagina : {}, {}", id, pagina);
        if (pagina.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pagina.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return paginaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return paginaRepository
                    .save(pagina)
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
     * {@code PATCH  /paginas/:id} : Partial updates given fields of an existing pagina, field will ignore if it is null
     *
     * @param id the id of the pagina to save.
     * @param pagina the pagina to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated pagina,
     * or with status {@code 400 (Bad Request)} if the pagina is not valid,
     * or with status {@code 404 (Not Found)} if the pagina is not found,
     * or with status {@code 500 (Internal Server Error)} if the pagina couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/paginas/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<Pagina>> partialUpdatePagina(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody Pagina pagina
    ) throws URISyntaxException {
        log.debug("REST request to partial update Pagina partially : {}, {}", id, pagina);
        if (pagina.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, pagina.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return paginaRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<Pagina> result = paginaRepository
                    .findById(pagina.getId())
                    .map(existingPagina -> {
                        if (pagina.getNumero() != null) {
                            existingPagina.setNumero(pagina.getNumero());
                        }
                        if (pagina.getTexto() != null) {
                            existingPagina.setTexto(pagina.getTexto());
                        }
                        if (pagina.getPlanoDeAula() != null) {
                            existingPagina.setPlanoDeAula(pagina.getPlanoDeAula());
                        }
                        if (pagina.getImagem() != null) {
                            existingPagina.setImagem(pagina.getImagem());
                        }
                        if (pagina.getImagemContentType() != null) {
                            existingPagina.setImagemContentType(pagina.getImagemContentType());
                        }

                        return existingPagina;
                    })
                    .flatMap(paginaRepository::save);

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
     * {@code GET  /paginas} : get all the paginas.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of paginas in body.
     */
    @GetMapping("/paginas")
    public Mono<List<Pagina>> getAllPaginas() {
        log.debug("REST request to get all Paginas");
        return paginaRepository.findAll().collectList();
    }

    /**
     * {@code GET  /paginas} : get all the paginas as a stream.
     * @return the {@link Flux} of paginas.
     */
    @GetMapping(value = "/paginas", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Pagina> getAllPaginasAsStream() {
        log.debug("REST request to get all Paginas as a stream");
        return paginaRepository.findAll();
    }

    /**
     * {@code GET  /paginas/:id} : get the "id" pagina.
     *
     * @param id the id of the pagina to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the pagina, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/paginas/{id}")
    public Mono<ResponseEntity<Pagina>> getPagina(@PathVariable Long id) {
        log.debug("REST request to get Pagina : {}", id);
        Mono<Pagina> pagina = paginaRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(pagina);
    }

    /**
     * {@code DELETE  /paginas/:id} : delete the "id" pagina.
     *
     * @param id the id of the pagina to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/paginas/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deletePagina(@PathVariable Long id) {
        log.debug("REST request to delete Pagina : {}", id);
        return paginaRepository
            .deleteById(id)
            .map(result ->
                ResponseEntity
                    .noContent()
                    .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                    .build()
            );
    }

	public PaginaRepository getPaginaRepository() {
		return paginaRepository;
	}

	public ParagrafoRepository getParagrafoRepository() {
		return paragrafoRepository;
	}

	public PerguntaRepository getPerguntaRepository() {
		return perguntaRepository;
	}
}
