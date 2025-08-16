package com.demo.controller;

import com.demo.model.Book;

import java.util.Map;

public abstract class BookController {

    public static void addBook(Map<String, Book> books, Book book){
        if(books.containsKey(book.getISBN())){
            System.out.println("Can't add the book wanted, the ISBN is already in the database");
        }
        else{
            books.put(book.getISBN(), book);
            System.out.println("Book "+ book.getName() + " successfully added");
        }
    }

    public static void removeBook(Map<String, Book> books, String bookISBN){
        if(!books.containsKey(bookISBN)){
            System.out.println("The book you want to delete is not in the library");
        }
        else{
            if(books.get(bookISBN).getStatusLoaned()){
                System.out.println("Can't remove the book because it is loaned");
            }
            else{
                books.remove(bookISBN);
                System.out.println("Book deleted successfully.");
            }

        }
    }

    public static String getBookISBNByName(Map<String, Book> books, String name){
        for(Book i: books.values()){
            if(i.getName().equals(name.toUpperCase().trim())){
                return i.getISBN();
            }
        }
        return "";
    }
}
