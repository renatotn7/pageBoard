package br.com.rnati.pageboard.repository.rowmapper;

import br.com.rnati.pageboard.domain.Projeto;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Projeto}, with proper type conversions.
 */
@Service
public class ProjetoRowMapper implements BiFunction<Row, String, Projeto> {

    private final ColumnConverter converter;

    public ProjetoRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Projeto} stored in the database.
     */
    @Override
    public Projeto apply(Row row, String prefix) {
        Projeto entity = new Projeto();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setNome(converter.fromRow(row, prefix + "_nome", String.class));
        entity.setDescricao(converter.fromRow(row, prefix + "_descricao", String.class));
        entity.setImagemContentType(converter.fromRow(row, prefix + "_imagem_content_type", String.class));
        entity.setImagem(converter.fromRow(row, prefix + "_imagem", byte[].class));
        entity.setTags(converter.fromRow(row, prefix + "_tags", String.class));
        entity.setUsuarioId(converter.fromRow(row, prefix + "_usuario_id", Long.class));
        return entity;
    }
}
