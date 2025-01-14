/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package swc4243;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginModel {
    
    
     //Check if the login credentials are valid.
 
    public boolean isLoginValid(String staffID, String password) {
        // SQL query to check login 
        String query = "SELECT * FROM users WHERE staffId = ? AND password = ?";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            // Set parameters in the prepared statement
            pstmt.setString(1, staffID);
            pstmt.setString(2, password);
            
            // Execute query
            ResultSet rs = pstmt.executeQuery();

            // If a row is returned, login is successful
            if (rs.next()) {
                return true;  // Login successful
            }
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
        }
        return false;  // Login failed
    } 
}

