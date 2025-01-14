/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package swc4243;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

public class AvailableScarfController implements Initializable {

    // FXML elements
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
    private Button btnUpload;

    @FXML
    private TableColumn<Scarf, String> colPrice;

    @FXML
    private TableColumn<Scarf, Integer> colScarfID;

    @FXML
    private TableColumn<Scarf, String> colScarfName;

    @FXML
    private TableColumn<Scarf, String> colStatus;

    @FXML
    private ImageView iv;

    @FXML
    private TextField price;

    @FXML
    private TextField scarfID;

    @FXML
    private TextField scarfName;

    @FXML
    private TextField search;

    @FXML
    private ChoiceBox<String> status;

    @FXML
    private TableView<Scarf> tvScarf;

    // Image file and default upload directory
    private File selectedFile;
    private final String defaultUploadDirectory = "C:\\Users\\ASUS\\Documents\\NetBeansProjects\\swc4243\\src\\image";

    // Initialize method
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Populate status ChoiceBox
        ObservableList<String> statusOptions = FXCollections.observableArrayList("Available", "Unavailable");
        status.setItems(statusOptions);

        // Add listener to ChoiceBox selection property
        status.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Update the selected item's status when changed
            Scarf selectedScarf = tvScarf.getSelectionModel().getSelectedItem();
            if (selectedScarf != null) {
                selectedScarf.setStatus(newValue);
                // Also update the status displayed in the text field
                status.setValue(newValue); // This ensures the ChoiceBox displays the updated status
            }
        });

        // Load scarf records into table view
        showScarf();
    }

    // Handle image upload button action
    @FXML
    void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image File");
        selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                Image image = new Image(new FileInputStream(selectedFile));
                iv.setImage(image);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    // Handle button actions
    @FXML
    void handleButtonAction(ActionEvent event) throws SQLException, FileNotFoundException {
        if (event.getSource() == btnAdd) {
            addRecord();
        } else if (event.getSource() == btnUpdate) {
            updateRecord();
        } else if (event.getSource() == btnDelete) {
            deleteRecord();
        } else if (event.getSource() == btnReset) {
            resetRecord();
        } else if (event.getSource() == btnSearch) {
            String searchTerm = search.getText();
            searchRecord(searchTerm);
        }

    }

    // Handle mouse click on table view
    @FXML
    void handleMouseAction(MouseEvent event) throws FileNotFoundException {
        Scarf scarf = tvScarf.getSelectionModel().getSelectedItem();
        if (scarf != null) {
            scarfID.setText(String.valueOf(scarf.getScarfID()));
            scarfName.setText(scarf.getScarfName());
            status.setValue(scarf.getStatus());
            price.setText(scarf.getPrice());

            // Display image
            String imagePath = scarf.getImage();
            File imageFile = new File(imagePath);
            if (imageFile.exists()) {
                Image image = new Image(new FileInputStream(imageFile));
                iv.setImage(image);
            } else {
                // Handle case where image file is not found
                System.out.println("Image file not found for scarf ID: " + scarf.getScarfID());
            }
        }
    }

    // Execute a SQL query that does not return a result set
    private void executeQuery(String query) {
        try (Connection conn = getConnection();
                Statement st = conn.createStatement()) {
            st.executeUpdate(query);
        } catch (SQLException ex) {
            System.out.println("Error in executing the query: " + ex.getMessage());
        }
    }

    // Add a record to the database
    private void addRecord() throws SQLException, FileNotFoundException {
        String query = "INSERT INTO scarf (scarfName, status, price, image) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
                PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, scarfName.getText());
            statement.setString(2, status.getValue());
            statement.setString(3, price.getText());

            // Save image to the specified directory
            String imagePath = formatImagePath(selectedFile.getPath());
            Files.copy(selectedFile.toPath(), Paths.get(imagePath), StandardCopyOption.REPLACE_EXISTING);

            // Set the image path in the database
            statement.setString(4, imagePath);

            statement.executeUpdate();
        } catch (SQLException ex) {
            System.out.println("Error adding record to database: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("Error saving image file: " + ex.getMessage());
        }
        showScarf();
    }

    // Update a record in the database
    private void updateRecord() {
        String selectedStatus = status.getValue();
        String query = "UPDATE scarf SET scarfName = ?, status = ?, price = ?, image = ? WHERE scarfID = ?";
        try (Connection conn = getConnection();
                PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, scarfName.getText());
            statement.setString(2, selectedStatus);
            statement.setString(3, price.getText());

            // Read image file
            FileInputStream inputStream = new FileInputStream(selectedFile);
            statement.setBinaryStream(4, inputStream, (int) selectedFile.length());

            statement.setString(5, scarfID.getText());

            statement.executeUpdate();
        } catch (SQLException | FileNotFoundException ex) {
            System.out.println("Error updating record in database: " + ex.getMessage());
        }
        showScarf();
    }

    // Delete a record from the database
    private void deleteRecord() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete Confirmation");
        alert.setHeaderText("Are you sure you want to delete this record?");
        alert.setContentText("This action cannot be undone.");

        // Show the confirmation dialog
        alert.showAndWait().ifPresent(result -> {
            if (result == ButtonType.OK) {
                String query = "DELETE FROM scarf WHERE scarfID = " + scarfID.getText();
                executeQuery(query);
                showScarf();
            }
        });
    }

    // Reset form fields
    private void resetRecord() {
        scarfID.setText("");
        scarfName.setText("");
        status.setValue(null);
        price.setText("");
        iv.setImage(null);
    }

    // Search for records in the database
    private void searchRecord(String searchTerm) {
        if (searchTerm.isEmpty()) {
            showScarf();
            return;
        }
        try (Connection conn = getConnection();
                PreparedStatement statement = conn.prepareStatement("SELECT * FROM scarf WHERE scarfName LIKE ?")) {
            statement.setString(1, "%" + searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();
            ObservableList<Scarf> searchResults = FXCollections.observableArrayList();
            while (resultSet.next()) {
                int scarfID = resultSet.getInt("scarfID");
                String scarfName = resultSet.getString("scarfName");
                String status = resultSet.getString("status");
                String price = resultSet.getString("price");
                String image = resultSet.getString("image");
                searchResults.add(new Scarf(scarfID, scarfName, status, price, image));
            }
            tvScarf.setItems(searchResults);
        } catch (SQLException ex) {
            System.out.println("Error executing search query: " + ex.getMessage());
        }
    }

    // Establish a database connection
    public Connection getConnection() {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost:3306/tudung_kita", "root", "");
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
            return null;
        }
    }

    // Format image path
    private String formatImagePath(String imagePath) {
        String fileName = selectedFile.getName();
        return defaultUploadDirectory + File.separator + fileName;
    }

    // Fetch scarf records from the database and populate the table view
    public void showScarf() {
        ObservableList<Scarf> scarfList = getScarfList();
        colScarfID.setCellValueFactory(new PropertyValueFactory<>("scarfID"));
        colScarfName.setCellValueFactory(new PropertyValueFactory<>("scarfName"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        tvScarf.setItems(scarfList);
    }

    // Fetch scarf records from the database
    public ObservableList<Scarf> getScarfList() {
        ObservableList<Scarf> scarfList = FXCollections.observableArrayList();
        String query = "SELECT * FROM scarf";
        try (Connection conn = getConnection();
                Statement st = conn.createStatement();
                ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                int scarfID = rs.getInt("scarfID");
                String scarfName = rs.getString("scarfName");
                String status = rs.getString("status");
                String price = rs.getString("price");
                String image = rs.getString("image");
                scarfList.add(new Scarf(scarfID, scarfName, status, price, image));
            }
        } catch (SQLException ex) {
            System.out.println("Error: " + ex.getMessage());
        }
        return scarfList;
    }

    // Get image data from the database
    private byte[] getImageDataFromDatabase(String imagePath) {
        try (Connection conn = getConnection();
                PreparedStatement statement = conn.prepareStatement("SELECT image FROM scarf WHERE image = ?")) {
            statement.setString(1, imagePath);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBytes("image");
            }
        } catch (SQLException ex) {
            System.out.println("Error retrieving image data from database: " + ex.getMessage());
        }
        return null;
    }

}
