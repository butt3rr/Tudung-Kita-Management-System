/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package swc4243;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Utility class for managing database connections.
 */
public class DBUtil {
    
    // JDBC driver class name
    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    
    // Database URL
    private static final String DB_URL = "jdbc:mysql://localhost:3306/tudung_kita";
    
    // Database credentials
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    /**
     * Get a connection to the database.
     * 
     * return Connection object represent the database connection
     * throws ClassNotFoundException if the JDBC driver class not found
     * throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        // Register JDBC driver
        Class.forName(JDBC_DRIVER);
        // Establish database connection
        return DriverManager.getConnection(DB_URL, USERNAME, PASSWORD);
    }
    
}

