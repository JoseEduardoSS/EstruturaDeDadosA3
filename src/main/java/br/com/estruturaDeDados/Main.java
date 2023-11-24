package br.com.estruturaDeDados;

import java.text.DecimalFormat;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // Definindo scanner para input de dados de percuso
        var scan = new Scanner(System.in);

        // Instanciado grafo
        var grafo = new GrafoGeografico();

        //  Definição de formatação de numérica
        var numberFormat = new DecimalFormat("#.00");

        //  Iniciando grafo
        grafo.ConstruirGrafo();

        // Input de dados do percurso:
        System.out.println("Digite o início e o destino do percurso: ");
        System.out.print("Início: ");
        var inicio = scan.nextLine();
        System.out.print("Destino: ");
        var destino = scan.nextLine();

        // Fazendo requisição
        var resultado = grafo.menorDistancia(inicio, destino);

        // Obtenção dos resultados
        var menorDistancia = resultado.getDistancia();
        var percurso = resultado.getPercurso();

        // View de resultados
        if (menorDistancia != null) {
            System.out.println("############################################");
            if (menorDistancia >= 0) {
                System.out.println("A menor distância entre " + Utils.capitalizeWords(inicio) + " e " + Utils.capitalizeWords(destino) + " é: " + numberFormat.format(menorDistancia) + "km");
                System.out.println("Percurso: " + String.join(" -> ", percurso));
            } else {
                System.out.println("Não há caminho entre " + inicio + " e " + destino);
            }
            System.out.println("############################################");
        }
    }
}