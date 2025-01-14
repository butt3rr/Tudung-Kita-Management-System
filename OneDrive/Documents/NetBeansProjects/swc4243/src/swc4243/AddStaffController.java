/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package swc4243;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

// Controller class for the AddStaff view
public class AddStaffController implements Initializable {

    // FXML elements injected from the corresponding FXML file
    @FXML
    private TextField address;

    @FXML
    private DatePicker dr;

    @FXML
    private Button btnAdd;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnReset;

    @FXML
    private Button btnSearch;

    @FXML
    private Button btnUpdate;

    @FXML
    private TableColumn<Staff, String> colAddress;

    @FXML
    private TableColumn<Staff, String> colDateReg;

    @FXML
    private TableColumn<Staff, String> colICNumber;

    @FXML
    private TableColumn<Staff, String> colPhoneNum;

    @FXML
    private TableColumn<Staff, String> colStaffID;

    @FXML
    private TableColumn<Staff, String> colStaffName;

    @FXML
    private TextField icNum;

    @FXML
    private TextField phoneNum;

    @FXML
    private TextField search;

    @FXML
    private TextField staffID;

    @FXML
    private TextField staffName;

    @FXML
    private TableView<Staff> tvStaff;

    // Handles button actions
    @FXML
    void handleButtonAction(ActionEvent event) {
        if (event.getSource() == btnAdd) {
            addRecord();
        } else if (event.getSource() == btnUpdate) {
            updateRecord();
        } else if (event.getSource() == btnDelete) {
            deleteRecord();
        } else if (event.getSource() == btnReset) {
            resetRecord();
        } else if (event.getSource() == btnSearch){
            String searchTerm = search.getText();
            searchRecord(searchTerm);
        } 
    }

    // Handles mouse actions
    @FXML
    void handleMouseAction(MouseEvent event) {
        Staff staff = tvStaff.getSelectionModel().getSelectedItem();
        staffID.setText(staff.getStaffID());
        staffName.setText(staff.getName());
        icNum.setText(staff.getIcNumber());
        phoneNum.setText(staff.getPhoneNumber());     
        address.setText(staff.getAddress());
        dr.setValue(LocalDate.parse(staff.getDateReg(), DateTimeFormatter.ofPattern("dd-MM-yyyy")));   
    }

