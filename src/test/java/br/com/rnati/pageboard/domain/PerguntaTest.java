package br.com.rnati.pageboard.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.rnati.pageboard.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PerguntaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Pergunta.class);
        Pergunta pergunta1 = new Pergunta();
        pergunta1.setId(1L);
        Pergunta pergunta2 = new Pergunta();
        pergunta2.setId(pergunta1.getId());
        assertThat(pergunta1).isEqualTo(pergunta2);
        pergunta2.setId(2L);
        assertThat(pergunta1).isNotEqualTo(pergunta2);
        pergunta1.setId(null);
        assertThat(pergunta1).isNotEqualTo(pergunta2);
    }
}
