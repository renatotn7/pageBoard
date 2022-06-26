package br.com.rnati.pageboard.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import br.com.rnati.pageboard.domain.AnexoDeParagrafo;
import br.com.rnati.pageboard.domain.enumeration.TipoAnexoDeParagrafo;
import br.com.rnati.pageboard.repository.rowmapper.AnexoDeParagrafoRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the AnexoDeParagrafo entity.
 */
@SuppressWarnings("unused")
class AnexoDeParagrafoRepositoryInternalImpl
    extends SimpleR2dbcRepository<AnexoDeParagrafo, Long>
    implements AnexoDeParagrafoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ParagrafoRowMapper paragrafoMapper;
    private final AnexoDeParagrafoRowMapper anexodeparagrafoMapper;

    private static final Table entityTable = Table.aliased("anexo_de_paragrafo", EntityManager.ENTITY_ALIAS);
    private static final Table paragrafoTable = Table.aliased("paragrafo", "paragrafo");

    public AnexoDeParagrafoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ParagrafoRowMapper paragrafoMapper,
        AnexoDeParagrafoRowMapper anexodeparagrafoMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(AnexoDeParagrafo.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.paragrafoMapper = paragrafoMapper;
        this.anexodeparagrafoMapper = anexodeparagrafoMapper;
    }

    @Override
    public Flux<AnexoDeParagrafo> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<AnexoDeParagrafo> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = AnexoDeParagrafoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ParagrafoSqlHelper.getColumns(paragrafoTable, "paragrafo"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(paragrafoTable)
            .on(Column.create("paragrafo_id", entityTable))
            .equals(Column.create("id", paragrafoTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, AnexoDeParagrafo.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<AnexoDeParagrafo> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<AnexoDeParagrafo> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private AnexoDeParagrafo process(Row row, RowMetadata metadata) {
        AnexoDeParagrafo entity = anexodeparagrafoMapper.apply(row, "e");
        entity.setParagrafo(paragrafoMapper.apply(row, "paragrafo"));
        return entity;
    }

    @Override
    public <S extends AnexoDeParagrafo> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
