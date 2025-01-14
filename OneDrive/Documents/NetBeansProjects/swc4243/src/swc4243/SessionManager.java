/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package swc4243;

public class SessionManager {
    
    // Variable to store the ID of the logged-in user
    private static String loggedInUserID;

    
     //Set the ID of the logged-in user.
     
    public static void setLoggedInUserID(String userID) {
        loggedInUserID = userID;
    }

    
     //Get the ID of the logged-in user.
    
    public static String getLoggedInUserID() {
        return loggedInUserID;
    }
}


