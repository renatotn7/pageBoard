package br.com.rnati.pageboard.repository.rowmapper;

import br.com.rnati.pageboard.domain.Resposta;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Resposta}, with proper type conversions.
 */
@Service
public class RespostaRowMapper implements BiFunction<Row, String, Resposta> {

    private final ColumnConverter converter;

    public RespostaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Resposta} stored in the database.
     */
    @Override
    public Resposta apply(Row row, String prefix) {
        Resposta entity = new Resposta();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTexto(converter.fromRow(row, prefix + "_texto", String.class));
        return entity;
    }
}
