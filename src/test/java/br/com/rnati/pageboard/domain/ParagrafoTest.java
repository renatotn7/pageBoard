package br.com.rnati.pageboard.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.rnati.pageboard.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ParagrafoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Paragrafo.class);
        Paragrafo paragrafo1 = new Paragrafo();
        paragrafo1.setId(1L);
        Paragrafo paragrafo2 = new Paragrafo();
        paragrafo2.setId(paragrafo1.getId());
        assertThat(paragrafo1).isEqualTo(paragrafo2);
        paragrafo2.setId(2L);
        assertThat(paragrafo1).isNotEqualTo(paragrafo2);
        paragrafo1.setId(null);
        assertThat(paragrafo1).isNotEqualTo(paragrafo2);
    }
}
