package br.com.rnati.pageboard.repository;

import br.com.rnati.pageboard.domain.Livro;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Livro entity.
 */
@SuppressWarnings("unused")
@Repository
public interface LivroRepository extends ReactiveCrudRepository<Livro, Long>, LivroRepositoryInternal {
    @Override
    Mono<Livro> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Livro> findAllWithEagerRelationships();

    @Override
    Flux<Livro> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM livro entity JOIN rel_livro__projeto joinTable ON entity.id = joinTable.projeto_id WHERE joinTable.projeto_id = :id"
    )
    Flux<Livro> findByProjeto(Long id);

    @Query("SELECT * FROM livro entity WHERE entity.assunto_id = :id")
    Flux<Livro> findByAssunto(Long id);

    @Query("SELECT * FROM livro entity WHERE entity.assunto_id IS NULL")
    Flux<Livro> findAllWhereAssuntoIsNull();

    @Override
    <S extends Livro> Mono<S> save(S entity);

    @Override
    Flux<Livro> findAll();

    @Override
    Mono<Livro> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface LivroRepositoryInternal {
    <S extends Livro> Mono<S> save(S entity);

    Flux<Livro> findAllBy(Pageable pageable);

    Flux<Livro> findAll();

    Mono<Livro> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Livro> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Livro> findOneWithEagerRelationships(Long id);

    Flux<Livro> findAllWithEagerRelationships();

    Flux<Livro> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
