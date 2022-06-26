package br.com.rnati.pageboard.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class UsuarioSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("login", table, columnPrefix + "_login"));
        columns.add(Column.aliased("email", table, columnPrefix + "_email"));
        columns.add(Column.aliased("foto", table, columnPrefix + "_foto"));
        columns.add(Column.aliased("foto_content_type", table, columnPrefix + "_foto_content_type"));

        columns.add(Column.aliased("pessoa_id", table, columnPrefix + "_pessoa_id"));
        return columns;
    }
}
