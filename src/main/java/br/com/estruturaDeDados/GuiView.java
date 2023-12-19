package br.com.estruturaDeDados;

import org.apache.commons.csv.CSVRecord;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class GuiView {

    public static SingleGraph graphView = new SingleGraph("Resultado");

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

        DecimalFormat numberFormat = new DecimalFormat("#.00");

        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

        if (resultado.menorDistancia() >= 0) {
            panel.add(new JLabel(
                    "A menor distância entre " + Utils.capitalizeWords(inicio) +
                            " e " + Utils.capitalizeWords(destino) +
                            " é: " + numberFormat.format(resultado.menorDistancia()) + " km"));
            panel.add(new JLabel("Tempo total da viagem: " + Utils.convertTime(resultado.tempoTotal())));
            panel.add(new JLabel("Custo total da viagem: R$ " + numberFormat.format(resultado.custoTotal())));
        } else {
            panel.add(new JLabel("Não há caminho entre " + inicio + " e " + destino));
        }

        var viewer = montarVisualizacaoDoGrafo(resultado.percurso());

        frame.setLayout(new BorderLayout());
        frame.add(panel, BorderLayout.NORTH);
        frame.add((Component) viewer.addDefaultView(false), BorderLayout.CENTER);
        frame.setSize(1200, 800);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    private static Viewer montarVisualizacaoDoGrafo(List<String> percurso) {
        System.setProperty("org.graphstream.ui", "swing");

        Viewer viewer = new Viewer(graphView, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        viewer.enableAutoLayout();

        viewer.enableAutoLayout();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER);

        for (int i = 0; i < percurso.size() - 1; i++) {
            var pontoA = percurso.get(i);
            var pontoB = percurso.get(i + 1);

            graphView.getNode(pontoA).setAttribute("ui.style", "fill-color: red;");
            graphView.getNode(pontoB).setAttribute("ui.style", "fill-color: red;");

            var edgeId = pontoA + pontoB;
            var edge = graphView.getEdge(edgeId);

            if (edge == null) {
                edgeId = pontoB + pontoA;
                edge = graphView.getEdge(edgeId);
            }

            if (edge != null) {
                edge.setAttribute("ui.style", "fill-color: red;");
            }
        }

        String styleSheet =
                "node {" +
                        "   fill-color: blue;" +
                        "   size: 30px, 20px;" +
                        "   text-size: 15px;" +
                        "}" +
                        "edge {" +
                        "   fill-color: black;" +
                        "   size: 3px;" +
                        "   text-size: 10px;" +
                        "}";
        graphView.setAttribute("ui.stylesheet", styleSheet);

        return viewer;
    }

    public void montarNosEArestasDoGrafo(String pontoA, String pontoB, Double distancia) {
        var nodeA = graphView.getNode(pontoA);
        var nodeB = graphView.getNode(pontoB);

        if (nodeA == null) {
            graphView.addNode(pontoA).setAttribute("ui.label", pontoA);
        }

        if (nodeB == null) {
            graphView.addNode(pontoB).setAttribute("ui.label", pontoB);
        }

        var edgeId = pontoA + pontoB;

        var edge = graphView.getEdge(edgeId);

        graphView.addEdge(edgeId, pontoA, pontoB).setAttribute("ui.label", distancia.toString() + " km");
    }
}
