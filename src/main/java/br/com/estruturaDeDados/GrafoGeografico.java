package br.com.estruturaDeDados;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GrafoGeografico {
    private final Map<String, Map<String, Aresta>> grafo = new HashMap<>();

    private static final double CONSUMO_ONIBUS = 4.0;
    private static final double CONSUMO_CARRO = 15.0;
    private static final double CONSUMO_MOTO = 20.0;
    private static final double CONSUMO_CAMINHAO = 3.0;

    private static final double CUSTO_PEDAGIO_CARRO = 7.30;
    private static final double CUSTO_PEDAGIO_MOTO = 3.65;
    private static final double CUSTO_PEDAGIO_ONIBUS = 13.80;
    private static final double CUSTO_PEDAGIO_CAMINHAO = 20.70;

    private static final double PRECO_COMBUSTIVEL = 5.63;

    private static final String CSVPATH = "src/main/java/br/com/estruturaDeDados/Dados.csv";

    public record Aresta(double distancia, int tempo) {
    }

    private void adicionarAresta(String pontoA, String pontoB, Aresta aresta) {
        grafo.computeIfAbsent(Objects.requireNonNull(pontoA).toLowerCase(), k -> new HashMap<>()).put(Objects.requireNonNull(pontoB).toLowerCase(), aresta);
        grafo.computeIfAbsent(Objects.requireNonNull(pontoB).toLowerCase(), k -> new HashMap<>()).put(pontoA.toLowerCase(), aresta);
    }

    public RetornoCalculo menorDistancia(String pontoA, String pontoB, String meioTransporte) {
        pontoA = Objects.requireNonNull(pontoA).toLowerCase();
        pontoB = Objects.requireNonNull(pontoB).toLowerCase();

        var distancias = new HashMap<String, Double>();
        var tempos = new HashMap<String, Integer>();

        var filaDePrioridade = new PriorityQueue<String>(Comparator.comparingDouble(distancias::get));
        var predecessores = new HashMap<String, String>();

        distancias.put(pontoA, 0.0);
        filaDePrioridade.add(pontoA);

        while (!filaDePrioridade.isEmpty()) {
            var pontoAtual = Objects.requireNonNull(filaDePrioridade.poll());

            if (pontoAtual.equals(pontoB)) {
                var percurso = construirPercurso(predecessores, pontoA, pontoB);
                var tempoTotal = Objects.requireNonNull(tempos.get(pontoB));
                return new RetornoCalculo(Objects.requireNonNull(distancias.get(pontoB)), tempoTotal, percurso, calcularCustoTotal(percurso, meioTransporte));
            }

            var entries = grafo.get(Objects.requireNonNull(pontoAtual));

            if (entries != null) {
                entries.forEach((proximoPonto, aresta) -> {
                    var novaDistancia = Objects.requireNonNull(distancias.getOrDefault(pontoAtual, 0.0)) + aresta.distancia();

                    if (!distancias.containsKey(proximoPonto) || novaDistancia < Objects.requireNonNull(distancias.getOrDefault(proximoPonto, 0.0))) {
                        distancias.put(proximoPonto, novaDistancia);
                        tempos.put(proximoPonto, Objects.requireNonNull(tempos.getOrDefault(pontoAtual, 0)) + aresta.tempo());
                        filaDePrioridade.add(proximoPonto);
                        predecessores.put(proximoPonto, pontoAtual);
                    }
                });
            }
        }

        return new RetornoCalculo(-1.0, -1, Collections.emptyList(), -1.0);
    }


    private List<String> construirPercurso(Map<String, String> predecessores, String pontoA, String pontoB) {
        List<String> percurso = new ArrayList<>();
        String pontoAtual = Objects.requireNonNull(pontoB).toLowerCase();

        while (!pontoAtual.equals(pontoA)) {
            percurso.add(Utils.capitalizeWords(Objects.requireNonNull(pontoAtual)));
            pontoAtual = Objects.requireNonNull(predecessores.getOrDefault(pontoAtual, null));
        }

        percurso.add(Utils.capitalizeWords(Objects.requireNonNull(pontoA)));
        Collections.reverse(percurso);
        return percurso;
    }

    private double calcularCustoTotal(List<String> percurso, String meioTransporte) {
        double custoTotal = 0.0;

        for (int i = 0; i < percurso.size() - 1; i++) {
            String pontoAtual = Objects.requireNonNull(percurso.get(i));
            String proximoPonto = Objects.requireNonNull(percurso.get(i + 1));

            Map<String, Aresta> pontoAtualArestas = grafo.get(Objects.requireNonNull(pontoAtual).toLowerCase());

            // Verificar se o ponto atual está presente no grafo
            if (pontoAtualArestas != null) {
                Aresta aresta = pontoAtualArestas.get(Objects.requireNonNull(proximoPonto).toLowerCase());

                // Verificar se a aresta entre pontoAtual e proximoPonto está presente no grafo
                if (aresta != null) {
                    double consumo = obterConsumoMedio(meioTransporte);  // Ajustado para obter o consumo do meio de transporte escolhido
                    double distancia = aresta.distancia();
                    double litrosConsumidos = distancia / consumo;

                    if (possuiPedagio(pontoAtual, proximoPonto)) {
                        double custoPedagio = calcularCustoPedagio(meioTransporte);
                        custoTotal += custoPedagio;
                    }

                    // Adicionar o custo do combustível
                    custoTotal += litrosConsumidos * PRECO_COMBUSTIVEL;
                } else {
                    System.out.println("Aresta entre " + pontoAtual + " e " + proximoPonto + " não encontrada no grafo.");
                }
            } else {
                System.out.println("Ponto " + pontoAtual + " não encontrado no grafo.");
            }
        }

        return custoTotal;
    }

    private boolean possuiPedagio(String pontoA, String pontoB) {
        return (Objects.requireNonNull(pontoA).equalsIgnoreCase("Fazenda Rio Grande") && Objects.requireNonNull(pontoB).equalsIgnoreCase("Mandirituba")) ||
                (Objects.requireNonNull(pontoA).equalsIgnoreCase("Mandirituba") && Objects.requireNonNull(pontoB).equalsIgnoreCase("Fazenda Rio Grande")) ||
                (Objects.requireNonNull(pontoA).equalsIgnoreCase("Rio Negro") && Objects.requireNonNull(pontoB).equalsIgnoreCase("Campo do Tenente")) ||
                (Objects.requireNonNull(pontoA).equalsIgnoreCase("Campo do Tenente") && Objects.requireNonNull(pontoB).equalsIgnoreCase("Rio Negro"));
    }

    private double calcularCustoPedagio(String meioTransporte) {
        return switch (Objects.requireNonNull(meioTransporte).toLowerCase()) {
            case "carro" -> CUSTO_PEDAGIO_CARRO;
            case "moto" -> CUSTO_PEDAGIO_MOTO;
            case "onibus" -> CUSTO_PEDAGIO_ONIBUS;
            case "caminhao" -> CUSTO_PEDAGIO_CAMINHAO;
            default -> 0.0;
        };
    }

    private double obterConsumoMedio(String meioTransporte) {
        return switch (Objects.requireNonNull(meioTransporte).toLowerCase()) {
            case "carro" -> CONSUMO_CARRO;
            case "moto" -> CONSUMO_MOTO;
            case "onibus" -> CONSUMO_ONIBUS;
            case "caminhao" -> CONSUMO_CAMINHAO;
            default -> 0.0;
        };
    }

    public void ConstruirGrafo() {
        try (var leitor = new FileReader(CSVPATH)) {
            var csvParser = new CSVParser(leitor, CSVFormat.DEFAULT);

            csvParser.forEach(data -> {
                var distancia = Double.parseDouble(Objects.requireNonNull(data.get(2)));
                var tempo = Integer.parseInt(Objects.requireNonNull(data.get(3)));

                adicionarAresta(Objects.requireNonNull(data.get(0)), Objects.requireNonNull(data.get(1)), new Aresta(distancia, tempo));
            });
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
