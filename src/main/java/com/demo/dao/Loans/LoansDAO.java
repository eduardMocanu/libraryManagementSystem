package com.demo.dao.Loans;

import com.demo.model.Loan;
import jakarta.mail.internet.InternetAddress;

import java.util.HashMap;
import java.util.HashSet;

public interface LoansDAO {
    boolean addLoan(Loan loan);
    HashMap<Integer, Loan> getExpiredLoans();
    boolean deactivateLoan(Integer loanId);
    HashMap<Integer, Loan> getExpiredLoansOfClientById(Integer clientId);
    Integer getLoanIdByBookISBNAndClientId(String bookISBN, Integer clientId);
    HashSet<Loan> getActiveLoansOfClientByClientId(Integer clientId);
    HashMap<Integer, Loan> getHistoryOfClientByClientId(Integer clientId);
    boolean returnBook(Integer clientId, String bookISBN);
    Loan getALoanData(String bookISBN, Integer clientId);
    boolean checkIfISBNIsLoaned(String bookISBN);
    String getBookISBNOfLoan(Integer loanId);
    boolean activateLoan(Integer loanId);
}
