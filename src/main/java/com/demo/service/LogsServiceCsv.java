package com.demo.service;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;

public class LogsServiceCsv {
    private final String file;

    public LogsServiceCsv(String file){
        this.file = file;
    }

    public void writeToLogsCsv(String prompt){
        try(CSVWriter writer = new CSVWriter(new FileWriter(file, true))){
            String[] line = {prompt};
            writer.writeNext(line);
        }catch(Exception e){
            System.out.println("Error on writing to the log file: " + e.getMessage());
        }

    }
}
