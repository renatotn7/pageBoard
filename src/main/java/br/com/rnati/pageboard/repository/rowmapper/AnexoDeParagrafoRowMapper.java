package br.com.rnati.pageboard.repository.rowmapper;

import br.com.rnati.pageboard.domain.AnexoDeParagrafo;
import br.com.rnati.pageboard.domain.enumeration.TipoAnexoDeParagrafo;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link AnexoDeParagrafo}, with proper type conversions.
 */
@Service
public class AnexoDeParagrafoRowMapper implements BiFunction<Row, String, AnexoDeParagrafo> {

    private final ColumnConverter converter;

    public AnexoDeParagrafoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link AnexoDeParagrafo} stored in the database.
     */
    @Override
    public AnexoDeParagrafo apply(Row row, String prefix) {
        AnexoDeParagrafo entity = new AnexoDeParagrafo();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setTipo(converter.fromRow(row, prefix + "_tipo", TipoAnexoDeParagrafo.class));
        entity.setValue(converter.fromRow(row, prefix + "_value", String.class));
        entity.setParagrafoId(converter.fromRow(row, prefix + "_paragrafo_id", Long.class));
        return entity;
    }
}
