package br.com.rnati.pageboard.repository;

import br.com.rnati.pageboard.domain.AnexoDeParagrafo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the AnexoDeParagrafo entity.
 */
@SuppressWarnings("unused")
@Repository
public interface AnexoDeParagrafoRepository extends ReactiveCrudRepository<AnexoDeParagrafo, Long>, AnexoDeParagrafoRepositoryInternal {
    Flux<AnexoDeParagrafo> findAllBy(Pageable pageable);

    @Query("SELECT * FROM anexo_de_paragrafo entity WHERE entity.paragrafo_id = :id")
    Flux<AnexoDeParagrafo> findByParagrafo(Long id);

    @Query("SELECT * FROM anexo_de_paragrafo entity WHERE entity.paragrafo_id IS NULL")
    Flux<AnexoDeParagrafo> findAllWhereParagrafoIsNull();

    @Override
    <S extends AnexoDeParagrafo> Mono<S> save(S entity);

    @Override
    Flux<AnexoDeParagrafo> findAll();

    @Override
    Mono<AnexoDeParagrafo> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface AnexoDeParagrafoRepositoryInternal {
    <S extends AnexoDeParagrafo> Mono<S> save(S entity);

    Flux<AnexoDeParagrafo> findAllBy(Pageable pageable);

    Flux<AnexoDeParagrafo> findAll();

    Mono<AnexoDeParagrafo> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<AnexoDeParagrafo> findAllBy(Pageable pageable, Criteria criteria);

}
