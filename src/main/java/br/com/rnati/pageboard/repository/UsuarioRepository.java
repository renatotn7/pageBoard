package br.com.rnati.pageboard.repository;

import br.com.rnati.pageboard.domain.Usuario;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Usuario entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UsuarioRepository extends ReactiveCrudRepository<Usuario, Long>, UsuarioRepositoryInternal {
    @Override
    Mono<Usuario> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Usuario> findAllWithEagerRelationships();

    @Override
    Flux<Usuario> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM usuario entity WHERE entity.pessoa_id = :id")
    Flux<Usuario> findByPessoa(Long id);

    @Query("SELECT * FROM usuario entity WHERE entity.pessoa_id IS NULL")
    Flux<Usuario> findAllWherePessoaIsNull();

    @Override
    <S extends Usuario> Mono<S> save(S entity);

    @Override
    Flux<Usuario> findAll();

    @Override
    Mono<Usuario> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface UsuarioRepositoryInternal {
    <S extends Usuario> Mono<S> save(S entity);

    Flux<Usuario> findAllBy(Pageable pageable);

    Flux<Usuario> findAll();

    Mono<Usuario> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Usuario> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Usuario> findOneWithEagerRelationships(Long id);

    Flux<Usuario> findAllWithEagerRelationships();

    Flux<Usuario> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
