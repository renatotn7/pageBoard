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
 * A Projeto.
 */
@Table("projeto")
public class Projeto implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("nome")
    private String nome;

    @Column("descricao")
    private String descricao;

    @Column("imagem")
    private byte[] imagem;

    @Column("imagem_content_type")
    private String imagemContentType;

    @Column("tags")
    private String tags;

    @Transient
    @JsonIgnoreProperties(value = { "pessoa", "projetos" }, allowSetters = true)
    private Usuario usuario;

    @Transient
    @JsonIgnoreProperties(value = { "paginas", "projetos", "assunto" }, allowSetters = true)
    private Set<Livro> livros = new HashSet<>();

    @Column("usuario_id")
    private Long usuarioId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Projeto id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return this.nome;
    }

    public Projeto nome(String nome) {
        this.setNome(nome);
        return this;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public Projeto descricao(String descricao) {
        this.setDescricao(descricao);
        return this;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public byte[] getImagem() {
        return this.imagem;
    }

    public Projeto imagem(byte[] imagem) {
        this.setImagem(imagem);
        return this;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public String getImagemContentType() {
        return this.imagemContentType;
    }

    public Projeto imagemContentType(String imagemContentType) {
        this.imagemContentType = imagemContentType;
        return this;
    }

    public void setImagemContentType(String imagemContentType) {
        this.imagemContentType = imagemContentType;
    }

    public String getTags() {
        return this.tags;
    }

    public Projeto tags(String tags) {
        this.setTags(tags);
        return this;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public Usuario getUsuario() {
        return this.usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
        this.usuarioId = usuario != null ? usuario.getId() : null;
    }

    public Projeto usuario(Usuario usuario) {
        this.setUsuario(usuario);
        return this;
    }

    public Set<Livro> getLivros() {
        return this.livros;
    }

    public void setLivros(Set<Livro> livros) {
        if (this.livros != null) {
            this.livros.forEach(i -> i.removeProjeto(this));
        }
        if (livros != null) {
            livros.forEach(i -> i.addProjeto(this));
        }
        this.livros = livros;
    }

    public Projeto livros(Set<Livro> livros) {
        this.setLivros(livros);
        return this;
    }

    public Projeto addLivro(Livro livro) {
        this.livros.add(livro);
        livro.getProjetos().add(this);
        return this;
    }

    public Projeto removeLivro(Livro livro) {
        this.livros.remove(livro);
        livro.getProjetos().remove(this);
        return this;
    }

    public Long getUsuarioId() {
        return this.usuarioId;
    }

    public void setUsuarioId(Long usuario) {
        this.usuarioId = usuario;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Projeto)) {
            return false;
        }
        return id != null && id.equals(((Projeto) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Projeto{" +
            "id=" + getId() +
            ", nome='" + getNome() + "'" +
            ", descricao='" + getDescricao() + "'" +
            ", imagem='" + getImagem() + "'" +
            ", imagemContentType='" + getImagemContentType() + "'" +
            ", tags='" + getTags() + "'" +
            "}";
    }
}
