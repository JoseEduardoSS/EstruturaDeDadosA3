package br.com.estruturaDeDados;

import org.apache.commons.csv.CSVRecord;

import java.util.*;

public class GrafoGeografico {

    // Definição das constantes de consumo, custo de pedágio e preço do combustível para diferentes meios de transporte
    private static final double CONSUMO_ONIBUS = 4.0;
    private static final double CONSUMO_CARRO = 15.0;
    private static final double CONSUMO_MOTO = 20.0;
    private static final double CONSUMO_CAMINHAO = 3.0;

    private static final double CUSTO_PEDAGIO_CARRO = 7.30;
    private static final double CUSTO_PEDAGIO_MOTO = 3.65;
    private static final double CUSTO_PEDAGIO_ONIBUS = 13.80;
    private static final double CUSTO_PEDAGIO_CAMINHAO = 20.70;

    private static final double PRECO_COMBUSTIVEL = 5.63;

    // Mapa para representar o grafo, onde cada ponto tem associação com outros pontos e a respectiva aresta
    public final Map<String, Map<String, Aresta>> grafo = new HashMap<>();

    private static final GuiView view = new GuiView();

    // Método para adicionar uma aresta ao grafo, associando dois pontos com a respectiva aresta
    private void adicionarAresta(String pontoA, String pontoB, Aresta aresta) {
        grafo.computeIfAbsent(Objects.requireNonNull(pontoA), k -> new HashMap<>()).put(Objects.requireNonNull(pontoB), aresta);
        grafo.computeIfAbsent(Objects.requireNonNull(pontoB), k -> new HashMap<>()).put(pontoA, aresta);

        view.montarNosEArestasDoGrafo(pontoA,pontoB,aresta.distancia());
    }

