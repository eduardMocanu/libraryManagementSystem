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
        if(!expiredLoans.isEmpty()){
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
        else{
            System.out.println("There are no expired loans");
        }
    }

    public static int deactivateLoan(Integer loanId) {
        String bookISBN = loanSql.getBookISBNOfLoan(loanId);
        int ok = loanSql.deactivateLoan(loanId);
        if(ok >= 1){
            if(booksSql.giveBookBack(bookISBN)){
                return 2;//operation successful
            }else{
                loanSql.activateLoan(loanId);
                return 4;//could not give book back
            }
        } else if (ok == 0) {
            return 1;//no loan active
        }else{
            return 0; //could not deactivate loan
        }
    }

    public static HashMap<Integer, Loan> checkExpiredLoansClient(Integer clientId) {
        return loanSql.getExpiredLoansOfClientById(clientId);
    }

    public static int giveBookBack(String bookName, String bookAuthor, Integer clientId) {
        String bookISBN = booksSql.getBookISBNByNameAndAuthor(bookName, bookAuthor);
        if(bookISBN != null){
            boolean okBooks = booksSql.giveBookBack(bookISBN);
            if(okBooks){
                boolean okLoans = loanSql.returnBook(clientId, bookISBN);
                if(okLoans){
                    return 2;//all good
                }else{
                    booksSql.loanBook(bookISBN);
                    return 1;//problem at loan inactivation client id is not correct in some way
                }
            }else{
                return 4;//can not give back the book, technical problem
            }
        }
        return 0;//book not in db

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