package com.demo.dao.Books;

import com.demo.model.Book;

import java.util.ArrayList;
import java.util.HashMap;

public interface BooksDAO {
    boolean addBook(Book book);
    String getNameByISBN(String ISBN);
    boolean removeBookByISBN(String bookISBN);
    String getBookISBNByNameAndAuthor(String bookName, String author);
    HashMap<String, ArrayList<String>> getAvailableBooksStructuredByAuthor();
    boolean loanBook(String bookISBN);
    boolean giveBookBack(String bookISBN);
    Book getBookObjByISBN(String bookISBN);
}
