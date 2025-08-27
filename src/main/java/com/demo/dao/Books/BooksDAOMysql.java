package com.demo.dao.Books;

import com.demo.dao.Logs.LogsDAOMysql;
import com.demo.dbConnection.MysqlDbConnection;
import com.demo.model.Book;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class BooksDAOMysql implements BooksDAO{
    private final Connection conn;
    private final LogsDAOMysql logsMysql;

    public BooksDAOMysql(){
        this.conn = MysqlDbConnection.getInstance().getConnection();
        this.logsMysql = new LogsDAOMysql();
    }

    @Override
    public boolean addBook(Book book) {
        String sqlQuery = "INSERT INTO Books (ISBN, Name, Author, NumberPages) VALUES (?, ?, ?, ?)";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setString(1, book.getISBN());
            preparedStatement.setString(2, book.getName());
            preparedStatement.setString(3, book.getAuthor());
            preparedStatement.setInt(4, book.getNumberPages());
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " row(s) affected");
            return true;
        } catch (SQLException e) {
            if(e.getErrorCode() == 1062){
                System.out.println("Attempted to insert an ISBN that already exists");
            }
            errorManager(e.getMessage());
            return false;
        }
    }

    @Override
    public String getNameByISBN(String ISBN) {
        String sqlQuery = "SELECT Name FROM Books WHERE ISBN = ?";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setString(1, ISBN);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return rs.getString("Name");
            }
            else{
                System.out.println("Did not find a book with that ISBN");
            }
        }catch(SQLException e){
            errorManager(e.getMessage());
        }
        return null;
    }

    @Override
    public void removeBookByISBN(String bookISBN) {
        String sqlQuery = "DELETE FROM Books WHERE ISBN = ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setString(1, bookISBN);
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected==0){
                System.out.println("The ISBN is not in the database");
            }
            else{
                System.out.println(bookISBN + " ISBN removed successfully");
            }
        }catch(SQLException e){
            errorManager(e.getMessage());
        }
    }

    @Override
    public String getBookISBNByNameAndAuthor(String bookName, String author) {
        String sqlQuery = "SELECT ISBN FROM Books WHERE Name = ? AND Author = ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setString(1, bookName);
            preparedStatement.setString(2, author);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return rs.getString("ISBN");
            }else{
                System.out.println("Book not found");
            }
        }catch(SQLException e){
            errorManager(e.getMessage());
        }
        return null;
    }

    @Override
    public HashMap<String, ArrayList<String>> getAvailableBooksStructuredByAuthor() {
        String sqlQuery = "SELECT Author, Name FROM Books WHERE StatusLoaned=0;";
        HashMap<String, ArrayList<String>> availableBooksByAuthors = new HashMap<>();
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            ResultSet rs = preparedStatement.executeQuery();
            while(rs.next()){
                if(availableBooksByAuthors.containsKey(rs.getString("Author"))){
                    availableBooksByAuthors.get(rs.getString("Author")).add(rs.getString("Name"));
                }
                else{
                    availableBooksByAuthors.put(rs.getString("Author"), new ArrayList<>());
                    availableBooksByAuthors.get(rs.getString("Author")).add(rs.getString("Name"));
                }
            }
        }catch(SQLException e){
            errorManager(e.getMessage());
        }
        return availableBooksByAuthors;
    }

    @Override
    public boolean loanBook(String bookISBN) {
        String sqlQuery = "UPDATE Books SET statusLoaned = 1 WHERE bookISBN = ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setString(1, bookISBN);
            int affectedRows = preparedStatement.executeUpdate();
            if(affectedRows == 1){
                return true;
            }
            return false;
        }catch (SQLException e){
            errorManager(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean giveBookBack(String booksISBN) {
        String sqlQuery = "UPDATE Books SET StatusLoaned = 0 WHERE BookISBN = ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setString(1, booksISBN);
            int rowsAffected = preparedStatement.executeUpdate();
            if(rowsAffected == 1){
                return true;
            }
            return false;
        }catch (SQLException e){
            errorManager(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean getStatusLoanedBook(String bookISBN) {
        return false;
    }


    private void errorManager(String value){
        System.out.println(value + " error occurred");
        logsMysql.writeLog("Books: " + value);
        throw new RuntimeException("Database problem Books DAO " + value);
    }
}
