package br.com.rnati.pageboard.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PerguntaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("questao", table, columnPrefix + "_questao"));
        columns.add(Column.aliased("resposta", table, columnPrefix + "_resposta"));

        columns.add(Column.aliased("paragrafo_id", table, columnPrefix + "_paragrafo_id"));
        return columns;
    }
}
