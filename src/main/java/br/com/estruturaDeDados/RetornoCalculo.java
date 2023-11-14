package br.com.estruturaDeDados;

import java.util.List;

public class RetornoCalculo {
    private final Double distancia;
    private final List<String> percurso;

    public RetornoCalculo(Double distancia, List<String> caminho) {
        this.distancia = distancia;
        this.percurso = caminho;
    }

    public Double getDistancia() {
        return distancia;
    }

    public List<String> getPercurso() {
        return percurso;
    }
}