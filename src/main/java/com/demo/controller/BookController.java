package com.demo.controller;

import com.demo.dao.Books.BooksDAOMysql;
import com.demo.model.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class BookController {

    private static BooksDAOMysql booksSql = new BooksDAOMysql();

    public static void addBook(Book book){
        booksSql.addBook(book);
    }

    public static void removeBook(String bookISBN){
        booksSql.removeBookByISBN(bookISBN);
    }

    public static String getBookISBNByNameAndAuthor(Scanner scanner, String name, String author){
        return booksSql.getBookISBNByNameAndAuthor(name, author);
    }

    public static HashMap<String, ArrayList<String>> getAvailableBooksByAuthor(){
        return booksSql.getAvailableBooksStructuredByAuthor();
    }

}
