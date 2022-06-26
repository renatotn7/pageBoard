package br.com.rnati.pageboard.repository;

import br.com.rnati.pageboard.domain.Pagina;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Pagina entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PaginaRepository extends ReactiveCrudRepository<Pagina, Long>, PaginaRepositoryInternal {
    @Query("SELECT * FROM pagina entity WHERE entity.livro_id = :id")
    Flux<Pagina> findByLivro(Long id);

    @Query("SELECT * FROM pagina entity WHERE entity.livro_id IS NULL")
    Flux<Pagina> findAllWhereLivroIsNull();

    @Override
    <S extends Pagina> Mono<S> save(S entity);

    @Override
    Flux<Pagina> findAll();

    @Override
    Mono<Pagina> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PaginaRepositoryInternal {
    <S extends Pagina> Mono<S> save(S entity);

    Flux<Pagina> findAllBy(Pageable pageable);

    Flux<Pagina> findAll();

    Mono<Pagina> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Pagina> findAllBy(Pageable pageable, Criteria criteria);

}
