/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML2.java to edit this template
 */
package swc4243;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controller class for the FXML document.
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private Button btn;

    @FXML
    private PasswordField pw;

    @FXML
    private TextField staffID;
    
     //Handle access button action
     
    @FXML
    private void access(ActionEvent event) throws IOException {
        // Create new instance of LoginModel
        LoginModel loginModel = new LoginModel();
        // Retrieve staff ID and password from text fields
        String staffId = staffID.getText();
        String password = pw.getText();

        // Check if login credentials are valid
        if (loginModel.isLoginValid(staffId, password)) {
            // Store the logged-in user's ID in the session
            SessionManager.setLoggedInUserID(staffId);

            try {
                // Load the home page after successful login
                Parent root = FXMLLoader.load(getClass().getResource("home.fxml"));
                Scene scene = new Scene(root);
                // Get the current window and set the scene
                Stage stage = (Stage) staffID.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Display an error message if login are incorrect
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Incorrect ID or Password!");
            alert.show();
        }
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
}

