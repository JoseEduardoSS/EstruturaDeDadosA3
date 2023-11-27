package br.com.estruturaDeDados;

import javax.swing.*;
import java.text.DecimalFormat;

public class Main {
    public static void main(String[] args) {
        // Instanciando grafo
        var grafo = new GrafoGeografico();

        // Definição de formatação numérica
        var numberFormat = new DecimalFormat("#.00");

        // Iniciando grafo
        grafo.ConstruirGrafo();

        // Input de dados do percurso
        var inicio = JOptionPane.showInputDialog("Início:");
        var destino = JOptionPane.showInputDialog("Destino:");

        String[] opcoesTransporte = {"Carro", "Moto", "Ônibus", "Caminhão"};
        int escolhaTransporte = JOptionPane.showOptionDialog(null, "Selecione o meio de transporte:",
                "Escolha o transporte", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opcoesTransporte, opcoesTransporte[0]);

        String meioTransporte;
        switch (escolhaTransporte) {
            case 0 -> meioTransporte = "carro";
            case 1 -> meioTransporte = "moto";
            case 2 -> meioTransporte = "onibus";
            case 3 -> meioTransporte = "caminhao";
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
        if (menorDistancia >= 0) {
            JOptionPane.showMessageDialog(null,
                    "A menor distância entre " + Utils.capitalizeWords(inicio) + " e " + Utils.capitalizeWords(destino) +
                            " é: " + numberFormat.format(menorDistancia) + " km\n" +
                            "Percurso: " + String.join(" → ", percurso) + "\n" +
                            "Tempo total da viagem: " + Utils.convertTime(tempo) + "\n" +
                            "Custo total da viagem: R$ " + numberFormat.format(custo),
                    "Resultados",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null,
                    "Não há caminho entre " + inicio + " e " + destino,
                    "Resultados",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
}
