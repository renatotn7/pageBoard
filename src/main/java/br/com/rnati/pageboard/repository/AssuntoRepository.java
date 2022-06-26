package br.com.rnati.pageboard.repository;

import br.com.rnati.pageboard.domain.Assunto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Assunto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AssuntoRepository extends ReactiveCrudRepository<Assunto, Long>, AssuntoRepositoryInternal {
    @Override
    <S extends Assunto> Mono<S> save(S entity);

    @Override
    Flux<Assunto> findAll();

    @Override
    Mono<Assunto> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AssuntoRepositoryInternal {
    <S extends Assunto> Mono<S> save(S entity);

    Flux<Assunto> findAllBy(Pageable pageable);

    Flux<Assunto> findAll();

    Mono<Assunto> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Assunto> findAllBy(Pageable pageable, Criteria criteria);

}
