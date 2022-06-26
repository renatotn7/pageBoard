package br.com.rnati.pageboard.repository;

import br.com.rnati.pageboard.domain.Pais;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Pais entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaisRepository extends ReactiveCrudRepository<Pais, Long>, PaisRepositoryInternal {
    @Override
    <S extends Pais> Mono<S> save(S entity);

    @Override
    Flux<Pais> findAll();

    @Override
    Mono<Pais> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PaisRepositoryInternal {
    <S extends Pais> Mono<S> save(S entity);

    Flux<Pais> findAllBy(Pageable pageable);

    Flux<Pais> findAll();

    Mono<Pais> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Pais> findAllBy(Pageable pageable, Criteria criteria);

}
