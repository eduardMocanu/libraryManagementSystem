package com.demo.dao.Clients;

import com.demo.dbConnection.MysqlDbConnection;
import com.demo.model.Client;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientsDAOMysql implements ClientsDAO{

    private final Connection conn;

    public ClientsDAOMysql(){
        this.conn = MysqlDbConnection.getInstance().getConnection();
    }
    @Override
    public void addClient(Client client){
        String sqlQuery = "INSERT INTO Clients (Name, Email) VALUES (?, ?);";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getEmail());
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");
        }catch(SQLException e){
            errorManager(e.getMessage());
        }
    }
    @Override
    public String getNameById(Integer clientId){
        String sqlQuery = "SELECT Name FROM Clients WHERE ID=?;";
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
    public void removeClientById(Integer clientId){
        String sqlQuery = "DELETE FROM Clients WHERE id = ?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setInt(1, clientId);
            int rowsAffected = preparedStatement.executeUpdate();
            System.out.println(rowsAffected + " rows affected");
        }catch (SQLException e){
            errorManager(e.getMessage());
        }
    }
    @Override
    public Client getClientObjById(Integer clientId){
        String sqlQuery = "SELECT * FROM Clients WHERE ID=?;";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setInt(1, clientId);
            ResultSet rs = preparedStatement.executeQuery();
            if(rs.next()){
                return new Client(rs.getInt("ID"), rs.getString("Name"), rs.getString("Email"));
            }
        }catch(SQLException e){
            errorManager(e.getMessage());
        }
        return null;
    }

    private void errorManager(String value){
        System.out.println(value + " error occurred");
    }
}
