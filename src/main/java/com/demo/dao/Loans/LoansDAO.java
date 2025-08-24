package com.demo.dao.Loans;

import com.demo.model.Loan;

import java.util.HashMap;

public interface LoansDAO {
    void addLoan(Loan loan);
    HashMap<String, Loan> getExpiredLoans();
    void deactivateLoan(Integer loanId);
    void getExpiredLoansClientById(Integer clientId);
    String getLoanIdByBookISBNAndClientId(String bookISBN, Integer clientId);
    HashMap<Integer, Loan> getActiveLoansOfClientByClientId(Integer clientId);
    HashMap<Integer, Loan> getHistoryOfClientByClientId(Integer clientId);
}
