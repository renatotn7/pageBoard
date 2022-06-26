package br.com.rnati.pageboard.repository;

import java.util.ArrayList;
import java.util.List;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Table;

public class LivroSqlHelper {

    public static List<Expression> getColumns(Table table, String columnPrefix) {
        List<Expression> columns = new ArrayList<>();
        columns.add(Column.aliased("id", table, columnPrefix + "_id"));
        columns.add(Column.aliased("nome_livro", table, columnPrefix + "_nome_livro"));
        columns.add(Column.aliased("editora", table, columnPrefix + "_editora"));
        columns.add(Column.aliased("autor", table, columnPrefix + "_autor"));
        columns.add(Column.aliased("ano_de_publicacao", table, columnPrefix + "_ano_de_publicacao"));
        columns.add(Column.aliased("tags", table, columnPrefix + "_tags"));

        columns.add(Column.aliased("assunto_id", table, columnPrefix + "_assunto_id"));
        return columns;
    }
}
