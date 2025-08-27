package com.demo.dao.Clients;

import com.demo.dao.Logs.LogsDAOMysql;
import com.demo.dbConnection.MysqlDbConnection;
import com.demo.model.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientsDAOMysql implements ClientsDAO{

    private final Connection conn;
    private final LogsDAOMysql logsMysql;

    public ClientsDAOMysql(){
        this.conn = MysqlDbConnection.getInstance().getConnection();
        this.logsMysql = new LogsDAOMysql();
    }
    @Override
    public Integer addClient(Client client){
        String sqlQuery = "INSERT INTO Clients (Name, Email) VALUES (?, ?);";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getEmail());
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");
            Integer id = null;
            if(rowsAffected > 0){
                try(ResultSet rs = preparedStatement.getGeneratedKeys()){
                    if(rs.next()){
                        id = rs.getInt(1);
                    }
                }
            }
            return id;
        }catch(SQLException e){
            if(e.getErrorCode() == 1062){
                System.out.println("Attempted to insert an email that already exists");
            }
            errorManager(e.getMessage());
            return null;
        }
    }
    @Override
    public String getNameById(Integer clientId){
        String sqlQuery = "SELECT Name FROM Clients WHERE Id=?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setInt(1, clientId);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return rs.getString("Name");
            }
        }catch(SQLException e){
            errorManager(e.getMessage());
        }
        return null;
    }
    @Override
    public boolean removeClientById(Integer clientId){
        String sqlQuery = "DELETE FROM Clients WHERE id = ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setInt(1, clientId);
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");
            return true;
        }catch (SQLException e){
            errorManager(e.getMessage());
            return false;
        }
    }
    @Override
    public Client getClientObjById(Integer clientId){
        String sqlQuery = "SELECT * FROM Clients WHERE Id=?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setInt(1, clientId);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return new Client(rs.getString("Name"), rs.getString("Email"));
            }
        }catch(SQLException e){
            errorManager(e.getMessage());
        }
        return null;
    }



    private void errorManager(String value){
        System.out.println(value + " error occurred");
        logsMysql.writeLog("Clients: " + value);
        throw new RuntimeException("Database problem Clients DAO " + value);
    }
}
