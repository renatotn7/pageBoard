package br.com.rnati.pageboard.client.service.voNucleo;


public class RespostaNucleoVO {
  
    private Long id;


    private String texto;


	public RespostaNucleoVO(Long id, String texto) {
		super();
		this.id = id;
		this.texto = texto;
	}
	public RespostaNucleoVO() {
		
	}

	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getTexto() {
		return texto;
	}


	public void setTexto(String texto) {
		this.texto = texto;
	}
}
