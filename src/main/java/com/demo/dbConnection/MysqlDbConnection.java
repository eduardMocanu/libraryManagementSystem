package com.demo.dbConnection;

import java.sql.*;

import com.mysql.cj.jdbc.MysqlDataSource;
import io.github.cdimascio.dotenv.*;

public class MysqlDbConnection implements DbConnection{
    private static final MysqlDbConnection instance = new MysqlDbConnection();
    private static final Dotenv dotenv = Dotenv.load();
    private static final String user = dotenv.get("MYSQL_USER");
    private static final String url = dotenv.get("MYSQL_URL");
    private static final String password = dotenv.get("MYSQL_PASSWORD");
    private static Connection connection;

    @Override
    public Connection getConnection(){
        if (connection == null){
            try{
                MysqlDataSource mysql = new MysqlDataSource();
                mysql.setUrl(url);
                mysql.setUser(user);
                mysql.setPassword(password);
                connection = mysql.getConnection();
            }
            catch(SQLException e){
                System.out.println(e.getMessage());
            }
        }
        return connection;
    }
    private MysqlDbConnection() {}

    public static MysqlDbConnection getInstance(){
        return instance;
    }

}
