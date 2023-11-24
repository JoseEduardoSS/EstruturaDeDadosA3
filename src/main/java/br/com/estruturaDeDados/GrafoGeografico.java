package br.com.estruturaDeDados;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class GrafoGeografico {
    private final Map<String, Map<String, Aresta>> grafo = new HashMap<>();

    private static final String CSVPATH = "C:\\Workspace\\projects\\EstruturaDeDadosA3\\src\\main\\java\\br\\com\\estruturaDeDados\\Dados.csv";

    //computeIfAbsent(pontoA) verifica se o ponto pontoA já existe no grafo.
    //Se não existir, cria um novo mapa vazio associado a esse ponto.
    //Em seguida, .put(pontoB, distancia) adiciona uma entrada no mapa associado a pontoA,
    //onde a chave é pontoB (o ponto vizinho) e o valor é a distância entre pontoA e pontoB.
    //O mesmo processo é repetido para pontoB e sua conexão com pontoA
    private void adicionarAresta(String pontoA, String pontoB, Aresta aresta) {
        grafo.computeIfAbsent(pontoA.toLowerCase(), k -> new HashMap<>()).put(pontoB.toLowerCase(), aresta);
        grafo.computeIfAbsent(pontoB.toLowerCase(), k -> new HashMap<>()).put(pontoA.toLowerCase(), aresta);
    }

    public RetornoCalculo menorDistancia(String pontoA, String pontoB) {
        pontoA = pontoA.toLowerCase();
        pontoB = pontoB.toLowerCase();

        var distancias = new HashMap<String, Double>();

        //filaDePrioridade classificará as strings com base nos valores correspondentes no mapa distancias.
        //As strings com valores menores no mapa terão maior prioridade na fila
        var filaDePrioridade = new PriorityQueue<String>(Comparator.comparingDouble(distancias::get));
        var predecessores = new HashMap<String, String>();

        distancias.put(pontoA, 0.0);
        filaDePrioridade.add(pontoA);

        while (!filaDePrioridade.isEmpty()) {
            var pontoAtual = filaDePrioridade.poll();

            //define o final do loop while e retorna os valores
            if (pontoAtual.equals(pontoB)) {
                var percurso = construirPercurso(predecessores, pontoA, pontoB);
                return new RetornoCalculo(distancias.get(pontoB), percurso);
            }

            //entries ira pegar o map de adjacências do ponto atual
            var entries = grafo.get(pontoAtual);

            if (entries != null) {
                entries.forEach((proximoPonto, distancia) -> {

                    //verifica se já calculamos alguma distância para o ponto atual. Se ainda não calculamos,
                    //retorna 0.0 como valor padrão. Adicionamos a distância do ponto atual (distancia) a essa distância,
                    //criando assim uma possível nova distância para chegar ao próximo ponto
                    var novaDistancia = distancias.getOrDefault(pontoAtual, 0.0) + distancia.distancia();

                    //verifica se o próximo ponto ainda não tem uma
                    //distância calculada (ou seja, é a primeira vez que chegamos a ele)
                    if (!distancias.containsKey(proximoPonto)

                            //verifica se a nova distância que calculamos é menor do que a distância atualmente
                            //registrada para o próximo ponto (ou se ainda não foi registrada, assume 0.0 como valor padrão)
                            || novaDistancia < distancias.getOrDefault(proximoPonto, 0.0)) {

                        //atualiza a distância para o próximo ponto com a nova distância calculada
                        distancias.put(proximoPonto, novaDistancia);

                        //adiciona o próximo ponto à fila de prioridade. Isso significa que
                        //planejamos explorar as arestas saindo desse ponto a partir de agora
                        filaDePrioridade.add(proximoPonto);
                        predecessores.put(proximoPonto, pontoAtual);
                    }
                });
            }
        }

        return new RetornoCalculo(-1.0, Collections.emptyList());
    }

    private List<String> construirPercurso(Map<String, String> predecessores, String pontoA, String pontoB) {
        List<String> percurso = new ArrayList<>();
        String pontoAtual = pontoB.toLowerCase();

        while (!pontoAtual.equals(pontoA)) {
            percurso.add(Utils.capitalizeWords(pontoAtual));
            pontoAtual = predecessores.getOrDefault(pontoAtual, null);
            if (pontoAtual == null) {
                break;
            }
        }

        percurso.add(Utils.capitalizeWords(pontoA));
        Collections.reverse(percurso);
        return percurso;
    }

    public void ConstruirGrafo() {
        try (var leitor = new FileReader(CSVPATH)) {
            var csvParser = new CSVParser(leitor, CSVFormat.DEFAULT);


            csvParser.forEach(data -> {
                var distancia = Double.parseDouble(data.get(2));
                var tempo = Double.parseDouble(data.get(3));

                adicionarAresta(data.get(0), data.get(1), new Aresta(distancia, tempo));
            });
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}