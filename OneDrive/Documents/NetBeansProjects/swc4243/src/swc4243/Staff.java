/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package swc4243;

public class Staff {
    
    private String staffID;
    private String name;
    private String icNumber;
    private String phoneNumber;
    private String address;
    private String dateReg;

    public Staff(String staffID, String name, String icNumber, String phoneNumber, String address, String dateReg) {
        this.staffID = staffID;
        this.name = name;
        this.icNumber = icNumber;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.dateReg = dateReg;
    }

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcNumber() {
        return icNumber;
    }

    public void setIcNumber(String icNumber) {
        this.icNumber = icNumber;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateReg() {
        return dateReg;
    }

    public void setDateReg(String dateReg) {
        this.dateReg = dateReg;
    }
    
    
    
    
    
    
    
}
