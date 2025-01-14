/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package swc4243;


public class CartItem {
    private int scarfID;
    private String scarfName;
    private int quantity;
    private double price;

    public CartItem(int scarfID, String scarfName, int quantity, double price) {
        this.scarfID = scarfID;
        this.scarfName = scarfName;
        this.quantity = quantity;
        this.price = price;
    }

    public int getScarfID() {
        return scarfID;
    }

    public String getScarfName() {
        return scarfName;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }
}

