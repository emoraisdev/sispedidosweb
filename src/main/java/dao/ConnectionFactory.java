/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author allbrax
 */
public class ConnectionFactory {
    public static Connection getConnection() throws SQLException{
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }

        String url = "jdbc:mysql://localhost:3306/pedidos";
        String user = "root";
        String password = "root";
        Properties properties = new Properties();
        properties.setProperty("user", user);
        properties.setProperty("password", password);
        properties.setProperty("allowPublicKeyRetrieval", "true");
        properties.setProperty("useSSL", "false");
        properties.setProperty("requireSSL", "false");

        return DriverManager.getConnection(url, properties);
    }
}
