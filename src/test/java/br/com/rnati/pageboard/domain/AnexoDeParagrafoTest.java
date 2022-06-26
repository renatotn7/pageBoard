package br.com.rnati.pageboard.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.rnati.pageboard.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AnexoDeParagrafoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AnexoDeParagrafo.class);
        AnexoDeParagrafo anexoDeParagrafo1 = new AnexoDeParagrafo();
        anexoDeParagrafo1.setId(1L);
        AnexoDeParagrafo anexoDeParagrafo2 = new AnexoDeParagrafo();
        anexoDeParagrafo2.setId(anexoDeParagrafo1.getId());
        assertThat(anexoDeParagrafo1).isEqualTo(anexoDeParagrafo2);
        anexoDeParagrafo2.setId(2L);
        assertThat(anexoDeParagrafo1).isNotEqualTo(anexoDeParagrafo2);
        anexoDeParagrafo1.setId(null);
        assertThat(anexoDeParagrafo1).isNotEqualTo(anexoDeParagrafo2);
    }
}
