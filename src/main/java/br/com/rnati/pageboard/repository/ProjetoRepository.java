package br.com.rnati.pageboard.repository;

import br.com.rnati.pageboard.domain.Projeto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Projeto entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjetoRepository extends ReactiveCrudRepository<Projeto, Long>, ProjetoRepositoryInternal {
    @Query("SELECT * FROM projeto entity WHERE entity.usuario_id = :id")
    Flux<Projeto> findByUsuario(Long id);

    @Query("SELECT * FROM projeto entity WHERE entity.usuario_id IS NULL")
    Flux<Projeto> findAllWhereUsuarioIsNull();

    @Override
    <S extends Projeto> Mono<S> save(S entity);

    @Override
    Flux<Projeto> findAll();

    @Override
    Mono<Projeto> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProjetoRepositoryInternal {
    <S extends Projeto> Mono<S> save(S entity);

    Flux<Projeto> findAllBy(Pageable pageable);

    Flux<Projeto> findAll();

    Mono<Projeto> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Projeto> findAllBy(Pageable pageable, Criteria criteria);

}
