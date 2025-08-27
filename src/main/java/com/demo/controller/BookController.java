package com.demo.controller;

import com.demo.dao.Books.BooksDAO;
import com.demo.dao.Books.BooksDAOMysql;
import com.demo.model.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class BookController {

    private static BooksDAO booksSql = new BooksDAOMysql();

    public static boolean addBook(Book book){
        return booksSql.addBook(book);
    }

    public static boolean removeBook(String bookISBN){
        //check if book is loaned
        booksSql.removeBookByISBN(bookISBN);
    }

    public static String getBookISBNByNameAndAuthor(String name, String author){
        return booksSql.getBookISBNByNameAndAuthor(name, author);
    }

    public static HashMap<String, ArrayList<String>> getAvailableBooksByAuthor(){
        return booksSql.getAvailableBooksStructuredByAuthor();
    }

}
