package br.com.rnati.pageboard.repository.rowmapper;

import br.com.rnati.pageboard.domain.Estado;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Estado}, with proper type conversions.
 */
@Service
public class EstadoRowMapper implements BiFunction<Row, String, Estado> {

    private final ColumnConverter converter;

    public EstadoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Estado} stored in the database.
     */
    @Override
    public Estado apply(Row row, String prefix) {
        Estado entity = new Estado();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNome(converter.fromRow(row, prefix + "_nome", String.class));
        entity.setUf(converter.fromRow(row, prefix + "_uf", String.class));
        entity.setPaisId(converter.fromRow(row, prefix + "_pais_id", Long.class));
        return entity;
    }
}
