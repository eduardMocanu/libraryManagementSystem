package com.demo.model;

import java.util.Map;

public class Library {
    private Map<String, Client> clients;
    private Map<String, Book> books;
    private Map<String, Loan> loans;

    public Library(Map<String, Client> clients, Map<String, Book> books, Map<String, Loan> loans){
        this.clients = clients;
        this.books = books;
        this.loans = loans;
    }


}
