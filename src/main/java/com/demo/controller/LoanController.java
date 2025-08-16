package com.demo.controller;

import com.demo.model.Book;
import com.demo.model.Client;
import com.demo.model.Loan;
import com.demo.service.EmailService;

import java.time.LocalDate;
import java.util.Map;

public abstract class LoanController {

    public static void addLoan(Map<String, Book> books, Map<String, Loan> loans, Loan loan){
        if(loan.getBookISBN().isEmpty()){
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
}
