package br.com.rnati.pageboard.repository;

import br.com.rnati.pageboard.domain.PageBoard;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive repository for the PageBoard entity.
 */
@SuppressWarnings("unused")
@Repository
public interface PageBoardRepository extends ReactiveCrudRepository<PageBoard, Long>, PageBoardRepositoryInternal {
    @Override
    <S extends PageBoard> Mono<S> save(S entity);

    @Override
    Flux<PageBoard> findAll();

    @Override
    Mono<PageBoard> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface PageBoardRepositoryInternal {
    <S extends PageBoard> Mono<S> save(S entity);

    Flux<PageBoard> findAllBy(Pageable pageable);

    Flux<PageBoard> findAll();

    Mono<PageBoard> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<PageBoard> findAllBy(Pageable pageable, Criteria criteria);

}
