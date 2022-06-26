package br.com.rnati.pageboard.repository.rowmapper;

import br.com.rnati.pageboard.domain.Assunto;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Assunto}, with proper type conversions.
 */
@Service
public class AssuntoRowMapper implements BiFunction<Row, String, Assunto> {

    private final ColumnConverter converter;

    public AssuntoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Assunto} stored in the database.
     */
    @Override
    public Assunto apply(Row row, String prefix) {
        Assunto entity = new Assunto();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNome(converter.fromRow(row, prefix + "_nome", String.class));
        return entity;
    }
}
