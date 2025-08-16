package com.demo.service;

import com.demo.model.Book;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class BookServiceCsv {
    private String[] header;
    private final String filePath;

    public BookServiceCsv(String filePath){
        this.filePath = filePath;
    }

    public Map<String, Book> readCSVFile(){

        Map<String, Book> fileRead = new HashMap<>();
        try(CSVReader csvReader = new CSVReader(new FileReader(filePath))){
            header = csvReader.readNext();
            String[] row;
            while((row = csvReader.readNext()) != null){
                Book book = new Book(row[0].trim(), row[1].toUpperCase().trim(), row[2].toUpperCase().trim(), Integer.parseInt(row[3]), Boolean.parseBoolean(row[4]));
                fileRead.put(row[0], book);
            }
        }
        catch(IOException | CsvValidationException e){
            System.out.println("Error " + e.getMessage() + " appeared");
        }
        return fileRead;
    }

    public void writeCSVFile(Map<String, Book> books){
        try(CSVWriter csvWriter = new CSVWriter(new FileWriter(filePath))){
            csvWriter.writeNext(header);
            for(Book i:books.values()){
                String[] line = {i.getISBN().trim(), i.getName().toUpperCase().trim(), i.getAuthor().toUpperCase().trim(), String.valueOf(i.getNumberPages()).trim(), String.valueOf(i.getStatusLoaned()).trim()};
                csvWriter.writeNext(line);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
