package br.com.rnati.pageboard.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import br.com.rnati.pageboard.domain.Estado;
import br.com.rnati.pageboard.repository.rowmapper.EstadoRowMapper;
import br.com.rnati.pageboard.repository.rowmapper.PaisRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Estado entity.
 */
@SuppressWarnings("unused")
class EstadoRepositoryInternalImpl extends SimpleR2dbcRepository<Estado, Long> implements EstadoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PaisRowMapper paisMapper;
    private final EstadoRowMapper estadoMapper;

    private static final Table entityTable = Table.aliased("estado", EntityManager.ENTITY_ALIAS);
    private static final Table paisTable = Table.aliased("pais", "pais");

    public EstadoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PaisRowMapper paisMapper,
        EstadoRowMapper estadoMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Estado.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.paisMapper = paisMapper;
        this.estadoMapper = estadoMapper;
    }

    @Override
    public Flux<Estado> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Estado> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = EstadoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PaisSqlHelper.getColumns(paisTable, "pais"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(paisTable)
            .on(Column.create("pais_id", entityTable))
            .equals(Column.create("id", paisTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Estado.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Estado> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Estado> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Estado> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Estado> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Estado> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Estado process(Row row, RowMetadata metadata) {
        Estado entity = estadoMapper.apply(row, "e");
        entity.setPais(paisMapper.apply(row, "pais"));
        return entity;
    }

    @Override
    public <S extends Estado> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
