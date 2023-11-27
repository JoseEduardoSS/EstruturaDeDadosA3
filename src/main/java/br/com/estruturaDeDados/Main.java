package br.com.estruturaDeDados;

import java.text.DecimalFormat;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        // Definindo scanner para input de dados de percurso
        var scan = new Scanner(System.in);

        // Instanciando grafo
        var grafo = new GrafoGeografico();

        // Definição de formatação numérica
        var numberFormat = new DecimalFormat("#.00");

        // Iniciando grafo
        grafo.ConstruirGrafo();

        // Input de dados do percurso
        System.out.println("Insira informações sobre a viagem que será feita: ");
        System.out.print("Início: ");
        var inicio = scan.nextLine();
        System.out.print("Destino: ");
        var destino = scan.nextLine();

        System.out.print("""
                Meio de transporte:
                1 - Carro
                2 - Moto
                3 - Ônibus
                4 - Caminhão
                Selecione:""");
        var transporte = scan.nextInt();

        String meioTransporte;
        switch (transporte) {
            case 1 -> meioTransporte = "carro";
            case 2 -> meioTransporte = "moto";
            case 3 -> meioTransporte = "onibus";
            case 4 -> meioTransporte = "caminhao";
            default -> meioTransporte = null;
        }

        // Fazendo requisição
        var resultado = grafo.menorDistancia(inicio, destino, meioTransporte);

        // Obtenção dos resultados
        var menorDistancia = resultado.menorDistancia();
        var percurso = resultado.percurso();
        var tempo = resultado.tempoTotal();
        var custo = resultado.custoTotal();

        // Exibição dos resultados
        System.out.println("====================================================================================");
        if (menorDistancia >= 0) {
            System.out.println("A menor distância entre " + Utils.capitalizeWords(inicio) + " e " + Utils.capitalizeWords(destino) + " é: " + numberFormat.format(menorDistancia) + " km");
            System.out.println("Percurso: " + String.join(" -> ", percurso));
            System.out.println("Tempo total da viagem: " + Utils.convertTime(tempo));
            System.out.println("Custo total da viagem: R$ " + numberFormat.format(custo));
        } else {
            System.out.println("Não há caminho entre " + inicio + " e " + destino);
        }
        System.out.println("====================================================================================");

        // Fechando o scanner
        scan.close();
    }
}
