package br.com.rnati.pageboard.repository;

import static org.springframework.data.relational.core.query.Criteria.where;

import br.com.rnati.pageboard.domain.Endereco;
import br.com.rnati.pageboard.repository.rowmapper.CidadeRowMapper;
import br.com.rnati.pageboard.repository.rowmapper.EnderecoRowMapper;
import br.com.rnati.pageboard.repository.rowmapper.EstadoRowMapper;
import br.com.rnati.pageboard.repository.rowmapper.PaisRowMapper;
import br.com.rnati.pageboard.repository.rowmapper.PessoaRowMapper;
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
 * Spring Data SQL reactive custom repository implementation for the Endereco entity.
 */
@SuppressWarnings("unused")
class EnderecoRepositoryInternalImpl extends SimpleR2dbcRepository<Endereco, Long> implements EnderecoRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final CidadeRowMapper cidadeMapper;
    private final EstadoRowMapper estadoMapper;
    private final PaisRowMapper paisMapper;
    private final PessoaRowMapper pessoaMapper;
    private final EnderecoRowMapper enderecoMapper;

    private static final Table entityTable = Table.aliased("endereco", EntityManager.ENTITY_ALIAS);
    private static final Table cidadeTable = Table.aliased("cidade", "cidade");
    private static final Table estadoTable = Table.aliased("estado", "estado");
    private static final Table paisTable = Table.aliased("pais", "pais");
    private static final Table pessoaTable = Table.aliased("pessoa", "pessoa");

    public EnderecoRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        CidadeRowMapper cidadeMapper,
        EstadoRowMapper estadoMapper,
        PaisRowMapper paisMapper,
        PessoaRowMapper pessoaMapper,
        EnderecoRowMapper enderecoMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Endereco.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.cidadeMapper = cidadeMapper;
        this.estadoMapper = estadoMapper;
        this.paisMapper = paisMapper;
        this.pessoaMapper = pessoaMapper;
        this.enderecoMapper = enderecoMapper;
    }

    @Override
    public Flux<Endereco> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Endereco> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = EnderecoSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(CidadeSqlHelper.getColumns(cidadeTable, "cidade"));
        columns.addAll(EstadoSqlHelper.getColumns(estadoTable, "estado"));
        columns.addAll(PaisSqlHelper.getColumns(paisTable, "pais"));
        columns.addAll(PessoaSqlHelper.getColumns(pessoaTable, "pessoa"));
        SelectFromAndJoinCondition selectFrom = Select
            .builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(cidadeTable)
            .on(Column.create("cidade_id", entityTable))
            .equals(Column.create("id", cidadeTable))
            .leftOuterJoin(estadoTable)
            .on(Column.create("estado_id", entityTable))
            .equals(Column.create("id", estadoTable))
            .leftOuterJoin(paisTable)
            .on(Column.create("pais_id", entityTable))
            .equals(Column.create("id", paisTable))
            .leftOuterJoin(pessoaTable)
            .on(Column.create("pessoa_id", entityTable))
            .equals(Column.create("id", pessoaTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Endereco.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Endereco> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Endereco> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Endereco> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Endereco> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Endereco> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Endereco process(Row row, RowMetadata metadata) {
        Endereco entity = enderecoMapper.apply(row, "e");
        entity.setCidade(cidadeMapper.apply(row, "cidade"));
        entity.setEstado(estadoMapper.apply(row, "estado"));
        entity.setPais(paisMapper.apply(row, "pais"));
        entity.setPessoa(pessoaMapper.apply(row, "pessoa"));
        return entity;
    }

    @Override
    public <S extends Endereco> Mono<S> save(S entity) {
        return super.save(entity);
    }
}
