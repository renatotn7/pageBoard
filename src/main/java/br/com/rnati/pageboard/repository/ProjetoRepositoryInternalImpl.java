package br.com.rnati.pageboard.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import br.com.rnati.pageboard.domain.Projeto;
import br.com.rnati.pageboard.repository.rowmapper.ProjetoRowMapper;
import br.com.rnati.pageboard.repository.rowmapper.UsuarioRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Projeto entity.
 */
@SuppressWarnings("unused")
class ProjetoRepositoryInternalImpl extends SimpleR2dbcRepository<Projeto, Long> implements ProjetoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UsuarioRowMapper usuarioMapper;
    private final ProjetoRowMapper projetoMapper;

    private static final Table entityTable = Table.aliased("projeto", EntityManager.ENTITY_ALIAS);
    private static final Table usuarioTable = Table.aliased("usuario", "usuario");

    public ProjetoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UsuarioRowMapper usuarioMapper,
        ProjetoRowMapper projetoMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Projeto.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.usuarioMapper = usuarioMapper;
        this.projetoMapper = projetoMapper;
    }

    @Override
    public Flux<Projeto> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Projeto> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProjetoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UsuarioSqlHelper.getColumns(usuarioTable, "usuario"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(usuarioTable)
            .on(Column.create("usuario_id", entityTable))
            .equals(Column.create("id", usuarioTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Projeto.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Projeto> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Projeto> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Projeto process(Row row, RowMetadata metadata) {
        Projeto entity = projetoMapper.apply(row, "e");
        entity.setUsuario(usuarioMapper.apply(row, "usuario"));
        return entity;
    }

    @Override
    public <S extends Projeto> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
