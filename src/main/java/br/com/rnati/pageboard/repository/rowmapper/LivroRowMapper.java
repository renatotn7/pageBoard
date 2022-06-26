package br.com.rnati.pageboard.repository.rowmapper;

import br.com.rnati.pageboard.domain.Livro;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Livro}, with proper type conversions.
 */
@Service
public class LivroRowMapper implements BiFunction<Row, String, Livro> {

    private final ColumnConverter converter;

    public LivroRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Livro} stored in the database.
     */
    @Override
    public Livro apply(Row row, String prefix) {
        Livro entity = new Livro();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNomeLivro(converter.fromRow(row, prefix + "_nome_livro", String.class));
        entity.setEditora(converter.fromRow(row, prefix + "_editora", String.class));
        entity.setAutor(converter.fromRow(row, prefix + "_autor", String.class));
        entity.setAnoDePublicacao(converter.fromRow(row, prefix + "_ano_de_publicacao", Integer.class));
        entity.setTags(converter.fromRow(row, prefix + "_tags", String.class));
        entity.setAssuntoId(converter.fromRow(row, prefix + "_assunto_id", Long.class));
        return entity;
    }
}
