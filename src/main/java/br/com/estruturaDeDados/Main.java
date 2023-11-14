package br.com.estruturaDeDados;

public class Main {
    public static void main(String[] args) {
        System.out.println("Criando grafo!");
        var grafo = new GrafoGeografico();

        grafo.adicionarAresta("A", "B", 5.0);
        grafo.adicionarAresta("A", "C", 5.0);
        grafo.adicionarAresta("A", "D", 10.0);
        grafo.adicionarAresta("B", "C", 20.0);
        grafo.adicionarAresta("C", "D", 15.0);
        grafo.adicionarAresta("D", "E", 5.0);
        grafo.adicionarAresta("C", "E", 5.0);

        System.out.println("Grafo criado!");

        var inicio = "A";
        var destino = "E";

        var resultado = grafo.menorDistancia(inicio, destino);

        var menorDistancia = resultado.getDistancia();
        var percurso = resultado.getPercurso();

        if (menorDistancia != null) {
            if (menorDistancia >= 0) {
                System.out.println("A menor distância entre " + inicio + " e " + destino + " é: " + menorDistancia);
                System.out.println("Percurso: " + String.join(" -> ", percurso));
            } else {
                System.out.println("Não há caminho entre $pontoA e $pontoB");
            }
        }
    }
}