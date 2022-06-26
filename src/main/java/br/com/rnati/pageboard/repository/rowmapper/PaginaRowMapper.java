package br.com.rnati.pageboard.repository.rowmapper;

import br.com.rnati.pageboard.domain.Pagina;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Pagina}, with proper type conversions.
 */
@Service
public class PaginaRowMapper implements BiFunction<Row, String, Pagina> {

    private final ColumnConverter converter;

    public PaginaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Pagina} stored in the database.
     */
    @Override
    public Pagina apply(Row row, String prefix) {
        Pagina entity = new Pagina();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNumero(converter.fromRow(row, prefix + "_numero", Integer.class));
        entity.setTexto(converter.fromRow(row, prefix + "_texto", String.class));
        entity.setPlanoDeAula(converter.fromRow(row, prefix + "_plano_de_aula", String.class));
        entity.setImagemContentType(converter.fromRow(row, prefix + "_imagem_content_type", String.class));
        entity.setImagem(converter.fromRow(row, prefix + "_imagem", byte[].class));
        entity.setLivroId(converter.fromRow(row, prefix + "_livro_id", Long.class));
        return entity;
    }
}
