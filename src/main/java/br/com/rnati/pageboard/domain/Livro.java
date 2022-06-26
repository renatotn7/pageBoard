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
 * A Livro.
 */
@Table("livro")
public class Livro implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nome_livro")
    private String nomeLivro;

    @Column("editora")
    private String editora;

    @Column("autor")
    private String autor;

    @Column("ano_de_publicacao")
    private Integer anoDePublicacao;

    @Column("tags")
    private String tags;

    @Transient
    @JsonIgnoreProperties(value = { "paragrafos", "livro" }, allowSetters = true)
    private Set<Pagina> paginas = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "usuario", "livros" }, allowSetters = true)
    private Set<Projeto> projetos = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "livros" }, allowSetters = true)
    private Assunto assunto;

    @Column("assunto_id")
    private Long assuntoId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Livro id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeLivro() {
        return this.nomeLivro;
    }

    public Livro nomeLivro(String nomeLivro) {
        this.setNomeLivro(nomeLivro);
        return this;
    }

    public void setNomeLivro(String nomeLivro) {
        this.nomeLivro = nomeLivro;
    }

    public String getEditora() {
        return this.editora;
    }

    public Livro editora(String editora) {
        this.setEditora(editora);
        return this;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public String getAutor() {
        return this.autor;
    }

    public Livro autor(String autor) {
        this.setAutor(autor);
        return this;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public Integer getAnoDePublicacao() {
        return this.anoDePublicacao;
    }

    public Livro anoDePublicacao(Integer anoDePublicacao) {
        this.setAnoDePublicacao(anoDePublicacao);
        return this;
    }

    public void setAnoDePublicacao(Integer anoDePublicacao) {
        this.anoDePublicacao = anoDePublicacao;
    }

    public String getTags() {
        return this.tags;
    }

    public Livro tags(String tags) {
        this.setTags(tags);
        return this;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Set<Pagina> getPaginas() {
        return this.paginas;
    }

    public void setPaginas(Set<Pagina> paginas) {
        if (this.paginas != null) {
            this.paginas.forEach(i -> i.setLivro(null));
        }
        if (paginas != null) {
            paginas.forEach(i -> i.setLivro(this));
        }
        this.paginas = paginas;
    }

    public Livro paginas(Set<Pagina> paginas) {
        this.setPaginas(paginas);
        return this;
    }

    public Livro addPagina(Pagina pagina) {
        this.paginas.add(pagina);
        pagina.setLivro(this);
        return this;
    }

    public Livro removePagina(Pagina pagina) {
        this.paginas.remove(pagina);
        pagina.setLivro(null);
        return this;
    }

    public Set<Projeto> getProjetos() {
        return this.projetos;
    }

    public void setProjetos(Set<Projeto> projetos) {
        this.projetos = projetos;
    }

    public Livro projetos(Set<Projeto> projetos) {
        this.setProjetos(projetos);
        return this;
    }

    public Livro addProjeto(Projeto projeto) {
        this.projetos.add(projeto);
        projeto.getLivros().add(this);
        return this;
    }

    public Livro removeProjeto(Projeto projeto) {
        this.projetos.remove(projeto);
        projeto.getLivros().remove(this);
        return this;
    }

    public Assunto getAssunto() {
        return this.assunto;
    }

    public void setAssunto(Assunto assunto) {
        this.assunto = assunto;
        this.assuntoId = assunto != null ? assunto.getId() : null;
    }

    public Livro assunto(Assunto assunto) {
        this.setAssunto(assunto);
        return this;
    }

    public Long getAssuntoId() {
        return this.assuntoId;
    }

    public void setAssuntoId(Long assunto) {
        this.assuntoId = assunto;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Livro)) {
            return false;
        }
        return id != null && id.equals(((Livro) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Livro{" +
            "id=" + getId() +
            ", nomeLivro='" + getNomeLivro() + "'" +
            ", editora='" + getEditora() + "'" +
            ", autor='" + getAutor() + "'" +
            ", anoDePublicacao=" + getAnoDePublicacao() +
            ", tags='" + getTags() + "'" +
            "}";
    }
}
