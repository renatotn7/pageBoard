package br.com.rnati.pageboard.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PaginaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("numero", table, columnPrefix + "_numero"));
        columns.add(Column.aliased("texto", table, columnPrefix + "_texto"));
        columns.add(Column.aliased("plano_de_aula", table, columnPrefix + "_plano_de_aula"));
        columns.add(Column.aliased("imagem", table, columnPrefix + "_imagem"));
        columns.add(Column.aliased("imagem_content_type", table, columnPrefix + "_imagem_content_type"));

        columns.add(Column.aliased("livro_id", table, columnPrefix + "_livro_id"));
        return columns;
    }
}
