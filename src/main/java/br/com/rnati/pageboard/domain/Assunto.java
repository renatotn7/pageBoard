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
 * A Assunto.
 */
@Table("assunto")
public class Assunto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nome")
    private String nome;

    @Transient
    @JsonIgnoreProperties(value = { "paginas", "projetos", "assunto" }, allowSetters = true)
    private Set<Livro> livros = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Assunto id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public Assunto nome(String nome) {
        this.setNome(nome);
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Set<Livro> getLivros() {
        return this.livros;
    }

    public void setLivros(Set<Livro> livros) {
        if (this.livros != null) {
            this.livros.forEach(i -> i.setAssunto(null));
        }
        if (livros != null) {
            livros.forEach(i -> i.setAssunto(this));
        }
        this.livros = livros;
    }

    public Assunto livros(Set<Livro> livros) {
        this.setLivros(livros);
        return this;
    }

    public Assunto addLivro(Livro livro) {
        this.livros.add(livro);
        livro.setAssunto(this);
        return this;
    }

    public Assunto removeLivro(Livro livro) {
        this.livros.remove(livro);
        livro.setAssunto(null);
        return this;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Assunto)) {
            return false;
        }
        return id != null && id.equals(((Assunto) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Assunto{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            "}";
    }
}
