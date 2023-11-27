package br.com.estruturaDeDados;

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

    public static String convertTime(int minutos) {
        if (minutos < 0) {
            throw new IllegalArgumentException("O número de minutos não pode ser negativo");
        }

        int horas = minutos / 60;
        int minutosRestantes = minutos % 60;

        if (horas == 0) {
            return minutos + " minutos";
        } else if (minutosRestantes == 0) {
            return horas + " hora" + (horas > 1 ? "s" : "");
        } else {
            return horas + " hora" + (horas > 1 ? "s" : "") + " e " + minutosRestantes + " minuto" + (minutosRestantes > 1 ? "s" : "");
        }
    }
}
