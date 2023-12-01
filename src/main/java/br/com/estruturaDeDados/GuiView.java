package br.com.estruturaDeDados;

import org.apache.commons.csv.CSVRecord;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class GuiView {

    public static void exibirJanelaPrincipal(List<CSVRecord> csvRecords, GrafoGeografico grafo) {
        JFrame frame = new JFrame("Calculadora de Rotas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLayout(new GridLayout(4, 4));

        JComboBox<String> inicioComboBox = new JComboBox<>(extrairVertices(csvRecords));
        JComboBox<String> destinoComboBox = new JComboBox<>(extrairVertices(csvRecords));

        String[] opcoesTransporte = {"Carro", "Moto", "Ônibus", "Caminhão"};
        JComboBox<String> transporteComboBox = new JComboBox<>(opcoesTransporte);

        JButton calcularButton = new JButton("Calcular");
        calcularButton.addActionListener(e -> {
            String inicio = (String) Objects.requireNonNull(inicioComboBox.getSelectedItem());
            String destino = (String) Objects.requireNonNull(destinoComboBox.getSelectedItem());
            String meioTransporte = (String) Objects.requireNonNull(transporteComboBox.getSelectedItem());

            switch (meioTransporte) {
                case "Carro" -> meioTransporte = "carro";
                case "Moto" -> meioTransporte = "moto";
                case "Ônibus" -> meioTransporte = "onibus";
                case "Caminhão" -> meioTransporte = "caminhao";
            }

            exibirResultados(grafo.menorDistancia(inicio, destino, meioTransporte), inicio, destino);
        });

        frame.add(new JLabel("Início:"));
        frame.add(inicioComboBox);
        frame.add(new JLabel("Destino:"));
        frame.add(destinoComboBox);
        frame.add(new JLabel("Meio de Transporte:"));
        frame.add(transporteComboBox);
        frame.add(new JLabel());
        frame.add(calcularButton);

        frame.setLocationRelativeTo(null);

        frame.setVisible(true);
    }

    private static String[] extrairVertices(List<CSVRecord> csvRecords) {
        return csvRecords.stream()
                .flatMap(record -> Stream.of(record.get(0), record.get(1)))
                .distinct()
                .toArray(String[]::new);
    }

    private static void exibirResultados(RetornoCalculo resultado, String inicio, String destino) {
        JFrame frame = new JFrame("Resultados");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        var tamanhoPercurso = 0;
        for (var ponto : resultado.percurso()) {
            tamanhoPercurso += ponto.length();
        }

        var larguraJanela = tamanhoPercurso > 3 ? tamanhoPercurso * 5 + 450 : 450;

        frame.setSize(larguraJanela, 200);
        frame.setLayout(new GridLayout(4, 1));

        DecimalFormat numberFormat = new DecimalFormat("#.00");

        frame.setLocationRelativeTo(null);

        if (resultado.menorDistancia() >= 0) {
            frame.add(new JLabel(
                    "A menor distância entre " + Utils.capitalizeWords(inicio) +
                            " e " + Utils.capitalizeWords(destino) +
                            " é: " + numberFormat.format(resultado.menorDistancia()) + " km"));
            frame.add(new JLabel("Percurso: " + String.join(" → ", resultado.percurso())));
            frame.add(new JLabel("Tempo total da viagem: " + Utils.convertTime(resultado.tempoTotal())));
            frame.add(new JLabel("Custo total da viagem: R$ " + numberFormat.format(resultado.custoTotal())));
        } else {
            frame.add(new JLabel("Não há caminho entre " + inicio + " e " + destino));
        }

        frame.setVisible(true);
    }
}
