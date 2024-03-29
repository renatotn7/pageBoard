package br.com.rnati.pageboard.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import br.com.rnati.pageboard.domain.Paragrafo;
import br.com.rnati.pageboard.repository.rowmapper.PaginaRowMapper;
import br.com.rnati.pageboard.repository.rowmapper.ParagrafoRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Paragrafo entity.
 */
@SuppressWarnings("unused")
class ParagrafoRepositoryInternalImpl extends SimpleR2dbcRepository<Paragrafo, Long> implements ParagrafoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final PaginaRowMapper paginaMapper;
    private final ParagrafoRowMapper paragrafoMapper;

    private static final Table entityTable = Table.aliased("paragrafo", EntityManager.ENTITY_ALIAS);
    private static final Table paginaTable = Table.aliased("pagina", "pagina");

    public ParagrafoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        PaginaRowMapper paginaMapper,
        ParagrafoRowMapper paragrafoMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Paragrafo.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.paginaMapper = paginaMapper;
        this.paragrafoMapper = paragrafoMapper;
    }

    @Override
    public Flux<Paragrafo> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Paragrafo> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ParagrafoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(PaginaSqlHelper.getColumns(paginaTable, "pagina"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(paginaTable)
            .on(Column.create("pagina_id", entityTable))
            .equals(Column.create("id", paginaTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Paragrafo.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Paragrafo> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Paragrafo> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Paragrafo> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Paragrafo> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Paragrafo> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Paragrafo process(Row row, RowMetadata metadata) {
        Paragrafo entity = paragrafoMapper.apply(row, "e");
        entity.setPagina(paginaMapper.apply(row, "pagina"));
        return entity;
    }

    @Override
    public <S extends Paragrafo> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
