package com.demo.model;

public class Book {
    private final String ISBN, name, author;
    private final int numberPages;
    private boolean statusLoaned;

    public Book(String ISBN, String name, String author, int pages, boolean statusLoaned){
        this.ISBN = ISBN;
        this.name = name;
        this.author = author;
        this.numberPages = pages;
        this.statusLoaned = statusLoaned;
    }
    //GETTERS
    public String getISBN(){
        return this.ISBN;
    }

    public String getName(){
        return this.name;
    }

    public String getAuthor(){
        return this.author;
    }

    public int getNumberPages(){
        return this.numberPages;
    }

    public boolean getStatusLoaned(){
        return this.statusLoaned;
    }

    //SETTERS
    public void setStatusLoaned(boolean status){
        this.statusLoaned = status;
    }
}
