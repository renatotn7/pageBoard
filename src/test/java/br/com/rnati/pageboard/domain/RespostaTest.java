package br.com.rnati.pageboard.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.rnati.pageboard.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RespostaTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Resposta.class);
        Resposta resposta1 = new Resposta();
        resposta1.setId(1L);
        Resposta resposta2 = new Resposta();
        resposta2.setId(resposta1.getId());
        assertThat(resposta1).isEqualTo(resposta2);
        resposta2.setId(2L);
        assertThat(resposta1).isNotEqualTo(resposta2);
        resposta1.setId(null);
        assertThat(resposta1).isNotEqualTo(resposta2);
    }
}
