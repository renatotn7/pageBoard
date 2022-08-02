package br.com.rnati.pageboard.enumerator;

public enum TipoAnexo {
    TXTSIMPLIFICADO(1),
    TXTTOPICOS(2),
    EXPLICATEXTO(3),
    EXPLICATEXTOCTIT(4),
    PERGUNTARESPOSTADISC(5),
    PERGUNTARESPOSTAMULT(6);

    private Integer id;

    TipoAnexo(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return this.id;
    }
}
