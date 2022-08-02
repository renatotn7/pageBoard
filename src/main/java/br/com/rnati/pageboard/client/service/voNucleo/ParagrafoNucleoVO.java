package br.com.rnati.pageboard.client.service.voNucleo;

import java.io.Serializable;
import java.util.ArrayList;

import java.util.List;








public class ParagrafoNucleoVO implements Serializable{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	


	public ParagrafoNucleoVO() {
			super();
			// TODO Auto-generated constructor stub
		}
	   private Long id;

		 
	    private Integer numero;

	
	    private String texto;
	    
	    private List< AnexoNucleoVO> anexos; 
	
	    private String resumo;

	   
	    private List<PerguntaNucleoVO> perguntas = new ArrayList<>();

	

	   
	    private Long paginaId;


		public Long getId() {
			return id;
		}


		public void setId(Long id) {
			this.id = id;
		}


		public Integer getNumero() {
			return numero;
		}


		public void setNumero(Integer numero) {
			this.numero = numero;
		}


		public String getTexto() {
			return texto;
		}


		public void setTexto(String texto) {
			this.texto = texto;
		}


		public String getResumo() {
			return resumo;
		}


		public void setResumo(String resumo) {
			this.resumo = resumo;
		}


		public List<PerguntaNucleoVO> getPerguntas() {
			return perguntas;
		}


		public void setPerguntas(List<PerguntaNucleoVO> perguntas) {
			this.perguntas = perguntas;
		}
		public List< AnexoNucleoVO> getAnexos() {
			return anexos;
		}


		public void setAnexos(List< AnexoNucleoVO> anexos) {
			this.anexos = anexos;
		}



		public ParagrafoNucleoVO(Long id, Integer numero, String texto, String resumo, List<PerguntaNucleoVO> perguntas,
				Long paginaId) {
			super();
			this.id = id;
			this.numero = numero;
			this.texto = texto;
			this.resumo = resumo;
			this.perguntas = perguntas;
			this.paginaId = paginaId;
		}


		public Long getPaginaId() {
			return paginaId;
		}


		public void setPaginaId(Long paginaId) {
			this.paginaId = paginaId;
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
