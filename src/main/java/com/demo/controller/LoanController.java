package com.demo.controller;

import com.demo.model.Book;
import com.demo.model.Client;
import com.demo.model.Loan;
import com.demo.service.EmailService;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class LoanController {

    public static void addLoan(Map<String, Book> books, Map<String, Loan> loans, Loan loan){
        Set<String> booksISBNExpired = checkExpiredLoansClient(loans, loan.getClientId());
        if(!booksISBNExpired.isEmpty()){
            for(String i:booksISBNExpired){
                System.out.println("Can't loan a book because you have an expired loan already for the book " + books.get(i).getName());
            }
        }
        else if(loan.getBookISBN().isEmpty()){
            System.out.println("The ISBN is not found");
        }
        else if(loans.containsKey(loan.getId())){
            System.out.println("Can't add the loan, the id is already added");
        }
        else if(books.get(loan.getBookISBN()).getStatusLoaned()){
            System.out.println("The book you want to loan is already loaned");
        }
        else{
            loans.put(loan.getId(), loan);
            System.out.println("The loan was successfully added to the database with the id " + loan.getId());
            //modify the status of the book to true; statusBookLoaned = true
            books.get(loan.getBookISBN()).setStatusLoaned(true);
        }
    }

    public static void checkAllLoansSendEmail(Map<String, Loan> loans, Map<String, Client> clients, Map<String, Book> books){
        LocalDate currentDate = LocalDate.now();
        boolean ok = true;
        for(Loan i: loans.values()){
            if(currentDate.isAfter(i.getLoanEnd()) && i.getActive()){
                if(!i.getEmailed()){
                    EmailService.sendEmailTo(clients.get(i.getClientId()).getEmail(), books.get(i.getBookISBN()).getName(), books.get(i.getBookISBN()).getAuthor(), clients.get(i.getClientId()).getName(), String.valueOf(i.getLoanEnd()));
                    i.setEmailed(true);
                    System.out.println("Email sent to " + clients.get(i.getClientId()).getName());
                }else{
                    System.out.println(clients.get(i.getClientId()).getFullName() + " has a loan that expired but was emailed already.");
                }
                ok = false;
            }
        }
        if(ok){
            System.out.println("All loans are OK");
        }
    }

    public static void deactivateLoan(Map<String, Loan> loans, Map<String, Book> books, String id){
        if(loans.containsKey(id) && loans.get(id).getActive()){
            String bookISBN = loans.get(id).getBookISBN();
            books.get(bookISBN).setStatusLoaned(false);
            loans.get(id).setActive(false);
            System.out.println("loan " + id + " deactivated successfully");
        }
        else if(!loans.containsKey(id)){
            System.out.println("The given id of the loan does not exist in the database");
        }
        else{
            System.out.println("The given id of the loan is not active anymore");
        }
    }

    public static String getNewLoanId(Map<String, Loan> loans){
        int maximum = 0;
        for(String i:loans.keySet()){
            maximum = Math.max(maximum, Integer.parseInt(i));
        }
        return String.valueOf(maximum+1);
    }

    public static Set<String> checkExpiredLoansClient(Map<String, Loan> loans, String clientId){
        Set<String> returnValues = new HashSet<>();
        for(Loan i:loans.values()){
            if(i.getClientId().equals(clientId) && i.getActive() && i.getLoanEnd().isAfter(LocalDate.now())){
                returnValues.add(i.getBookISBN());
            }
        }
        return returnValues;
    }

    public static void giveBookBack(Map<String, Loan> loans, Map<String, Client> clients, Map<String, Book> books, String bookISBN, String clientID){
        if(!books.containsKey(bookISBN)){
            System.out.println("The given book is not in the database.");
        } else if (!books.get(bookISBN).getStatusLoaned()) {
            System.out.println("The given book is not loaned.");
        }else if(!clients.containsKey(clientID)){
            System.out.println("The given ID is not a registered member");
        }else{
            boolean ok = false;
            for(Loan i: loans.values()){
                if(i.getClientId().equals(clientID) && i.getBookISBN().equals(bookISBN) && i.getActive()){
                    ok = true;
                    i.setActive(false);
                    books.get(bookISBN).setStatusLoaned(false);
                    System.out.println("Thank you " + clients.get(clientID).getName() + " for giving the book back");
                    break;
                }
            }
            if(!ok){
                System.out.println("The given client doesn't have such book loaned");
            }
        }
    }
}
