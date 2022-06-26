package br.com.rnati.pageboard.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class ParagrafoSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("numero", table, columnPrefix + "_numero"));
        columns.add(Column.aliased("texto", table, columnPrefix + "_texto"));
        columns.add(Column.aliased("resumo", table, columnPrefix + "_resumo"));

        columns.add(Column.aliased("pagina_id", table, columnPrefix + "_pagina_id"));
        return columns;
    }
}
