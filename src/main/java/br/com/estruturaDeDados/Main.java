package br.com.estruturaDeDados;

public class Main {

    public static void main(String[] args) {
        // Instanciando grafo
        var grafo = new GrafoGeografico();

        // Leitura de CSV
        var dados = CsvFileReader.getCSVData();

        // Iniciando grafo
        grafo.ConstruirGrafo(dados);

        // Exibindo janela principal
        GuiView.exibirJanelaPrincipal(dados, grafo);
    }
}
