/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package swc4243;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Controller class for the home view.
 */
public class HomeController implements Initializable {

    @FXML
    private AnchorPane ap;

    @FXML
    private BorderPane bp;

    @FXML
    private Button addStaffButton;

    @FXML
    private Label home_availableScarf;

    @FXML
    private Label home_totalSales;

    @FXML
    private Label home_totalCustomers;

    @FXML
    private BarChart<String, Number> home_chart;

    @FXML
    private Button logoutButton;

    @FXML
    private Label lastUpdateLabel;

    private Connection connect;
    private PreparedStatement prepare;
    private Statement statement;
    private ResultSet result;

     //Handle logout button action.
    @FXML
    void handleLogOut(ActionEvent event) throws IOException {
        // Create a confirmation alert
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Log Out Confirmation");
        alert.setHeaderText("Logging Out");
        alert.setContentText("Are you sure you want to log out?");

        // Show the alert and wait for user response
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // User confirmed, proceed with log out
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLDocument.fxml"));
            Parent root = loader.load();

            // Create the scene
            Scene scene = new Scene(root);

            // Get the stage information from the event to switch scenes
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(scene);
            stage.show();
        } else {
            // User canceled the action, nothing happens
        }
    }

     //Handle click event on 'Add Staff' button.
    @FXML
    void addStaff(MouseEvent event) {
        loadPage("addStaff");
    }

     //Handle click event on 'Purchase' button.
    @FXML
    void catalog(MouseEvent event) {
        loadPage("catalog");
    }

     //Handle click event on 'Dashboard' button.
     
    @FXML
    void dashboard(MouseEvent event) {
        bp.setCenter(ap);
    }

  
     //Handle click event on 'Scarf List' button.
    @FXML
    void inventory(MouseEvent event) {
        loadPage("availableScarf");
    }

    
     //Initialize the controller class.
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        boolean isStaffSV = isStaffSV();
        addStaffButton.setVisible(isStaffSV);
        loadHomeData();
        loadChartData();
    }

    //Refresh the graph on button click.
    
    @FXML
    void refreshGraph(ActionEvent event) {
        loadChartData();
        loadHomeData();
        updateLastUpdateLabel();
    }

    //Update the last update label with current date and time.
     
    private void updateLastUpdateLabel() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        lastUpdateLabel.setText("Last update: " + formattedDateTime);
    }

    
    // Check if the logged-in user is a staff member.
    private boolean isStaffSV() {
        String loggedInUserID = SessionManager.getLoggedInUserID();
        return loggedInUserID != null && loggedInUserID.toLowerCase().contains("sv");
    }

     //Load data for the home view
    private void loadHomeData() {
        try {
            homeAS();
            homeTI();
            homeTC();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //Load chart data for the home view.
    private void loadChartData() {
        homeChart();
    }

    
     //Load available scarf count.
    public void homeAS() throws SQLException {
        String sql = "SELECT COUNT(scarfID) AS countAS FROM scarf WHERE status = 'Available'";

        try {
            Connection connect = DBUtil.getConnection();
            Statement statement = connect.createStatement();
            ResultSet result = statement.executeQuery(sql);

            int countAS = 0;
            if (result.next()) {
                countAS = result.getInt("countAS");
            }
            home_availableScarf.setText(String.valueOf(countAS));

            result.close();
            statement.close();
            connect.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    
     //Load total income
    public void homeTI() throws SQLException {
        String sql = "SELECT SUM(totalPurchase) AS totalIncome FROM customer WHERE DATE(datePurchase) = CURDATE()";

        try {
            Connection connect = DBUtil.getConnection();
            Statement statement = connect.createStatement();
            ResultSet result = statement.executeQuery(sql);

            double totalIncome = 0;
            if (result.next()) {
                totalIncome = result.getDouble("totalIncome");
            }
            String formattedTotalIncome = String.format("RM %.2f", totalIncome);
            home_totalSales.setText(formattedTotalIncome);

            result.close();
            statement.close();
            connect.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

     //Load total customers.
    public void homeTC() throws SQLException {
        String sql = "SELECT COUNT(*) AS totalCustomers FROM customer WHERE DATE(datePurchase) = CURDATE()";

        try {
            Connection connect = DBUtil.getConnection();
            Statement statement = connect.createStatement();
            ResultSet result = statement.executeQuery(sql);

            int totalCustomers = 0;
            if (result.next()) {
                totalCustomers = result.getInt("totalCustomers");
            }
            home_totalCustomers.setText(String.valueOf(totalCustomers));

            result.close();
            statement.close();
            connect.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

     //Load chart data
     
    public void homeChart() {
        home_chart.getData().clear();

        String sql = "SELECT CONCAT(DATE_FORMAT(datePurchase, '%Y-%m-%d '), " +
                     "LPAD(CEIL(HOUR(datePurchase)/3)*3, 2, '00'), ':00:00') AS timestamp_3hour, " +
                     "SUM(totalPurchase) AS total_sales " +
                     "FROM customer " +
                     "WHERE DATE(datePurchase) = CURDATE() " +
                     "AND HOUR(datePurchase) >= 8 " +
                     "GROUP BY LPAD(CEIL(HOUR(datePurchase)/3)*3, 2, '00') " +
                     "ORDER BY timestamp_3hour ASC";

        try (Connection connection = DBUtil.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            XYChart.Series<String, Number> series = new XYChart.Series<>();

            while (resultSet.next()) {
                String timestamp3Hour = resultSet.getString("timestamp_3hour");
                double totalSales = resultSet.getDouble("total_sales");
                series.getData().add(new XYChart.Data<>(timestamp3Hour, totalSales));
            }

            home_chart.getData().add(series);

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
   
     //Load the specified page into the border pane.
   
    private void loadPage(String page) {
        Parent root = null;
        
        try {
            root = FXMLLoader.load(getClass().getResource(page + ".fxml"));
        } catch (IOException ex) {
            Logger.getLogger(HomeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        bp.setCenter(root);
    }  
}

