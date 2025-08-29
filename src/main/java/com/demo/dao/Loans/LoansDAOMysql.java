package com.demo.dao.Loans;

import com.demo.dao.Logs.LogsDAOMysql;
import com.demo.dbConnection.MysqlDbConnection;
import com.demo.model.Loan;

import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.function.Function;

public class LoansDAOMysql implements LoansDAO{
    private final Connection conn;
    private final LogsDAOMysql logsMysql;

    public LoansDAOMysql(){
        this.conn = MysqlDbConnection.getInstance().getConnection();
        this.logsMysql = new LogsDAOMysql();
    }

    @Override
    public boolean addLoan(Loan loan) {
        String sqlQuery = "INSERT INTO Loans (LoanStart, LoanEnd, ClientId, BookISBN) VALUES (?, ?, ?, ?);";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setDate(1, Date.valueOf(loan.getLoanStart()));
            preparedStatement.setDate(2, Date.valueOf(loan.getLoanEnd()));
            preparedStatement.setInt(3, loan.getClientId());
            preparedStatement.setString(4, loan.getBookISBN());
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");
            return true;
        }catch(SQLException e){
            errorManager(e.getMessage());
            return false;
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
                expiredLoans.put(rs.getObject("Id", Integer.class), new Loan(rs.getDate("LoanStart").toLocalDate(), rs.getDate("LoanEnd").toLocalDate(), rs.getObject("ClientId", Integer.class), rs.getString("BookISBN"), rs.getBoolean("Active"), rs.getBoolean("Emailed")));
            }
        }catch(SQLException e){
            errorManager(e.getMessage());
        }
        return expiredLoans;
    }

    @Override
    public boolean deactivateLoan(Integer loanId) {
        String sqlQuery = "UPDATE Loans SET Active = 0 WHERE Id = ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setInt(1, loanId);
            int affectedRows = preparedStatement.executeUpdate();
            System.out.println(affectedRows + " rows affected");
            return true;
        } catch (SQLException e) {
            errorManager(e.getMessage());
            return false;
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
                expiredLoans.put(rs.getObject("Id", Integer.class), new Loan(rs.getDate("LoanStart").toLocalDate(), rs.getDate("LoanEnd").toLocalDate(), rs.getObject("ClientId", Integer.class), rs.getString("BookISBN"), rs.getBoolean("Active"), rs.getBoolean("Emailed")));
            }
        }catch (SQLException e){
            errorManager(e.getMessage());
        }
        return expiredLoans;
    }

    @Override
    public Integer getLoanIdByBookISBNAndClientId(String bookISBN, Integer clientId) {
        String sqlQuery = "SELECT Id FROM Loans WHERE BookISBN = ? AND ClientId = ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setString(1, bookISBN);
            preparedStatement.setInt(2, clientId);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return rs.getObject("Id", Integer.class);
            }
        }catch(SQLException e){
            errorManager(e.getMessage());
        }
        return null;
    }

    @Override
    public HashSet<Loan> getActiveLoansOfClientByClientId(Integer clientId) {
        HashSet<Loan> activeLoans = new HashSet<>();
        String sqlQuery = "SELECT * FROM Loans WHERE ClientId = ? AND Active = TRUE;";

        try (PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)) {
            preparedStatement.setInt(1, clientId);
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                activeLoans.add(readToLoan.apply(rs));
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

    @Override
    public boolean returnBook(Integer clientId, String bookISBN) {
        String sqlQuery = "UPDATE Loans SET Active = 0 WHERE ClientId = ? AND BookISBN = ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setInt(1, clientId);
            preparedStatement.setString(2, bookISBN);
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected == 0){
                System.out.println("No loan with this data");
                return false;
            }else{
                System.out.println("Loan updated successfully");
                return true;
            }
        }catch (SQLException e){
            errorManager(e.getMessage());
        }
        return false;
    }

    @Override
    public Loan getALoanData(String bookISBN, Integer clientId) {
        String sqlQuery = "SELECT * FROM Loans WHERE BookISBN = ? AND ClientId = ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setString(1, bookISBN);
            preparedStatement.setInt(2, clientId);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return new Loan(rs.getDate("LoanStart").toLocalDate(), rs.getDate("LoanEnd").toLocalDate(), clientId, bookISBN, rs.getBoolean("Active"), rs.getBoolean("Emailed"));
            }
        }catch(SQLException e){
            errorManager(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean checkIfISBNIsLoaned(String bookISBN) {
        String sqlQUery = "SELECT * FROM Loans WHERE BookISBN = ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQUery)){
            preparedStatement.setString(1, bookISBN);
            ResultSet rs = preparedStatement.executeQuery();
            if(!rs.next()){
                return false;
            }
        }catch (SQLException e){
            errorManager(e.getMessage());
        }
        return true;
    }

    @Override
    public String getBookISBNOfLoan(Integer loanId) {
        String sqlQuery = "SELECT BookISBN FROM Loans WHERE Id = ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setInt(1, loanId);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return rs.getString("BookISBN");
            }
        }catch(SQLException e){
            errorManager(e.getMessage());
        }
        return null;
    }

    @Override
    public boolean activateLoan(Integer loanId) {
        String sqlQuery = "UPDATE Loans SET Active = 1 WHERE Id = ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setInt(1, loanId);
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected == 1){
                return true;
            }
        }catch (SQLException e){
            errorManager(e.getMessage());
        }
        return false;
    }


    // This is a reusable lambda for mapping ResultSet → Loan
    Function<ResultSet, Loan> readToLoan = resultSet ->{
        Loan returnValue = null;
        try{
            returnValue = new Loan(
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
        logsMysql.writeLog("Loans: " + value);
        throw new RuntimeException("Database problem Loans DAO " + value);
    }
}
