/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arnowouter.marcelleke.ejb.connectors.erpClasses;

import java.io.Serializable;

/**
 *
 * @author Arno
 */
public class Employee implements Serializable {
    private String name;
    private int erpID;
    private int userID;
    private String workEmail;
    private boolean isAManager;
    private String managerId;
    private boolean isAdmin;
    private String identificationID;
    
    public Employee(
            String name, 
            int erpID,
            int userIdOdoo,
            String workEmail, 
            boolean isAManager, 
            String managerId,
            String identificationID
    ) {
        this.name = name;
        this.erpID = erpID;
        this.userID = userIdOdoo;
        this.workEmail = workEmail;
        this.isAManager = isAManager;
        this.managerId = managerId;
        this.isAdmin = false;
        this.identificationID = identificationID;
    }

    public String getName() {return name;}
    public int getErpId() {return erpID;}
    public String getWorkEmail() {return workEmail;}
    public boolean isAManager() {return isAManager;}
    public String getManagerId() {return managerId;}
    public int getUserID() {return userID;}
    public boolean isAdmin() {return isAdmin;}
    public String getIdentificationID() {return identificationID;}

    public void setName(String name) {this.name = name;}
    public void setErpId(int erpId) {this.erpID = erpId;}
    public void setUserID(int userID) {this.userID = userID;}
    public void setWorkEmail(String workEmail) {this.workEmail = workEmail;}
    public void setIsAManager(boolean isAManager) {this.isAManager = isAManager;}
    public void setManagerId(String managerId) {this.managerId = managerId;}
    public void setIsAdmin(boolean isAdmin) {this.isAdmin = isAdmin;}
    public void setIdentificationID(String identificationID) {this.identificationID = identificationID;}
    
    @Override
    public String toString() {
        return "Employee{" 
                + "name=" + name 
                + ", erpId=" + erpID 
                + ", userIdOdoo=" + userID 
                + ", workEmail=" + workEmail 
                + ", isAManager=" + isAManager 
                + ", managerId=" + managerId 
                + ", identificationID=" + identificationID 
                + ", isAdmin=" + isAdmin 
        + '}';
    }
    
}
