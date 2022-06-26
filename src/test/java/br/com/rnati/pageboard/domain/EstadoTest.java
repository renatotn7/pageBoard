package br.com.rnati.pageboard.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.rnati.pageboard.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EstadoTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Estado.class);
        Estado estado1 = new Estado();
        estado1.setId(1L);
        Estado estado2 = new Estado();
        estado2.setId(estado1.getId());
        assertThat(estado1).isEqualTo(estado2);
        estado2.setId(2L);
        assertThat(estado1).isNotEqualTo(estado2);
        estado1.setId(null);
        assertThat(estado1).isNotEqualTo(estado2);
    }
}
