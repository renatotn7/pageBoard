package br.com.rnati.pageboard.repository.rowmapper;

import br.com.rnati.pageboard.domain.Pergunta;
import io.r2dbc.spi.Row;
import java.util.function.BiFunction;
import org.springframework.stereotype.Service;

/**
 * Converter between {@link Row} to {@link Pergunta}, with proper type conversions.
 */
@Service
public class PerguntaRowMapper implements BiFunction<Row, String, Pergunta> {

    private final ColumnConverter converter;

    public PerguntaRowMapper(ColumnConverter converter) {
        this.converter = converter;
    }

    /**
     * Take a {@link Row} and a column prefix, and extract all the fields.
     * @return the {@link Pergunta} stored in the database.
     */
    @Override
    public Pergunta apply(Row row, String prefix) {
        Pergunta entity = new Pergunta();
        entity.setId(converter.fromRow(row, prefix + "_id", Long.class));
        entity.setQuestao(converter.fromRow(row, prefix + "_questao", String.class));
        entity.setResposta(converter.fromRow(row, prefix + "_resposta", String.class));
        entity.setParagrafoId(converter.fromRow(row, prefix + "_paragrafo_id", Long.class));
        return entity;
    }
}
