package br.com.rnati.pageboard.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class AnexoDeParagrafoSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("tipo", table, columnPrefix + "_tipo"));
        columns.add(Column.aliased("value", table, columnPrefix + "_value"));

        columns.add(Column.aliased("paragrafo_id", table, columnPrefix + "_paragrafo_id"));
        return columns;
    }
}
