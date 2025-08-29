package com.demo.controller;

import com.demo.dao.Books.BooksDAO;
import com.demo.dao.Books.BooksDAOMysql;
import com.demo.dao.Loans.LoansDAO;
import com.demo.dao.Loans.LoansDAOMysql;
import com.demo.model.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class BookController {

    private static BooksDAO booksSql = new BooksDAOMysql();
    private static LoansDAO loansSql = new LoansDAOMysql();

    public static boolean addBook(Book book){
        return booksSql.addBook(book);
    }

    public static int removeBook(String bookISBN){
        if(!loansSql.checkIfISBNIsLoaned(bookISBN)){
            if(booksSql.removeBookByISBN(bookISBN)){
                return 2;//successful
            }
            return 1;//is not in db
        }
        return 0;//loaned
    }

    public static String getBookISBNByNameAndAuthor(String name, String author){
        return booksSql.getBookISBNByNameAndAuthor(name, author);
    }

    public static HashMap<String, ArrayList<String>> getAvailableBooksByAuthor(){
        return booksSql.getAvailableBooksStructuredByAuthor();
    }

    public static Book getBookObjByISBN(String bookISBN){
        return booksSql.getBookObjByISBN(bookISBN);
    }
}
