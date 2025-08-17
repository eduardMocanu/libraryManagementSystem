package com.demo.controller;

import com.demo.model.Book;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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

    public static String getBookISBNByName(Scanner scanner, Map<String, Book> books, String name){
        Map<String, String> allBooksSameName = new HashMap<>();
        for(Book i: books.values()){
            if(i.getName().equals(name.toUpperCase().trim())){
                allBooksSameName.put(i.getAuthor(), i.getISBN());
            }
        }
        if(allBooksSameName.isEmpty()){
            return "";
        }
        else if(allBooksSameName.size() == 1){
            return String.valueOf(allBooksSameName.values());
        }
        else{
            System.out.println("There are multiple books with the same title, give me the author name");
            String author = scanner.nextLine().trim().toUpperCase();
            return allBooksSameName.getOrDefault(author, "");
        }
    }
}
