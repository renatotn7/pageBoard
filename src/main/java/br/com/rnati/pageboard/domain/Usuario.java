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
 * A Usuario.
 */
@Table("usuario")
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("login")
    private String login;

    @Column("email")
    private String email;

    @Column("foto")
    private byte[] foto;

    @Column("foto_content_type")
    private String fotoContentType;

    @Transient
    private Pessoa pessoa;

    @Transient
    @JsonIgnoreProperties(value = { "usuario", "livros" }, allowSetters = true)
    private Set<Projeto> projetos = new HashSet<>();

    @Column("pessoa_id")
    private Long pessoaId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Usuario id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return this.login;
    }

    public Usuario login(String login) {
        this.setLogin(login);
        return this;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getEmail() {
        return this.email;
    }

    public Usuario email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public byte[] getFoto() {
        return this.foto;
    }

    public Usuario foto(byte[] foto) {
        this.setFoto(foto);
        return this;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public String getFotoContentType() {
        return this.fotoContentType;
    }

    public Usuario fotoContentType(String fotoContentType) {
        this.fotoContentType = fotoContentType;
        return this;
    }

    public void setFotoContentType(String fotoContentType) {
        this.fotoContentType = fotoContentType;
    }

    public Pessoa getPessoa() {
        return this.pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
        this.pessoaId = pessoa != null ? pessoa.getId() : null;
    }

    public Usuario pessoa(Pessoa pessoa) {
        this.setPessoa(pessoa);
        return this;
    }

    public Set<Projeto> getProjetos() {
        return this.projetos;
    }

    public void setProjetos(Set<Projeto> projetos) {
        if (this.projetos != null) {
            this.projetos.forEach(i -> i.setUsuario(null));
        }
        if (projetos != null) {
            projetos.forEach(i -> i.setUsuario(this));
        }
        this.projetos = projetos;
    }

    public Usuario projetos(Set<Projeto> projetos) {
        this.setProjetos(projetos);
        return this;
    }

    public Usuario addProjeto(Projeto projeto) {
        this.projetos.add(projeto);
        projeto.setUsuario(this);
        return this;
    }

    public Usuario removeProjeto(Projeto projeto) {
        this.projetos.remove(projeto);
        projeto.setUsuario(null);
        return this;
    }

    public Long getPessoaId() {
        return this.pessoaId;
    }

    public void setPessoaId(Long pessoa) {
        this.pessoaId = pessoa;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Usuario)) {
            return false;
        }
        return id != null && id.equals(((Usuario) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Usuario{" +
            "id=" + getId() +
            ", login='" + getLogin() + "'" +
            ", email='" + getEmail() + "'" +
            ", foto='" + getFoto() + "'" +
            ", fotoContentType='" + getFotoContentType() + "'" +
            "}";
    }
}
