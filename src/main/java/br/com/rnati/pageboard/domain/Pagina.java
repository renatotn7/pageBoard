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
 * A Pagina.
 */
@Table("pagina")
public class Pagina implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("numero")
    private Integer numero;

    @Column("texto")
    private String texto;

    @Column("plano_de_aula")
    private String planoDeAula;

    @Column("imagem")
    private byte[] imagem;

    @Column("imagem_content_type")
    private String imagemContentType;

    @Transient
    @JsonIgnoreProperties(value = { "perguntas", "pagina" }, allowSetters = true)
    private Set<Paragrafo> paragrafos = new HashSet<>();

    @Transient
    @JsonIgnoreProperties(value = { "paginas", "projetos", "assunto" }, allowSetters = true)
    private Livro livro;

    @Column("livro_id")
    private Long livroId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Pagina id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getNumero() {
        return this.numero;
    }

    public Pagina numero(Integer numero) {
        this.setNumero(numero);
        return this;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getTexto() {
        return this.texto;
    }

    public Pagina texto(String texto) {
        this.setTexto(texto);
        return this;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public String getPlanoDeAula() {
        return this.planoDeAula;
    }

    public Pagina planoDeAula(String planoDeAula) {
        this.setPlanoDeAula(planoDeAula);
        return this;
    }

    public void setPlanoDeAula(String planoDeAula) {
        this.planoDeAula = planoDeAula;
    }

    public byte[] getImagem() {
        return this.imagem;
    }

    public Pagina imagem(byte[] imagem) {
        this.setImagem(imagem);
        return this;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public String getImagemContentType() {
        return this.imagemContentType;
    }

    public Pagina imagemContentType(String imagemContentType) {
        this.imagemContentType = imagemContentType;
        return this;
    }

    public void setImagemContentType(String imagemContentType) {
        this.imagemContentType = imagemContentType;
    }

    public Set<Paragrafo> getParagrafos() {
        return this.paragrafos;
    }

    public void setParagrafos(Set<Paragrafo> paragrafos) {
        if (this.paragrafos != null) {
            this.paragrafos.forEach(i -> i.setPagina(null));
        }
        if (paragrafos != null) {
            paragrafos.forEach(i -> i.setPagina(this));
        }
        this.paragrafos = paragrafos;
    }

    public Pagina paragrafos(Set<Paragrafo> paragrafos) {
        this.setParagrafos(paragrafos);
        return this;
    }

    public Pagina addParagrafo(Paragrafo paragrafo) {
        this.paragrafos.add(paragrafo);
        paragrafo.setPagina(this);
        return this;
    }

    public Pagina removeParagrafo(Paragrafo paragrafo) {
        this.paragrafos.remove(paragrafo);
        paragrafo.setPagina(null);
        return this;
    }

    public Livro getLivro() {
        return this.livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
        this.livroId = livro != null ? livro.getId() : null;
    }

    public Pagina livro(Livro livro) {
        this.setLivro(livro);
        return this;
    }

    public Long getLivroId() {
        return this.livroId;
    }

    public void setLivroId(Long livro) {
        this.livroId = livro;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Pagina)) {
            return false;
        }
        return id != null && id.equals(((Pagina) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Pagina{" +
            "id=" + getId() +
            ", numero=" + getNumero() +
            ", texto='" + getTexto() + "'" +
            ", planoDeAula='" + getPlanoDeAula() + "'" +
            ", imagem='" + getImagem() + "'" +
            ", imagemContentType='" + getImagemContentType() + "'" +
            "}";
    }
}
