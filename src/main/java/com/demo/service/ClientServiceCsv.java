package com.demo.service;

import com.demo.model.Client;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClientServiceCsv {
    private String[] header;
    private final String filePath;

    public ClientServiceCsv(String filePath){
        this.filePath = filePath;
    }

    public Map<String, Client> readCSVFile(){

        Map<String, Client> fileRead = new HashMap<>();
        try(CSVReader csvReader = new CSVReader(new FileReader(filePath))){
            header = csvReader.readNext();
            String[] row;
            while((row = csvReader.readNext()) != null){
                Client client = new Client(row[2].toUpperCase().trim(), row[3].trim());
                fileRead.put(row[0], client);
            }
        }
        catch(IOException | CsvValidationException e){
            System.out.println("Error " + e.getMessage() + " appeared");
        }
        return fileRead;
    }

    public void writeCSVFile(Map<String, Client> clients){
        try(CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath))){
            csvWriter.writeNext(header);
            for(Client i:clients.values()){
                String[] line = {i.getName().toUpperCase().trim(), i.getEmail().trim()};
                csvWriter.writeNext(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
