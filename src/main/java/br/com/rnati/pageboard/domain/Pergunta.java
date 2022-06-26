package br.com.rnati.pageboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Pergunta.
 */
@Table("pergunta")
public class Pergunta implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("questao")
    private String questao;

    @Column("resposta")
    private String resposta;

    @Transient
    @JsonIgnoreProperties(value = { "perguntas", "pagina" }, allowSetters = true)
    private Paragrafo paragrafo;

    @Column("paragrafo_id")
    private Long paragrafoId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pergunta id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestao() {
        return this.questao;
    }

    public Pergunta questao(String questao) {
        this.setQuestao(questao);
        return this;
    }

    public void setQuestao(String questao) {
        this.questao = questao;
    }

    public String getResposta() {
        return this.resposta;
    }

    public Pergunta resposta(String resposta) {
        this.setResposta(resposta);
        return this;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public Paragrafo getParagrafo() {
        return this.paragrafo;
    }

    public void setParagrafo(Paragrafo paragrafo) {
        this.paragrafo = paragrafo;
        this.paragrafoId = paragrafo != null ? paragrafo.getId() : null;
    }

    public Pergunta paragrafo(Paragrafo paragrafo) {
        this.setParagrafo(paragrafo);
        return this;
    }

    public Long getParagrafoId() {
        return this.paragrafoId;
    }

    public void setParagrafoId(Long paragrafo) {
        this.paragrafoId = paragrafo;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pergunta)) {
            return false;
        }
        return id != null && id.equals(((Pergunta) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pergunta{" +
            "id=" + getId() +
            ", questao='" + getQuestao() + "'" +
            ", resposta='" + getResposta() + "'" +
            "}";
    }
}
