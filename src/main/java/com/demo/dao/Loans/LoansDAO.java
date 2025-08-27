package com.demo.dao.Loans;

import com.demo.model.Loan;

import java.util.HashMap;
import java.util.HashSet;

public interface LoansDAO {
    void addLoan(Loan loan);
    HashMap<Integer, Loan> getExpiredLoans();
    void deactivateLoan(Integer loanId);
    HashMap<Integer, Loan> getExpiredLoansOfClientById(Integer clientId);
    Integer getLoanIdByBookISBNAndClientId(String bookISBN, Integer clientId);
    HashSet<Loan> getActiveLoansOfClientByClientId(Integer clientId);
    HashMap<Integer, Loan> getHistoryOfClientByClientId(Integer clientId);
    void returnBook(Integer clientId, String bookISBN);
}
