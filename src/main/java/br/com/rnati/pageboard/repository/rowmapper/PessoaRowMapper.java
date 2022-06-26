package br.com.rnati.pageboard.repository.rowmapper;

import br.com.rnati.pageboard.domain.Pessoa;
import io.r2dbc.spi.Row;
import java.time.LocalDate;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Pessoa}, with proper type conversions.
 */
@Service
public class PessoaRowMapper implements BiFunction<Row, String, Pessoa> {

    private final ColumnConverter converter;

    public PessoaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Pessoa} stored in the database.
     */
    @Override
    public Pessoa apply(Row row, String prefix) {
        Pessoa entity = new Pessoa();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNome(converter.fromRow(row, prefix + "_nome", String.class));
        entity.setDataDeNascimento(converter.fromRow(row, prefix + "_data_de_nascimento", LocalDate.class));
        return entity;
    }
}
