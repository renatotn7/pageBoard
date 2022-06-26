package br.com.rnati.pageboard.repository;

import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Query.query;

import br.com.rnati.pageboard.domain.Livro;
import br.com.rnati.pageboard.domain.Projeto;
import br.com.rnati.pageboard.repository.rowmapper.AssuntoRowMapper;
import br.com.rnati.pageboard.repository.rowmapper.LivroRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.BiFunction;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data SQL reactive custom repository implementation for the Livro entity.
 */
@SuppressWarnings("unused")
class LivroRepositoryInternalImpl extends SimpleR2dbcRepository<Livro, Long> implements LivroRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final AssuntoRowMapper assuntoMapper;
    private final LivroRowMapper livroMapper;

    private static final Table entityTable = Table.aliased("livro", EntityManager.ENTITY_ALIAS);
    private static final Table assuntoTable = Table.aliased("assunto", "assunto");

    private static final EntityManager.LinkTable projetoLink = new EntityManager.LinkTable("rel_livro__projeto", "livro_id", "projeto_id");

    public LivroRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        AssuntoRowMapper assuntoMapper,
        LivroRowMapper livroMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Livro.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.assuntoMapper = assuntoMapper;
        this.livroMapper = livroMapper;
    }

    @Override
    public Flux<Livro> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Livro> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = LivroSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(AssuntoSqlHelper.getColumns(assuntoTable, "assunto"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(assuntoTable)
            .on(Column.create("assunto_id", entityTable))
            .equals(Column.create("id", assuntoTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Livro.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Livro> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Livro> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Livro> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Livro> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Livro> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Livro process(Row row, RowMetadata metadata) {
        Livro entity = livroMapper.apply(row, "e");
        entity.setAssunto(assuntoMapper.apply(row, "assunto"));
        return entity;
    }

    @Override
    public <S extends Livro> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Livro> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(projetoLink, entity.getId(), entity.getProjetos().stream().map(Projeto::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(projetoLink, entityId);
    }
}
