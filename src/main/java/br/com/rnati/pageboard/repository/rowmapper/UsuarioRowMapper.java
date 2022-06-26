package br.com.rnati.pageboard.repository.rowmapper;

import br.com.rnati.pageboard.domain.Usuario;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Usuario}, with proper type conversions.
 */
@Service
public class UsuarioRowMapper implements BiFunction<Row, String, Usuario> {

    private final ColumnConverter converter;

    public UsuarioRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Usuario} stored in the database.
     */
    @Override
    public Usuario apply(Row row, String prefix) {
        Usuario entity = new Usuario();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setLogin(converter.fromRow(row, prefix + "_login", String.class));
        entity.setEmail(converter.fromRow(row, prefix + "_email", String.class));
        entity.setFotoContentType(converter.fromRow(row, prefix + "_foto_content_type", String.class));
        entity.setFoto(converter.fromRow(row, prefix + "_foto", byte[].class));
        entity.setPessoaId(converter.fromRow(row, prefix + "_pessoa_id", Long.class));
        return entity;
    }
}
