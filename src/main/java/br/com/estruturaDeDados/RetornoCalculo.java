package br.com.estruturaDeDados;
import java.util.List;

public record RetornoCalculo(double menorDistancia, int tempoTotal, List<String> percurso, double custoTotal) {
}
