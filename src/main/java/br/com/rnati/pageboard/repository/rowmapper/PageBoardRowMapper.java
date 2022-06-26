package br.com.rnati.pageboard.repository.rowmapper;

import br.com.rnati.pageboard.domain.PageBoard;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link PageBoard}, with proper type conversions.
 */
@Service
public class PageBoardRowMapper implements BiFunction<Row, String, PageBoard> {

    private final ColumnConverter converter;

    public PageBoardRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link PageBoard} stored in the database.
     */
    @Override
    public PageBoard apply(Row row, String prefix) {
        PageBoard entity = new PageBoard();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        return entity;
    }
}
