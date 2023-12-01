package br.com.estruturaDeDados;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CsvFileReader {

    private static final String CSVPATH = "src/main/java/br/com/estruturaDeDados/Dados.csv";

    public static List<CSVRecord> getCSVData() {
        try (var leitor = new FileReader(CSVPATH);
             var csvParser = new CSVParser(leitor, CSVFormat.DEFAULT)) {
            return csvParser.getRecords();
        } catch (IOException e) {
            throw new IllegalStateException(e.getMessage());
        }
    }
}
