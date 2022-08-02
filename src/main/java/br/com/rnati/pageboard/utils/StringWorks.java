package br.com.rnati.pageboard.utils;

public class StringWorks {

    public static String paragrafoBrutoParaLinha(String texto) {
        boolean lastwithtraco = false;
        String separador = "\\n";
        String[] linhas = texto.split(separador);
        StringBuilder sb = new StringBuilder();
        sb.append(linhas[0]);
        for (int i = 1; i < linhas.length; i++) {
            String strimmed = linhas[i].trim();
            String primeiraLetra = strimmed.charAt(0) + "";
            boolean traco = false;
            if (strimmed.charAt(strimmed.length() - 1) == '-') {
                strimmed = strimmed.substring(0, strimmed.length() - 1);
                traco = true;
            }
            if (strimmed.charAt(strimmed.length() - 1) == 'e') {
                strimmed = strimmed + " ";
                traco = true;
            }
            if (primeiraLetra.equals(primeiraLetra.toLowerCase())) {
                if (!lastwithtraco) {
                    sb.append(" " + strimmed);
                } else {
                    sb.append(strimmed);
                }
            } else {
                if (!lastwithtraco) {
                    sb.append("\r\n" + strimmed);
                } else {
                    sb.append(strimmed);
                }
            }
            lastwithtraco = traco;
        }
        //System.out.println(sb.toString());

        return sb.toString();
    }

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //formato 1 texto seguido
        String texto =
            "Unidade 2 | Introdução\r\n" +
            "Dicotomias como duas partes diferentes que\r\n" +
            "se complementam\r\n" +
            "Nesta unidade, vamos nos aprofundar acerca das\r\n" +
            "dicotomias saussurianas, principalmente a do\r\n" +
            "significante x significado, da língua x fala e da\r\n" +
            "sincronia x diacronia.\r\n";

        String separador = "\\r\\n";
        String[] linhas = texto.split(separador);
        StringBuilder sb = new StringBuilder();
        sb.append(linhas[0]);
        for (int i = 1; i < linhas.length; i++) {
            String strimmed = linhas[i].trim();
            String primeiraLetra = strimmed.charAt(0) + "";
            if (primeiraLetra.equals(primeiraLetra.toLowerCase())) {
                sb.append(" " + strimmed);
            } else {
                sb.append("\r\n" + strimmed);
            }
        }
        System.out.println(sb.toString());
    }
}