    // Initializes the controller class.
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        showStaff();
    }    

    // Executes SQL queries
    private void executeQuery(String query) {
        try (Connection conn = getConnection();
             Statement st = conn.createStatement()) {
            st.executeUpdate(query);
        } catch (SQLException ex) {
            System.out.println("Error in executing the query: " + ex.getMessage());
        }
    }
    
    // Adds a record to the database
    @FXML
    private void addRecord() {
        // Check if all fields are filled
        if (staffID.getText().isEmpty() || staffName.getText().isEmpty() || icNum.getText().isEmpty() ||
                phoneNum.getText().isEmpty() || address.getText().isEmpty() || dr.getValue() == null) {
            // Show alert if any field is empty
            showAlert("Error", "Please fill in all fields", AlertType.ERROR);
            return;
        }

        // Check if staff ID, IC number, and phone number already exist
        if (staffExists(staffID.getText()) || icNumberExists(icNum.getText()) || phoneNumberExists(phoneNum.getText())) {
            // Show alert if any of the fields already exist
            showAlert("Error", "Staff ID, IC number, or phone number already exists!", AlertType.ERROR);
            return; // Exit the method
        }

        // Proceed with insertion if all fields are unique
        String formattedDate = dr.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String query = "INSERT INTO users (staffID, name, icNumber, phoneNumber, address, dateReg, password) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, staffID.getText());
            statement.setString(2, staffName.getText());
            statement.setString(3, icNum.getText());
            statement.setString(4, phoneNum.getText());
            statement.setString(5, address.getText());
            statement.setString(6, formattedDate);
            // Generate password from last four digits of IC number
            String password = icNum.getText().substring(icNum.getText().length() - 6);
            statement.setString(7, password);
            statement.executeUpdate();
            showAlert("Success", "Record added successfully", AlertType.INFORMATION);
            showStaff(); // Refresh the table view after adding the record
        } catch (SQLException ex) {
            showAlert("Error", "Error adding record to database: " + ex.getMessage(), AlertType.ERROR);
        }
    }

    // Updates a record in the database
    private void updateRecord() {
        // Check if all fields are filled
        if (staffID.getText().isEmpty() || staffName.getText().isEmpty() || icNum.getText().isEmpty() ||
                phoneNum.getText().isEmpty() || address.getText().isEmpty() || dr.getValue() == null) {
            // Show alert if any field is empty
            showAlert("Error", "Please fill in all fields", AlertType.ERROR);
            return;
        }

        // Check if staff ID, IC number, and phone number already exist
        if (staffExists(staffID.getText()) || icNumberExists(icNum.getText()) || phoneNumberExists(phoneNum.getText())) {
            // Show alert if any of the fields already exist
            showAlert("Error", "Staff ID, IC number, or phone number already exists!", AlertType.ERROR);
            return; // Exit the method
        }
        
        String formattedDate = dr.getValue().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String query = "UPDATE users SET name = '" + staffName.getText() + "', dateReg = '" + formattedDate + "', phoneNumber = '" + phoneNum.getText() + "', address = '" + address.getText() + "', icNumber = '" + icNum.getText() + "'  WHERE staffID = '" + staffID.getText() + "'";
        executeQuery(query);
        showStaff();
    }

    // Deletes a record from the database
    private void deleteRecord() {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this record?");
        alert.setContentText("This action cannot be undone.");
    
        // Show the confirmation dialog
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                // If user clicks OK, proceed with deletion
                String query = "DELETE FROM users WHERE staffID = '" + staffID.getText() + "'";
                executeQuery(query);
                showStaff();
            }
        });
    }

    // Resets the fields
    private void resetRecord() {
        staffID.setText("");
        staffName.setText("");
        icNum.setText("");
        phoneNum.setText("");
        address.setText("");
        dr.setValue(null);
    }

    // Searches for records in the database
    private void searchRecord(String searchTerm) {
        if (searchTerm.isEmpty()) {
            showStaff();
            return;
        }

        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement("SELECT * FROM users WHERE name LIKE ? OR address LIKE ? OR phoneNumber LIKE ?")) {
            statement.setString(1, "%" + searchTerm + "%");
            statement.setString(2, "%" + searchTerm + "%");
            statement.setString(3, "%" + searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();

            ObservableList<Staff> searchResults = FXCollections.observableArrayList();
            while (resultSet.next()) {
                String staffID = resultSet.getString("staffID");
                String name = resultSet.getString("name");
                String icNumber = resultSet.getString("icNumber");
                String phoneNumber = resultSet.getString("phoneNumber");
                String address = resultSet.getString("address");
                String dateReg = resultSet.getString("dateReg");
                searchResults.add(new Staff(staffID, name, icNumber, phoneNumber, address, dateReg));
            }
            tvStaff.setItems(searchResults);
        } catch (SQLException ex) {
            System.out.println("Error executing search query: " + ex.getMessage());
        }
    }

    // Establishes a database connection
    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/tudung_kita", "root", "");
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
            return null;
        }
    }

    // Retrieves a list of staff from the database
    public ObservableList<Staff> getStaffList() {
        ObservableList<Staff> staffList = FXCollections.observableArrayList();
        String query = "SELECT * FROM users";
        
        try (Connection conn = getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                Staff staff = new Staff(
                    rs.getString("staffID"),
                    rs.getString("name"),
                    rs.getString("icNumber"),
                    rs.getString("phoneNumber"),
                    rs.getString("address"),
                    rs.getString("dateReg")
                );
                staffList.add(staff);
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return staffList;
    }

    // Displays the staff data in the table view
    public void showStaff() {
        ObservableList<Staff> staffList = getStaffList();

        colStaffID.setCellValueFactory(new PropertyValueFactory<>("staffID"));
        colStaffName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colICNumber.setCellValueFactory(new PropertyValueFactory<>("icNumber"));
        colPhoneNum.setCellValueFactory(new PropertyValueFactory<>("phoneNumber"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colDateReg.setCellValueFactory(new PropertyValueFactory<>("dateReg"));

        tvStaff.setItems(staffList);
    }
    
    // Displays an alert dialog
    private void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Checks if a staff with the given ID exists
    private boolean staffExists(String staffID) {
        String query = "SELECT * FROM users WHERE staffID = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, staffID);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException ex) {
            showAlert("Error", "Error checking staff existence: " + ex.getMessage(), AlertType.ERROR);
            return false;
        }
    }

    // Checks if a staff with the given IC number exists
    private boolean icNumberExists(String icNumber) {
        String query = "SELECT * FROM users WHERE icNumber = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, icNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException ex) {
            showAlert("Error", "Error checking IC number existence: " + ex.getMessage(), AlertType.ERROR);
            return false;
        }
    }

    // Checks if a staff with the given phone number exists
    private boolean phoneNumberExists(String phoneNumber) {
        String query = "SELECT * FROM users WHERE phoneNumber = ?";
        try (Connection conn = getConnection();
             PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, phoneNumber);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException ex) {
            showAlert("Error", "Error checking phone number existence: " + ex.getMessage(), AlertType.ERROR);
            return false;
        }
    }
}
