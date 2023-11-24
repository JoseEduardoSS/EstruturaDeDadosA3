package br.com.estruturaDeDados;

import java.util.List;

public class Utils {

    public static String capitalizeWords(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        var words = str.split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = capitalize(words[i]);
        }
        return String.join(" ", words);
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    public static String formatarTempo(List<Double> tempos) {
        StringBuilder resultado = new StringBuilder();

        for (double tempoEmDecimal : tempos) {
            int horas = (int) tempoEmDecimal;
            int minutos = (int) ((tempoEmDecimal - horas) * 60);

            if (horas > 0) {
                resultado.append(horas).append(" hora");
                if (horas > 1) {
                    resultado.append("s");
                }
                resultado.append(" ");
            }

            if (minutos > 0 || (horas == 0 && minutos == 0)) {
                resultado.append(minutos).append(" minuto");
                if (minutos != 1) {
                    resultado.append("s");
                }
            }

            resultado.append("\n");
        }

        return resultado.toString();
    }
}
