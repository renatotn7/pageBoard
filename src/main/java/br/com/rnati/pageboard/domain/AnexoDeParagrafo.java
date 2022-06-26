package br.com.rnati.pageboard.domain;

import br.com.rnati.pageboard.domain.enumeration.TipoAnexoDeParagrafo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A AnexoDeParagrafo.
 */
@Table("anexo_de_paragrafo")
public class AnexoDeParagrafo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("tipo")
    private TipoAnexoDeParagrafo tipo;

    @Column("value")
    private String value;

    @Transient
    @JsonIgnoreProperties(value = { "perguntas", "pagina" }, allowSetters = true)
    private Paragrafo paragrafo;

    @Column("paragrafo_id")
    private Long paragrafoId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public AnexoDeParagrafo id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoAnexoDeParagrafo getTipo() {
        return this.tipo;
    }

    public AnexoDeParagrafo tipo(TipoAnexoDeParagrafo tipo) {
        this.setTipo(tipo);
        return this;
    }

    public void setTipo(TipoAnexoDeParagrafo tipo) {
        this.tipo = tipo;
    }

    public String getValue() {
        return this.value;
    }

    public AnexoDeParagrafo value(String value) {
        this.setValue(value);
        return this;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Paragrafo getParagrafo() {
        return this.paragrafo;
    }

    public void setParagrafo(Paragrafo paragrafo) {
        this.paragrafo = paragrafo;
        this.paragrafoId = paragrafo != null ? paragrafo.getId() : null;
    }

    public AnexoDeParagrafo paragrafo(Paragrafo paragrafo) {
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
        if (!(o instanceof AnexoDeParagrafo)) {
            return false;
        }
        return id != null && id.equals(((AnexoDeParagrafo) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "AnexoDeParagrafo{" +
            "id=" + getId() +
            ", tipo='" + getTipo() + "'" +
            ", value='" + getValue() + "'" +
            "}";
    }
}
