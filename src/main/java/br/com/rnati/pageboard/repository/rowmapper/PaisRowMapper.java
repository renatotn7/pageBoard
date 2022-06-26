package br.com.rnati.pageboard.repository.rowmapper;

import br.com.rnati.pageboard.domain.Pais;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Pais}, with proper type conversions.
 */
@Service
public class PaisRowMapper implements BiFunction<Row, String, Pais> {

    private final ColumnConverter converter;

    public PaisRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Pais} stored in the database.
     */
    @Override
    public Pais apply(Row row, String prefix) {
        Pais entity = new Pais();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNome(converter.fromRow(row, prefix + "_nome", String.class));
        entity.setSigla(converter.fromRow(row, prefix + "_sigla", String.class));
        return entity;
    }
}
