package utils;

/**
 * Classe utilitária para manutenção de String's.
 * @author Leandro Aparecido de Almeida
 */
public final class StrParser {

    /**
     * Constructor private. Não permite instanciar objetos da classe.
     */
    private StrParser() {
    }

    /**
     * Converte uma string em java.util.Date.
     * @param text string a ser convertida.
     * @return data correspondente à string.
     */
    public static java.util.Date asDate(String text) { 
        String dia = text.substring(0, 2); 
        String mes = text.substring(3, 5);
        String ano = text.substring(6, 10);
        String dataFormatoJDBC = ano + "-" + mes + "-" + dia;
        return java.sql.Date.valueOf(dataFormatoJDBC);
    }
    
    /**
     * Converte uma string em float.
     * @param text string a ser convertida.
     * @return float correspondente à string.
     */
    public static float asFloat(String text) {
        return Float.valueOf(text.trim().replace(".", "").replace(",", "."));
    }
    
    /**
     * Converte uma string em integer.
     * @param text string a ser convertida.
     * @return integer correspondente à string.
     */
    public static int asInt(String text) {
        return Integer.valueOf(text.trim().replace(".", ""));
    }
    
    /**
     * Converte um float em string.
     * @param value valor float a ser convertido.
     * @return string correspondente ao float, com duas casas decimais de precisão.
     */
    public static String asString(float value) {
        return String.format("%,.2f", value);        
    }
    
    /**
     * Converte um integer em string.
     * @param value valor integer a ser convertido.
     * @return string correspondente ao integer.
     */
    public static String asString(int value) {
        return String.format("%,d", value);        
    }
    
    /**
     * Converte uma data em string, no formato dd/mm/aaaa.
     * @param value data a ser convertida.
     * @return string correspondente à data.
     */
    public static String asString(java.util.Date value) {
        return String.format("%1$td/%1$tm/%1$tY", value);        
    }
    
}