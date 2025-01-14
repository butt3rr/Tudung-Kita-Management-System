/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package swc4243;

public class Scarf {
    
    private int scarfID;
    private String scarfName;
    private String status;
    private String price;
    private String image;
    
     public Scarf(int scarfID, String scarfName, String status, String price, String image) {
        this.scarfID = scarfID;
        this.scarfName = scarfName;
        this.status = status;
        this.price = price;
        this.image = image;
    }

    public void setScarfID(int scarfID) {
        this.scarfID = scarfID;
    }

    public void setScarfName(String scarfName) {
        this.scarfName = scarfName;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getScarfID() {
        return scarfID;
    }

    public String getScarfName() {
        return scarfName;
    }

    public String getStatus() {
        return status;
    }

    public String getPrice() {
        return price;
    }

    public String getImage() {
        return image;
    }

   
    
    
    
    
}
