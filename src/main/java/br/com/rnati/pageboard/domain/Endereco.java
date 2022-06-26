package br.com.rnati.pageboard.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Endereco.
 */
@Table("endereco")
public class Endereco implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @Column("logradouro")
    private String logradouro;

    @Column("numero")
    private Integer numero;

    @Column("complemento")
    private String complemento;

    @Column("bairro")
    private String bairro;

    @Column("c_ep")
    private String cEP;

    @Transient
    private Cidade cidade;

    @Transient
    private Estado estado;

    @Transient
    private Pais pais;

    @Transient
    @JsonIgnoreProperties(value = { "enderecos" }, allowSetters = true)
    private Pessoa pessoa;

    @Column("cidade_id")
    private Long cidadeId;

    @Column("estado_id")
    private Long estadoId;

    @Column("pais_id")
    private Long paisId;

    @Column("pessoa_id")
    private Long pessoaId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Endereco id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogradouro() {
        return this.logradouro;
    }

    public Endereco logradouro(String logradouro) {
        this.setLogradouro(logradouro);
        return this;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public Integer getNumero() {
        return this.numero;
    }

    public Endereco numero(Integer numero) {
        this.setNumero(numero);
        return this;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return this.complemento;
    }

    public Endereco complemento(String complemento) {
        this.setComplemento(complemento);
        return this;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return this.bairro;
    }

    public Endereco bairro(String bairro) {
        this.setBairro(bairro);
        return this;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getcEP() {
        return this.cEP;
    }

    public Endereco cEP(String cEP) {
        this.setcEP(cEP);
        return this;
    }

    public void setcEP(String cEP) {
        this.cEP = cEP;
    }

    public Cidade getCidade() {
        return this.cidade;
    }

    public void setCidade(Cidade cidade) {
        this.cidade = cidade;
        this.cidadeId = cidade != null ? cidade.getId() : null;
    }

    public Endereco cidade(Cidade cidade) {
        this.setCidade(cidade);
        return this;
    }

    public Estado getEstado() {
        return this.estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
        this.estadoId = estado != null ? estado.getId() : null;
    }

    public Endereco estado(Estado estado) {
        this.setEstado(estado);
        return this;
    }

    public Pais getPais() {
        return this.pais;
    }

    public void setPais(Pais pais) {
        this.pais = pais;
        this.paisId = pais != null ? pais.getId() : null;
    }

    public Endereco pais(Pais pais) {
        this.setPais(pais);
        return this;
    }

    public Pessoa getPessoa() {
        return this.pessoa;
    }

    public void setPessoa(Pessoa pessoa) {
        this.pessoa = pessoa;
        this.pessoaId = pessoa != null ? pessoa.getId() : null;
    }

    public Endereco pessoa(Pessoa pessoa) {
        this.setPessoa(pessoa);
        return this;
    }

    public Long getCidadeId() {
        return this.cidadeId;
    }

    public void setCidadeId(Long cidade) {
        this.cidadeId = cidade;
    }

    public Long getEstadoId() {
        return this.estadoId;
    }

    public void setEstadoId(Long estado) {
        this.estadoId = estado;
    }

    public Long getPaisId() {
        return this.paisId;
    }

    public void setPaisId(Long pais) {
        this.paisId = pais;
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
        if (!(o instanceof Endereco)) {
            return false;
        }
        return id != null && id.equals(((Endereco) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Endereco{" +
            "id=" + getId() +
            ", logradouro='" + getLogradouro() + "'" +
            ", numero=" + getNumero() +
            ", complemento='" + getComplemento() + "'" +
            ", bairro='" + getBairro() + "'" +
            ", cEP='" + getcEP() + "'" +
            "}";
    }
}
