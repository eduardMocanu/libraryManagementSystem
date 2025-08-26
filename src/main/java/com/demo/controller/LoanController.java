package com.demo.controller;

import com.demo.dao.Books.BooksDAOMysql;
import com.demo.dao.Clients.ClientsDAOMysql;
import com.demo.dao.Loans.LoansDAOMysql;
import com.demo.model.Book;
import com.demo.model.Client;
import com.demo.model.Loan;
import com.demo.service.EmailService;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class LoanController {

    private static LoansDAOMysql loanSql = new LoansDAOMysql();
    private static ClientsDAOMysql clientSql = new ClientsDAOMysql();
    private static BooksDAOMysql booksSql = new BooksDAOMysql();
    public static void addLoan(Loan loan){
        loanSql.addLoan(loan);
    }

    public static void checkAllLoansSendEmail(){
        HashMap<Integer, Loan> expiredLoans = loanSql.getExpiredLoans();
        for(Loan i : expiredLoans.values()){
            Client client = clientSql.getClientObjById(i.getClientId());
            String bookName = booksSql.getNameByISBN(i.getBookISBN());
            if(!i.getEmailed()){
                if(client != null && bookName != null){
                    EmailService.sendEmailTo(client.getEmail(), bookName, client.getName(), String.valueOf(i.getLoanEnd()));
                    System.out.println("Email sent to " + client.getName() +" for book: " + bookName);
                }
            }
            else{
                System.out.println("The client " + client.getName() + " was already emailed for the loan of book " + bookName);
            }
        }
    }

    public static void deactivateLoan(){
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

    public static String getNewLoanId(){
        int maximum = 0;
        for(String i:loans.keySet()){
            maximum = Math.max(maximum, Integer.parseInt(i));
        }
        return String.valueOf(maximum+1);
    }

    public static Set<String> checkExpiredLoansClient(){
        Set<String> returnValues = new HashSet<>();
        for(Loan i:loans.values()){
            if(i.getClientId().equals(clientId) && i.getActive() && i.getLoanEnd().isAfter(LocalDate.now())){
                returnValues.add(i.getBookISBN());
            }
        }
        return returnValues;
    }

    public static void giveBookBack(String bookISBN, String clientID){
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

    public static void removeInvalidDatesLoans(){
        for(Loan i : loans.values()){
            if(i.getLoanStart().isAfter(i.getLoanEnd()) && i.getActive()){
                deactivateLoan(loans, books, i.getId());
                System.out.println("Loan " + i.getId() + " was set as inactive because of invalid dates");
            }
        }
    }

    public static HashSet<Loan> getActiveLoansOfClient(String clientId){
        HashSet<Loan> returnValue = new HashSet<>();
        for (Loan i : loans.values()) {
            if (i.getClientId().equals(clientId) && i.getActive()) {
                returnValue.add(i);
            }
        }
        return returnValue;
    }

    public static void getHistoryOfClient(String clientId){
        boolean ok = false;
        for(Loan i:loans.values()){
            if(i.getClientId().equals(clientId)){
                ok = true;
                String active = i.getActive() ? "active" : "not active";
                System.out.println("Loan with id " + i.getId() + " is for book " + books.get(i.getBookISBN()).getName() + " and is " + active);
            }
        }
        if(!ok){
            System.out.println("You haven't loaned anything, ever");
        }
    }
}
