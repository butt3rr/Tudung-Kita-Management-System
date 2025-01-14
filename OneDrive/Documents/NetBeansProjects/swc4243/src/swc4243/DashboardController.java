/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package swc4243;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;


public class DashboardController implements Initializable {
    @FXML
    private Label salesLabel;

    @FXML
    private Label scarfsLabel;

    @FXML
    private Label customersLabel;
    
    @FXML
    private Label lastUpdateLabel;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
    }    
   
     //Handle refresh button action to update dashboard values.
    
    @FXML
    void refreshDashboard(ActionEvent event) throws ClassNotFoundException {
        // Implement code to refresh the values displayed on the dashboard
        updateSales(); // Update sales value
        updateScarfs(); // Update scarfs value
        updateCustomers(); // Update customers value
        updateLastUpdateLabel(); // Update last update label
    }

    
    //Update the sales value displayed on the dashboard.
     
    private void updateSales() throws ClassNotFoundException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT SUM(totalPurchase) FROM customer");
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                double totalSales = resultSet.getDouble(1);
                salesLabel.setText("$" + String.valueOf(totalSales));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
     //Update the scarfs value displayed on the dashboard.
    
    private void updateScarfs() throws ClassNotFoundException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(scarfID) FROM scarf");
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int scarfsCount = resultSet.getInt(1);
                scarfsLabel.setText(String.valueOf(scarfsCount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    
     //Update the customers value displayed on the dashboard.
     
    private void updateCustomers() throws ClassNotFoundException {
        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(customerID) FROM customer");
             ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                int customersCount = resultSet.getInt(1);
                customersLabel.setText(String.valueOf(customersCount));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
     //Update the last update label with current date and time.
    
    private void updateLastUpdateLabel() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        lastUpdateLabel.setText("Last updated at " + formattedDateTime);
    }
}
