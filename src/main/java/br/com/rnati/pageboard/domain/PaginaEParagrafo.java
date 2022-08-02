package br.com.rnati.pageboard.domain;

import java.io.Serializable;

public class PaginaEParagrafo implements Serializable {/**
 *
 */

    private static final long serialVersionUID = 1L;

    private Pagina pagina;
    private Paragrafo paragrafo;

    public Pagina getPagina() {
        return pagina;
    }

    public void setPagina(Pagina pagina) {
        this.pagina = pagina;
    }

    public Paragrafo getParagrafo() {
        return paragrafo;
    }

    public void setParagrafo(Paragrafo paragrafo) {
        this.paragrafo = paragrafo;
    }
}
