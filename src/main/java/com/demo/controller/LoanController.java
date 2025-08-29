package com.demo.controller;

import com.demo.dao.Books.BooksDAO;
import com.demo.dao.Books.BooksDAOMysql;
import com.demo.dao.Clients.ClientsDAO;
import com.demo.dao.Clients.ClientsDAOMysql;
import com.demo.dao.Loans.LoansDAO;
import com.demo.dao.Loans.LoansDAOMysql;
import com.demo.model.Client;
import com.demo.model.Loan;
import com.demo.service.EmailService;

import java.util.HashMap;
import java.util.HashSet;

public abstract class LoanController {

    private static LoansDAO loanSql = new LoansDAOMysql();
    private static ClientsDAO clientSql = new ClientsDAOMysql();
    private static BooksDAO booksSql = new BooksDAOMysql();

    public static boolean addLoan(Loan loan) {
        boolean ok = booksSql.loanBook(loan.getBookISBN());
        if(ok){
            boolean addedLoan = loanSql.addLoan(loan);
            if(!addedLoan){
                booksSql.giveBookBack(loan.getBookISBN());
            }else{
                return true;
            }
        }
        return false;
    }

    public static void checkAllLoansSendEmail() {
        HashMap<Integer, Loan> expiredLoans = loanSql.getExpiredLoans();
        for (Loan i : expiredLoans.values()) {
            Client client = clientSql.getClientObjById(i.getClientId());
            String bookName = booksSql.getNameByISBN(i.getBookISBN());
            if (!i.getEmailed()) {
                if (client != null && bookName != null) {
                    EmailService.sendEmailTo(client.getEmail(), bookName, client.getName(), String.valueOf(i.getLoanEnd()));
                    System.out.println("Email sent to " + client.getName() + " for book: " + bookName);
                }
            } else {
                System.out.println("The client " + client.getName() + " was already emailed for the loan of book " + bookName);
            }
        }
    }

    public static int deactivateLoan(Integer loanId) {
        String bookISBN = loanSql.getBookISBNOfLoan(loanId);
        boolean ok = loanSql.deactivateLoan(loanId);
        if(ok){
            if(booksSql.giveBookBack(bookISBN)){
                return 2;//operation successful
            }else{
                loanSql.activateLoan(loanId);
                return 1;//could not give the book back
            }
        }
        return 0; //could not deactivate loan
    }

    public static HashMap<Integer, Loan> checkExpiredLoansClient(Integer clientId) {
        return loanSql.getExpiredLoansOfClientById(clientId);
    }

    public static void giveBookBack(String bookName, String bookAuthor, Integer clientId) {
        String bookISBN = booksSql.getBookISBNByNameAndAuthor(bookName, bookAuthor);
        loanSql.returnBook(clientId, bookISBN);
    }

    public static HashSet<Loan> getActiveLoansOfClient(Integer clientId) {
        return loanSql.getActiveLoansOfClientByClientId(clientId);
    }

    public static HashMap<Integer, Loan> getHistoryOfClient(Integer clientId) {
        return loanSql.getHistoryOfClientByClientId(clientId);
    }

    public static Loan getALoanData(String bookName, String bookAuthor, Integer clientId){
        String bookISBN = booksSql.getBookISBNByNameAndAuthor(bookName, bookAuthor);
        if(bookISBN!=null){
            return loanSql.getALoanData(bookISBN, clientId);
        }else{
            return null;
        }
    }


}