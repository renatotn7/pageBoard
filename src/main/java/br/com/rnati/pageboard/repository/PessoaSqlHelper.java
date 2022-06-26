package br.com.rnati.pageboard.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class PessoaSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("nome", table, columnPrefix + "_nome"));
        columns.add(Column.aliased("data_de_nascimento", table, columnPrefix + "_data_de_nascimento"));

        return columns;
    }
}
