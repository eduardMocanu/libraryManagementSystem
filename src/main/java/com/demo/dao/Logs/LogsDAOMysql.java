package com.demo.dao.Logs;

import com.demo.dbConnection.MysqlDbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class LogsDAOMysql implements LogsDAO{

    private final Connection conn;

    public LogsDAOMysql(){
        this.conn = MysqlDbConnection.getInstance().getConnection();
    }

    @Override
    public void writeLog(String prompt) {
        String sqlQuery = "INSERT INTO Logs (Data) VALUES (?);";
        try(PreparedStatement preparedStatement = conn.prepareStatement(sqlQuery)){
            preparedStatement.setString(1, prompt);
            int rowsAffected = preparedStatement.executeUpdate();
        }catch (SQLException e){
            System.out.println("Logs: " + e.getMessage());
            //throw new RuntimeException("Database problem Logs DAO");
        }
    }
}
