package br.com.rnati.pageboard.repository;

import br.com.rnati.pageboard.domain.Pergunta;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Pergunta entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PerguntaRepository extends ReactiveCrudRepository<Pergunta, Long>, PerguntaRepositoryInternal {
    @Query("SELECT * FROM pergunta entity WHERE entity.paragrafo_id = :id")
    Flux<Pergunta> findByParagrafo(Long id);

    @Query("SELECT * FROM pergunta entity WHERE entity.paragrafo_id IS NULL")
    Flux<Pergunta> findAllWhereParagrafoIsNull();

    @Override
    <S extends Pergunta> Mono<S> save(S entity);

    @Override
    Flux<Pergunta> findAll();

    @Override
    Mono<Pergunta> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PerguntaRepositoryInternal {
    <S extends Pergunta> Mono<S> save(S entity);

    Flux<Pergunta> findAllBy(Pageable pageable);

    Flux<Pergunta> findAll();

    Mono<Pergunta> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Pergunta> findAllBy(Pageable pageable, Criteria criteria);

}
