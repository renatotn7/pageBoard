package br.com.rnati.pageboard.repository.rowmapper;

import br.com.rnati.pageboard.domain.Paragrafo;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Paragrafo}, with proper type conversions.
 */
@Service
public class ParagrafoRowMapper implements BiFunction<Row, String, Paragrafo> {

    private final ColumnConverter converter;

    public ParagrafoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Paragrafo} stored in the database.
     */
    @Override
    public Paragrafo apply(Row row, String prefix) {
        Paragrafo entity = new Paragrafo();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNumero(converter.fromRow(row, prefix + "_numero", Integer.class));
        entity.setTexto(converter.fromRow(row, prefix + "_texto", String.class));
        entity.setResumo(converter.fromRow(row, prefix + "_resumo", String.class));
        entity.setPaginaId(converter.fromRow(row, prefix + "_pagina_id", Long.class));
        return entity;
    }
}