    // Método principal para calcular a menor distância entre dois pontos, considerando um meio de transporte
    public RetornoCalculo menorDistancia(String pontoA, String pontoB, String meioTransporte) {
        // Inicialização de estruturas para armazenar distâncias, tempos, predecessores e uma fila de prioridade
        // para determinar a próxima posição a ser avaliada no algoritmo de Dijkstra
        pontoA = Objects.requireNonNull(pontoA);
        pontoB = Objects.requireNonNull(pontoB);
        var distancias = new HashMap<String, Double>();
        var tempos = new HashMap<String, Integer>();
        var filaDePrioridade = new PriorityQueue<String>(Comparator.comparingDouble(distancias::get));
        var predecessores = new HashMap<String, String>();

        // Inicialização da distância do ponto de origem (pontoA) como 0
        distancias.put(pontoA, 0.0);
        filaDePrioridade.add(pontoA);

        // Algoritmo de Dijkstra para encontrar a menor distância
        while (!filaDePrioridade.isEmpty()) {
            var pontoAtual = Objects.requireNonNull(filaDePrioridade.poll());

            if (pontoAtual.equals(pontoB)) {
                // Se o ponto atual for igual ao ponto de destino, constrói o percurso e retorna os resultados
                var percurso = construirPercurso(predecessores, pontoA, pontoB);
                var tempoTotal = Objects.requireNonNull(tempos.get(pontoB));
                return new RetornoCalculo(Objects.requireNonNull(distancias.get(pontoB)), tempoTotal, percurso, calcularCustoTotal(percurso, meioTransporte));
            }

            var entries = grafo.get(Objects.requireNonNull(pontoAtual));

            if (entries != null) {
                entries.forEach((proximoPonto, aresta) -> {
                    // Atualização das distâncias e tempos se encontrar um caminho mais curto
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

        // Se não encontrar um caminho, retorna um objeto RetornoCalculo indicando que não há caminho
        return new RetornoCalculo(-1.0, -1, Collections.emptyList(), -1.0);
    }

    // Método para construir o percurso a partir dos predecessores
    private List<String> construirPercurso(Map<String, String> predecessores, String pontoA, String pontoB) {
        List<String> percurso = new ArrayList<>();
        String pontoAtual = Objects.requireNonNull(pontoB);

        while (!pontoAtual.equals(pontoA)) {
            percurso.add((Objects.requireNonNull(pontoAtual)));
            pontoAtual = Objects.requireNonNull(predecessores.getOrDefault(pontoAtual, null));
        }

        percurso.add((Objects.requireNonNull(pontoA)));
        Collections.reverse(percurso);
        return percurso;
    }

    // Método para calcular o custo total do percurso considerando o meio de transporte
    private double calcularCustoTotal(List<String> percurso, String meioTransporte) {
        var custoTotal = 0.0;

        for (int i = 0; i < percurso.size() - 1; i++) {
            var pontoAtual = Objects.requireNonNull(percurso.get(i));
            var proximoPonto = Objects.requireNonNull(percurso.get(i + 1));

            var pontoAtualArestas = grafo.get(Objects.requireNonNull(pontoAtual));

            // Verifica se o ponto atual está presente no grafo
            if (pontoAtualArestas != null) {
                var aresta = pontoAtualArestas.get(Objects.requireNonNull(proximoPonto));

                // Verifica se a aresta entre pontoAtual e proximoPonto está presente no grafo
                if (aresta != null) {
                    double consumo = obterConsumoMedio(meioTransporte);
                    double distancia = aresta.distancia();
                    double litrosConsumidos = distancia / consumo;

                    if (possuiPedagio(pontoAtual, proximoPonto)) {
                        double custoPedagio = calcularCustoPedagio(meioTransporte);
                        custoTotal += custoPedagio;
                    }

                    // Adiciona o custo do combustível
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

    // Verifica se existe pedágio entre dois pontos específicos
    private boolean possuiPedagio(String pontoA, String pontoB) {
        return (Objects.requireNonNull(pontoA).equals("Fazenda Rio Grande") && Objects.requireNonNull(pontoB).equals("Mandirituba")) ||
                (Objects.requireNonNull(pontoA).equals("Mandirituba") && Objects.requireNonNull(pontoB).equals("Fazenda Rio Grande")) ||
                (Objects.requireNonNull(pontoA).equals("Rio Negro") && Objects.requireNonNull(pontoB).equals("Campo do Tenente")) ||
                (Objects.requireNonNull(pontoA).equals("Campo do Tenente") && Objects.requireNonNull(pontoB).equals("Rio Negro"));
    }

    // Calcula o custo do pedágio com base no meio de transporte
    private double calcularCustoPedagio(String meioTransporte) {
        return switch (Objects.requireNonNull(meioTransporte)) {
            case "carro" -> CUSTO_PEDAGIO_CARRO;
            case "moto" -> CUSTO_PEDAGIO_MOTO;
            case "onibus" -> CUSTO_PEDAGIO_ONIBUS;
            case "caminhao" -> CUSTO_PEDAGIO_CAMINHAO;
            default -> 0.0;
        };
    }

    // Obtém o consumo médio do veículo com base no meio de transporte
    private double obterConsumoMedio(String meioTransporte) {
        return switch (Objects.requireNonNull(meioTransporte)) {
            case "carro" -> CONSUMO_CARRO;
            case "moto" -> CONSUMO_MOTO;
            case "onibus" -> CONSUMO_ONIBUS;
            case "caminhao" -> CONSUMO_CAMINHAO;
            default -> 0.0;
        };
    }

    // Método para construir o grafo com base nos dados do CSV
    public void ConstruirGrafo(List<CSVRecord> csvRecords) {
        csvRecords.forEach(data -> {
            var distancia = Double.parseDouble(Objects.requireNonNull(data.get(2)));
            var tempo = Integer.parseInt(Objects.requireNonNull(data.get(3)));

            adicionarAresta(Objects.requireNonNull(data.get(0)), Objects.requireNonNull(data.get(1)), new Aresta(distancia, tempo));
        });
    }
}

