package br.com.rnati.pageboard.repository;

import br.com.rnati.pageboard.domain.Paragrafo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Paragrafo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ParagrafoRepository extends ReactiveCrudRepository<Paragrafo, Long>, ParagrafoRepositoryInternal {
    @Override
    Mono<Paragrafo> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Paragrafo> findAllWithEagerRelationships();

    @Override
    Flux<Paragrafo> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM paragrafo entity WHERE entity.pagina_id = :id")
    Flux<Paragrafo> findByPagina(Long id);

    @Query("SELECT * FROM paragrafo entity WHERE entity.pagina_id IS NULL")
    Flux<Paragrafo> findAllWherePaginaIsNull();

    @Override
    <S extends Paragrafo> Mono<S> save(S entity);

    @Override
    Flux<Paragrafo> findAll();

    @Override
    Mono<Paragrafo> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ParagrafoRepositoryInternal {
    <S extends Paragrafo> Mono<S> save(S entity);

    Flux<Paragrafo> findAllBy(Pageable pageable);

    Flux<Paragrafo> findAll();

    Mono<Paragrafo> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Paragrafo> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Paragrafo> findOneWithEagerRelationships(Long id);

    Flux<Paragrafo> findAllWithEagerRelationships();

    Flux<Paragrafo> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
