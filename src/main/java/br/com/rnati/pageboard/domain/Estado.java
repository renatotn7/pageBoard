package br.com.rnati.pageboard.domain;

import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Estado.
 */
@Table("estado")
public class Estado implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nome")
    private String nome;

    @Column("uf")
    private String uf;

    @Transient
    private Pais pais;

    @Column("pais_id")
    private Long paisId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Estado id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public Estado nome(String nome) {
        this.setNome(nome);
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getUf() {
        return this.uf;
    }

    public Estado uf(String uf) {
        this.setUf(uf);
        return this;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public Pais getPais() {
        return this.pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
        this.paisId = pais != null ? pais.getId() : null;
    }

    public Estado pais(Pais pais) {
        this.setPais(pais);
        return this;
    }

    public Long getPaisId() {
        return this.paisId;
    }

    public void setPaisId(Long pais) {
        this.paisId = pais;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Estado)) {
            return false;
        }
        return id != null && id.equals(((Estado) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Estado{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", uf='" + getUf() + "'" +
            "}";
    }
}
