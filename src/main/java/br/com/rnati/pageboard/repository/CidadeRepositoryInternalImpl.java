package br.com.rnati.pageboard.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import br.com.rnati.pageboard.domain.Cidade;
import br.com.rnati.pageboard.repository.rowmapper.CidadeRowMapper;
import br.com.rnati.pageboard.repository.rowmapper.EstadoRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Cidade entity.
 */
@SuppressWarnings("unused")
class CidadeRepositoryInternalImpl extends SimpleR2dbcRepository<Cidade, Long> implements CidadeRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final EstadoRowMapper estadoMapper;
    private final CidadeRowMapper cidadeMapper;

    private static final Table entityTable = Table.aliased("cidade", EntityManager.ENTITY_ALIAS);
    private static final Table estadoTable = Table.aliased("estado", "estado");

    public CidadeRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        EstadoRowMapper estadoMapper,
        CidadeRowMapper cidadeMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Cidade.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.estadoMapper = estadoMapper;
        this.cidadeMapper = cidadeMapper;
    }

    @Override
    public Flux<Cidade> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Cidade> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CidadeSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(EstadoSqlHelper.getColumns(estadoTable, "estado"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(estadoTable)
            .on(Column.create("estado_id", entityTable))
            .equals(Column.create("id", estadoTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Cidade.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Cidade> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Cidade> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Cidade> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Cidade> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Cidade> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Cidade process(Row row, RowMetadata metadata) {
        Cidade entity = cidadeMapper.apply(row, "e");
        entity.setEstado(estadoMapper.apply(row, "estado"));
        return entity;
    }

    @Override
    public <S extends Cidade> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
