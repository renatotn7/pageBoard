package br.com.rnati.pageboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Cidade.
 */
@Table("cidade")
public class Cidade implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nome")
    private String nome;

    @Transient
    private Estado estado;

    @Column("estado_id")
    private Long estadoId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Cidade id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public Cidade nome(String nome) {
        this.setNome(nome);
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Estado getEstado() {
        return this.estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
        this.estadoId = estado != null ? estado.getId() : null;
    }

    public Cidade estado(Estado estado) {
        this.setEstado(estado);
        return this;
    }

    public Long getEstadoId() {
        return this.estadoId;
    }

    public void setEstadoId(Long estado) {
        this.estadoId = estado;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Cidade)) {
            return false;
        }
        return id != null && id.equals(((Cidade) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Cidade{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            "}";
    }
}
