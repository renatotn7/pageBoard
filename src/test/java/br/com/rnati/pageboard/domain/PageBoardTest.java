package br.com.rnati.pageboard.domain;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.rnati.pageboard.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class PageBoardTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PageBoard.class);
        PageBoard pageBoard1 = new PageBoard();
        pageBoard1.setId(1L);
        PageBoard pageBoard2 = new PageBoard();
        pageBoard2.setId(pageBoard1.getId());
        assertThat(pageBoard1).isEqualTo(pageBoard2);
        pageBoard2.setId(2L);
        assertThat(pageBoard1).isNotEqualTo(pageBoard2);
        pageBoard1.setId(null);
        assertThat(pageBoard1).isNotEqualTo(pageBoard2);
    }
}
