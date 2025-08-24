package com.demo.dao.Loans;

import com.demo.dbConnection.DbConnection;
import com.demo.dbConnection.MysqlDbConnection;
import com.demo.model.Loan;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;

public class LoansDAOMysql implements LoansDAO{
    private final Connection conn;

    public LoansDAOMysql(){
        this.conn = MysqlDbConnection.getInstance().getConnection();
    }

    @Override
    public void addLoan(Loan loan) {
        String sqlQuery = "INSERT INTO Loans (LoanStart, LoanEnd, ClientId, BookISBN) VALUES (?, ?, ?, ?);";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setDate(1, Date.valueOf(loan.getLoanStart()));
            preparedStatement.setDate(2, Date.valueOf(loan.getLoanEnd()));
            preparedStatement.setInt(3, loan.getClientId());
            preparedStatement.setString(4, loan.getBookISBN());
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");
        }catch(SQLException e){
            errorManager(e.getMessage());
        }
    }

    @Override
    public HashMap<Integer, Loan> getExpiredLoans() {
        String sqlQuery = "SELECT * FROM Loans WHERE LoanEnd < ?;";
        HashMap<Integer, Loan> expiredLoans = new HashMap<>();
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setDate(1, Date.valueOf(LocalDate.now()));
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                expiredLoans.put(rs.getObject("Id", Integer.class), new Loan(rs.getInt("Id"), rs.getDate("LoanStart").toLocalDate(), rs.getDate("LoanEnd").toLocalDate(), rs.getObject("ClientId", Integer.class), rs.getString("BookISBN"), rs.getBoolean("Active"), rs.getBoolean("Emailed")));
            }
        }catch(SQLException e){
            errorManager(e.getMessage());
        }
        return expiredLoans;
    }

    @Override
    public void deactivateLoan(Integer loanId) {

    }

    @Override
    public void getExpiredLoansClientById(Integer clientId) {

    }

    @Override
    public String getLoanIdByBookISBNAndClientId(String bookISBN, Integer clientId) {
        return "";
    }

    @Override
    public HashMap<Integer, Loan> getActiveLoansOfClientByClientId(Integer clientId) {
        return null;
    }

    @Override
    public HashMap<Integer, Loan> getHistoryOfClientByClientId(Integer clientId) {
        return null;
    }

    private void errorManager(String value){
        System.out.println(value + " error occurred");
    }
}
