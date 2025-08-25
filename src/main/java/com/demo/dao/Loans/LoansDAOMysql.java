package com.demo.dao.Loans;

import com.demo.dbConnection.DbConnection;
import com.demo.dbConnection.MysqlDbConnection;
import com.demo.model.Loan;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.function.Function;

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
        String sqlQuery = "UPDATE Loans SET Active = 0 WHERE Id = ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setInt(1, loanId);
            int affectedRows = preparedStatement.executeUpdate();
            System.out.println(affectedRows + " rows affected");
        } catch (SQLException e) {
            errorManager(e.getMessage());
        }
    }

    @Override
    public HashMap<Integer, Loan> getExpiredLoansOfClientById(Integer clientId) {
        HashMap<Integer, Loan> expiredLoans = new HashMap<>();
        String sqlQuery = "SELECT * FROM Loans WHERE ClientId = ? AND LoanEnd < ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setInt(1, clientId);
            preparedStatement.setDate(2, Date.valueOf(LocalDate.now()));
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                expiredLoans.put(rs.getObject("Id", Integer.class), new Loan(rs.getInt("Id"), rs.getDate("LoanStart").toLocalDate(), rs.getDate("LoanEnd").toLocalDate(), rs.getObject("ClientId", Integer.class), rs.getString("BookISBN"), rs.getBoolean("Active"), rs.getBoolean("Emailed")));
            }
        }catch (SQLException e){
            errorManager(e.getMessage());
        }
        return expiredLoans;
    }

    @Override
    public String getLoanIdByBookISBNAndClientId(String bookISBN, Integer clientId) {
        String sqlQuery = "SELECT Id FROM Loans WHERE BookISBN = ? AND ClientId = ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setString(1, bookISBN);
            preparedStatement.setInt(2, clientId);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return rs.getString("Id");
            }
        }catch(SQLException e){
            errorManager(e.getMessage());
        }
        return null;
    }

    @Override
    public HashMap<Integer, Loan> getActiveLoansOfClientByClientId(Integer clientId) {
        HashMap<Integer, Loan> activeLoans = new HashMap<>();
        String sqlQuery = "SELECT * FROM Loans WHERE ClientId = ? AND Active = TRUE;";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)) {
            preparedStatement.setInt(1, clientId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                activeLoans.put(rs.getObject("Id", Integer.class), readToLoan.apply(rs));
            }
        } catch (SQLException e) {
            errorManager(e.getMessage());
        }

        return activeLoans;
    }

    @Override
    public HashMap<Integer, Loan> getHistoryOfClientByClientId(Integer clientId) {
        HashMap<Integer, Loan> activeLoans = new HashMap<>();
        String sqlQuery = "SELECT * FROM Loans WHERE ClientId = ?";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)) {
            preparedStatement.setInt(1, clientId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                activeLoans.put(rs.getObject("Id", Integer.class), readToLoan.apply(rs));
            }
        } catch (SQLException e) {
            errorManager(e.getMessage());
        }
        return activeLoans;
    }
    //this basically overwrites the apply method that is in Function interface
    Function<ResultSet, Loan> readToLoan = resultSet ->{
        Loan returnValue = null;
        try{
            returnValue = new Loan(resultSet.getObject("Id", Integer.class),
                    resultSet.getDate("LoanStart").toLocalDate(),
                    resultSet.getDate("LoanEnd").toLocalDate(),
                    resultSet.getObject("ClientId", Integer.class),
                    resultSet.getString("BookISBN"),
                    resultSet.getBoolean("Active"),
                    resultSet.getBoolean("Emailed"));
        }catch(SQLException e){
            throw new RuntimeException("Error mapping loan", e);
        }
        return returnValue;
    };

    private void errorManager(String value){
        System.out.println(value + " error occurred");
    }
}
