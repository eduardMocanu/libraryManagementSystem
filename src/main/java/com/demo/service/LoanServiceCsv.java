package com.demo.service;

import com.demo.controller.LoanController;
import com.demo.model.Loan;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.time.*;

public class LoanServiceCsv {
    private String[] header;
    private final String filePath;

    public LoanServiceCsv(String filePath){
        this.filePath = filePath;
    }

    public Map<String, Loan> readCSVFile(){
        Map<String, Loan> fileRead = new HashMap<>();
        try(CSVReader csvReader = new CSVReader(new FileReader(filePath))){
            header = csvReader.readNext();
            String[] row;
            while((row = csvReader.readNext()) != null){
                LocalDate startTime = LocalDate.parse(row[1]), endTime = LocalDate.parse(row[2]);
                Loan loan = new Loan(row[0].trim(), startTime, endTime, row[3], row[4], Boolean.parseBoolean(row[5]), Boolean.parseBoolean(row[6]));
                fileRead.put(row[0], loan);
            }
        }
        catch(IOException | CsvValidationException e){
            System.out.println("Error " + e.getMessage() + " appeared");
        }
        return fileRead;
    }

    public void writeCSVFile(Map<String, Loan> loans){
        try(CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath))){
            csvWriter.writeNext(header);
            for(Loan i:loans.values()){
                String[] line = {i.getId().trim(), i.getLoanStart().toString(), i.getLoanEnd().toString(), i.getClientId(), i.getBookISBN(), String.valueOf(i.getActive()), String.valueOf(i.getEmailed())};
                csvWriter.writeNext(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
