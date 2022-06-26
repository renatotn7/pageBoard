package br.com.rnati.pageboard.repository;

import br.com.rnati.pageboard.domain.Endereco;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the Endereco entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EnderecoRepository extends ReactiveCrudRepository<Endereco, Long>, EnderecoRepositoryInternal {
    @Override
    Mono<Endereco> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Endereco> findAllWithEagerRelationships();

    @Override
    Flux<Endereco> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM endereco entity WHERE entity.cidade_id = :id")
    Flux<Endereco> findByCidade(Long id);

    @Query("SELECT * FROM endereco entity WHERE entity.cidade_id IS NULL")
    Flux<Endereco> findAllWhereCidadeIsNull();

    @Query("SELECT * FROM endereco entity WHERE entity.estado_id = :id")
    Flux<Endereco> findByEstado(Long id);

    @Query("SELECT * FROM endereco entity WHERE entity.estado_id IS NULL")
    Flux<Endereco> findAllWhereEstadoIsNull();

    @Query("SELECT * FROM endereco entity WHERE entity.pais_id = :id")
    Flux<Endereco> findByPais(Long id);

    @Query("SELECT * FROM endereco entity WHERE entity.pais_id IS NULL")
    Flux<Endereco> findAllWherePaisIsNull();

    @Query("SELECT * FROM endereco entity WHERE entity.pessoa_id = :id")
    Flux<Endereco> findByPessoa(Long id);

    @Query("SELECT * FROM endereco entity WHERE entity.pessoa_id IS NULL")
    Flux<Endereco> findAllWherePessoaIsNull();

    @Override
    <S extends Endereco> Mono<S> save(S entity);

    @Override
    Flux<Endereco> findAll();

    @Override
    Mono<Endereco> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EnderecoRepositoryInternal {
    <S extends Endereco> Mono<S> save(S entity);

    Flux<Endereco> findAllBy(Pageable pageable);

    Flux<Endereco> findAll();

    Mono<Endereco> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Endereco> findAllBy(Pageable pageable, Criteria criteria);

    Mono<Endereco> findOneWithEagerRelationships(Long id);

    Flux<Endereco> findAllWithEagerRelationships();

    Flux<Endereco> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
