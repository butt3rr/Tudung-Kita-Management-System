/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXML.java to edit this template
 */
package swc4243;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Main class for the application.
 */
public class Swc4243 extends Application {
    
    // Variables to store the offset of mouse cursor during window dragging
    private double xOffset = 0;
    private double yOffset = 0;
    
    @Override
    public void start(Stage stage) throws Exception {
        // Load the FXML document
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));
        
        // Create a scene
        Scene scene = new Scene(root);
        
        // Set stage style to decorated
        stage.initStyle(StageStyle.DECORATED);
        
        root.setOnMousePressed(new EventHandler<MouseEvent>(){
            @Override
            public void handle (MouseEvent event){
                // Record the initial position of the mouse cursor
                xOffset = event.getSceneX();
                yOffset = event.getSceneY();
            }
        });
        
        // Handle mouse dragged event for dragging the window
        root.setOnMouseDragged(new EventHandler<MouseEvent>(){
            @Override
            public void handle (MouseEvent event){
                // Calculate the new position of the window based on the mouse movement
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });
        
        // Set the scene and display the stage
        stage.setScene(scene);
        stage.show();
    }

    
     //Main method

    public static void main(String[] args) {
        // Launch the JavaFX application
        launch(args);
    }
    
}

