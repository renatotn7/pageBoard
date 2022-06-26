package br.com.rnati.pageboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Paragrafo.
 */
@Table("paragrafo")
public class Paragrafo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("numero")
    private Integer numero;

    @Column("texto")
    private String texto;

    @Column("resumo")
    private String resumo;

    @Transient
    @JsonIgnoreProperties(value = { "paragrafo" }, allowSetters = true)
    private Set<Pergunta> perguntas = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "paragrafos", "livro" }, allowSetters = true)
    private Pagina pagina;

    @Column("pagina_id")
    private Long paginaId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Paragrafo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return this.numero;
    }

    public Paragrafo numero(Integer numero) {
        this.setNumero(numero);
        return this;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getTexto() {
        return this.texto;
    }

    public Paragrafo texto(String texto) {
        this.setTexto(texto);
        return this;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getResumo() {
        return this.resumo;
    }

    public Paragrafo resumo(String resumo) {
        this.setResumo(resumo);
        return this;
    }

    public void setResumo(String resumo) {
        this.resumo = resumo;
    }

    public Set<Pergunta> getPerguntas() {
        return this.perguntas;
    }

    public void setPerguntas(Set<Pergunta> perguntas) {
        if (this.perguntas != null) {
            this.perguntas.forEach(i -> i.setParagrafo(null));
        }
        if (perguntas != null) {
            perguntas.forEach(i -> i.setParagrafo(this));
        }
        this.perguntas = perguntas;
    }

    public Paragrafo perguntas(Set<Pergunta> perguntas) {
        this.setPerguntas(perguntas);
        return this;
    }

    public Paragrafo addPergunta(Pergunta pergunta) {
        this.perguntas.add(pergunta);
        pergunta.setParagrafo(this);
        return this;
    }

    public Paragrafo removePergunta(Pergunta pergunta) {
        this.perguntas.remove(pergunta);
        pergunta.setParagrafo(null);
        return this;
    }

    public Pagina getPagina() {
        return this.pagina;
    }

    public void setPagina(Pagina pagina) {
        this.pagina = pagina;
        this.paginaId = pagina != null ? pagina.getId() : null;
    }

    public Paragrafo pagina(Pagina pagina) {
        this.setPagina(pagina);
        return this;
    }

    public Long getPaginaId() {
        return this.paginaId;
    }

    public void setPaginaId(Long pagina) {
        this.paginaId = pagina;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Paragrafo)) {
            return false;
        }
        return id != null && id.equals(((Paragrafo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Paragrafo{" +
            "id=" + getId() +
            ", numero=" + getNumero() +
            ", texto='" + getTexto() + "'" +
            ", resumo='" + getResumo() + "'" +
            "}";
    }
}
