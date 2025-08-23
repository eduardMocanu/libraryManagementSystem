package com.demo.dao.Books;

import com.demo.model.Book;

import java.util.ArrayList;
import java.util.HashMap;

public interface BooksDAO {
    void addBook(Book book);
    String getNameByISBN(String ISBN);
    void removeBookByISBN(String bookISBN);
    Book getBookISBNByName(String bookName);
    HashMap<String, ArrayList<String>> getAvailableBooksStructuredByAuthor();
}
