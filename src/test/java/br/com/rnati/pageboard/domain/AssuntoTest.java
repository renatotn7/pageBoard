package br.com.rnati.pageboard.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.rnati.pageboard.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class AssuntoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Assunto.class);
        Assunto assunto1 = new Assunto();
        assunto1.setId(1L);
        Assunto assunto2 = new Assunto();
        assunto2.setId(assunto1.getId());
        assertThat(assunto1).isEqualTo(assunto2);
        assunto2.setId(2L);
        assertThat(assunto1).isNotEqualTo(assunto2);
        assunto1.setId(null);
        assertThat(assunto1).isNotEqualTo(assunto2);
    }
}
