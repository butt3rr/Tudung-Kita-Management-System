/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package swc4243; 

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.event.ActionEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;

// Controller class for the Catalog view
public class CatalogController implements Initializable {

    // FXML elements injected from the corresponding FXML file
    @FXML
    private ComboBox<Integer> scarfIDComboBox;

    @FXML
    private TextField scarfNameTextField;
    
    @FXML
    private Text textTotal;

    @FXML
    private Spinner<Integer> quantitySpinner;

    @FXML
    private TableView<CartItem> cartTableView;

    @FXML
    private TableColumn<CartItem, Integer> scarfIDColumn;

    @FXML
    private TableColumn<CartItem, String> scarfNameColumn;

    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;

    @FXML
    private TableColumn<CartItem, Double> priceColumn;

    // Database connection object
    private Connection connection;

    // Observable list to hold cart items
    private ObservableList<CartItem> cartItems = FXCollections.observableArrayList();

    // Initializes the controller class.
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize database connection
        initializeDatabaseConnection();
        
        // Populate scarf ID combo box
        populateScarfIDComboBox();
        
        // Add listener to scarf ID combo box
        addScarfIDComboBoxListener();
        
        // Initialize cart table view
        initializeCartTableView();
        
        // Initialize quantity spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0);
        quantitySpinner.setValueFactory(valueFactory);
    }

    // Establishes a connection to the MySQL database
    private void initializeDatabaseConnection() {
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/tudung_kita", "root", "");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Populates the scarf ID combo box with data from the database
    private void populateScarfIDComboBox() {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT scarfID FROM scarf");
            ObservableList<Integer> scarfIDs = FXCollections.observableArrayList();
            while (resultSet.next()) {
                scarfIDs.add(resultSet.getInt("scarfID"));
            }
            scarfIDComboBox.setItems(scarfIDs);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Adds a listener to the scarf ID combo box to update scarf name text field when a scarf ID is selected
    private void addScarfIDComboBoxListener() {
        scarfIDComboBox.setOnAction(event -> {
            Integer selectedScarfID = scarfIDComboBox.getValue();
            if (selectedScarfID != null) {
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement("SELECT scarfName FROM scarf WHERE scarfID = ?");
                    preparedStatement.setInt(1, selectedScarfID);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        String scarfName = resultSet.getString("scarfName");
                        scarfNameTextField.setText(scarfName);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // Initializes the cart table view
    private void initializeCartTableView() {
        // Set up column cell value factories
        scarfIDColumn.setCellValueFactory(new PropertyValueFactory<>("scarfID"));
        scarfNameColumn.setCellValueFactory(new PropertyValueFactory<>("scarfName"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        // Price column needs special handling due to double type
        priceColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<CartItem, Double>, ObservableValue<Double>>() {
            @Override
            public ObservableValue<Double> call(TableColumn.CellDataFeatures<CartItem, Double> param) {
                return new SimpleDoubleProperty(param.getValue().getPrice()).asObject();
            }
        });

        // Format the price column to display with two decimal places
        priceColumn.setCellFactory(tc -> new TableCell<CartItem, Double>() {
            @Override
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty || price == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", price));
                }
            }
        });

        // Set the items to be displayed in the table view
        cartTableView.setItems(cartItems);
    }

    // Handles the "Add to Cart" button click event
    @FXML
    private void handleAddToCartButton() {
        // Retrieve selected scarf ID, scarf name, and quantity
        Integer selectedScarfID = scarfIDComboBox.getValue();
        String scarfName = scarfNameTextField.getText();
        Integer quantity = quantitySpinner.getValue();
        
        // Ensure all necessary data is available
        if (selectedScarfID != null && scarfName != null && quantity != null) {
            try {
                // Query database to get price of selected scarf
                PreparedStatement preparedStatement = connection.prepareStatement("SELECT price FROM scarf WHERE scarfID = ?");
                preparedStatement.setInt(1, selectedScarfID);
                ResultSet resultSet = preparedStatement.executeQuery();
                
                // If scarf found, calculate total price and add to cart
                if (resultSet.next()) {
                    double price = resultSet.getDouble("price");
                    double totalPrice = price * quantity;
                    cartItems.add(new CartItem(selectedScarfID, scarfName, quantity, totalPrice));
                    
                    // Update total price in the cart
                    updateTotalPrice();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Updates the total price displayed in the cart
    private void updateTotalPrice() {
        double totalPrice = 0;
        for (CartItem item : cartItems) {
            totalPrice += item.getPrice();
        }
        String formattedPrice = String.format("%.2f", totalPrice);
        textTotal.setText("Total: RM " + formattedPrice);
    }

    // Handles the "Pay" button click event
    @FXML
    private void handlePayButton(ActionEvent event) {
        double totalPurchase = calculateTotalPurchase();
        String paymentMethod = ((Button) event.getSource()).getText().substring(7); // Extract payment method from button text

        if (totalPurchase > 0) {
            // Confirmation alert before processing the payment
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Payment");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Are you sure you want to make the payment?");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // User confirmed payment
                try {
                    // Insert payment details into database
                    PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO customer (totalPurchase, paymentMethod) VALUES (?, ?)");
                    preparedStatement.setDouble(1, totalPurchase);
                    preparedStatement.setString(2, paymentMethod);
                    preparedStatement.executeUpdate();
                    resetCart();
                    // Display a confirmation message to the user
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Payment Successful");
                    alert.setHeaderText(null);
                    alert.setContentText("Payment successful. Thank you for your purchase!");
                    alert.showAndWait();
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Display an error message to the user
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("An error occurred while processing payment. Please try again later.");
                    alert.showAndWait();
                }
            } else {
                // Payment was canceled by the user
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Payment Canceled");
                alert.setHeaderText(null);
                alert.setContentText("Payment has been canceled.");
                alert.showAndWait();
            }
        } else {
            // Display a message to the user indicating no items in the cart
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("There are no items in the cart to pay for.");
            alert.showAndWait();
        }
    }

    // Handles the "Void Item" button click event
    @FXML
    private void voidItem(ActionEvent event) {
        // Get the selected item from the table view
        CartItem selectedItem = cartTableView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            // Remove the selected item from the table view
            cartTableView.getItems().remove(selectedItem);
        } else {
            // If no item is selected, display a warning message to the user
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("No Item Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select an item to void.");
            alert.showAndWait();
        }
    }

    // Calculates the total purchase amount in the cart
    private double calculateTotalPurchase() {
        double totalPurchase = 0;
        for (CartItem item : cartItems) {
            totalPurchase += item.getPrice();
        }
        return totalPurchase;
    }

    // Resets the cart by clearing all items
    private void resetCart() {
        cartItems.clear();
    }
}

