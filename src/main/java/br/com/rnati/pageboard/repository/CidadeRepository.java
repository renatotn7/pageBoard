package br.com.rnati.pageboard.repository;

import br.com.rnati.pageboard.domain.Cidade;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Cidade entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CidadeRepository extends ReactiveCrudRepository<Cidade, Long>, CidadeRepositoryInternal {
    @Override
    Mono<Cidade> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Cidade> findAllWithEagerRelationships();

    @Override
    Flux<Cidade> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM cidade entity WHERE entity.estado_id = :id")
    Flux<Cidade> findByEstado(Long id);

    @Query("SELECT * FROM cidade entity WHERE entity.estado_id IS NULL")
    Flux<Cidade> findAllWhereEstadoIsNull();

    @Override
    <S extends Cidade> Mono<S> save(S entity);

    @Override
    Flux<Cidade> findAll();

    @Override
    Mono<Cidade> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CidadeRepositoryInternal {
    <S extends Cidade> Mono<S> save(S entity);

    Flux<Cidade> findAllBy(Pageable pageable);

    Flux<Cidade> findAll();

    Mono<Cidade> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Cidade> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Cidade> findOneWithEagerRelationships(Long id);

    Flux<Cidade> findAllWithEagerRelationships();

    Flux<Cidade> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
