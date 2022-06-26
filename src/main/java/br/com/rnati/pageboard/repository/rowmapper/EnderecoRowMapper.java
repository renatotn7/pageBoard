package br.com.rnati.pageboard.repository.rowmapper;

import br.com.rnati.pageboard.domain.Endereco;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Endereco}, with proper type conversions.
 */
@Service
public class EnderecoRowMapper implements BiFunction<Row, String, Endereco> {

    private final ColumnConverter converter;

    public EnderecoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Endereco} stored in the database.
     */
    @Override
    public Endereco apply(Row row, String prefix) {
        Endereco entity = new Endereco();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLogradouro(converter.fromRow(row, prefix + "_logradouro", String.class));
        entity.setNumero(converter.fromRow(row, prefix + "_numero", Integer.class));
        entity.setComplemento(converter.fromRow(row, prefix + "_complemento", String.class));
        entity.setBairro(converter.fromRow(row, prefix + "_bairro", String.class));
        entity.setcEP(converter.fromRow(row, prefix + "_c_ep", String.class));
        entity.setCidadeId(converter.fromRow(row, prefix + "_cidade_id", Long.class));
        entity.setEstadoId(converter.fromRow(row, prefix + "_estado_id", Long.class));
        entity.setPaisId(converter.fromRow(row, prefix + "_pais_id", Long.class));
        entity.setPessoaId(converter.fromRow(row, prefix + "_pessoa_id", Long.class));
        return entity;
    }
}
