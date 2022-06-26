package br.com.rnati.pageboard.repository.rowmapper;

import br.com.rnati.pageboard.domain.Cidade;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Cidade}, with proper type conversions.
 */
@Service
public class CidadeRowMapper implements BiFunction<Row, String, Cidade> {

    private final ColumnConverter converter;

    public CidadeRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Cidade} stored in the database.
     */
    @Override
    public Cidade apply(Row row, String prefix) {
        Cidade entity = new Cidade();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNome(converter.fromRow(row, prefix + "_nome", String.class));
        entity.setEstadoId(converter.fromRow(row, prefix + "_estado_id", Long.class));
        return entity;
    }
}
