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

    //GETTERS
    public Map<String, Book> getBooks() {
        return books;
    }

    public Map<String, Client> getClients() {
        return clients;
    }

    public Map<String, Loan> getLoans() {
        return loans;
    }
    //ADDITION

    public void addClient(Client client){
        if(clients.containsKey(client.getId())){
            System.out.println("Unable to add the client, the id already exists");
        }
        else{
            clients.put(client.getId(), client);
        }
    }

    public void addBook(Book book){
        if(books.containsKey(book.getISBN())){
            System.out.println("Unable to add the book, the ISBN already exists");
        }
        else{
            books.put(book.getISBN(), book);
        }
    }

    public void addLoan(Loan loan){
        if(loans.containsKey(loan.getId())){
            System.out.println("Unable to add the loan, the id already exists");
        }
        else{
            loans.put(loan.getId(), loan);
        }
    }
}
