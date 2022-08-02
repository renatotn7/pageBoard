package br.com.rnati.pageboard.client.service.voNucleo;







public class PerguntaNucleoVO {
	
	    private Long id;

	  
	    private String questao;

	   
	    private String resposta;

	   
	    private Long paragrafoId;
public PerguntaNucleoVO() {
	
}
public PerguntaNucleoVO(Long id, String questao, String resposta, Long paragrafoId) {
	super();
	this.id = id;
	this.questao = questao;
	this.resposta = resposta;
	this.paragrafoId = paragrafoId;
}
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public String getQuestao() {
	return questao;
}
public void setQuestao(String questao) {
	this.questao = questao;
}
public String getResposta() {
	return resposta;
}
public void setResposta(String resposta) {
	this.resposta = resposta;
}
public Long getParagrafoId() {
	return paragrafoId;
}
public void setParagrafoId(Long paragrafoId) {
	this.paragrafoId = paragrafoId;
}
@Override
public String toString() {
    return "Pergunta{" +
        "id=" + getId() +
        ", questao='" + getQuestao() + "'" +
        ", resposta='" + getResposta() + "'" +
        "}";
}
}
