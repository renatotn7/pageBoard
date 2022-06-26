package br.com.rnati.pageboard.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ProjetoSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("nome", table, columnPrefix + "_nome"));
        columns.add(Column.aliased("descricao", table, columnPrefix + "_descricao"));
        columns.add(Column.aliased("imagem", table, columnPrefix + "_imagem"));
        columns.add(Column.aliased("imagem_content_type", table, columnPrefix + "_imagem_content_type"));
        columns.add(Column.aliased("tags", table, columnPrefix + "_tags"));

        columns.add(Column.aliased("usuario_id", table, columnPrefix + "_usuario_id"));
        return columns;
    }
}
