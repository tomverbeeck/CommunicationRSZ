/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arnowouter.marcelleke.ejb.connectors.erpClasses;

/**
 *
 * @author Arno
 */
public class Task extends ERPObject {
    private int saleOrderID;
    private int projectID;
    private String description;
    private Employee employeeThisTaskIsAssignedTo;
    private Location location;
    private String state;
    
    public Task() {
    }

    public Task(int ID, String name, int saleOrderID, int projectID) {
        this.ID = ID;
        this.name = name;
        this.saleOrderID = saleOrderID;
        this.projectID = projectID;
    }

    public int getSaleOrderID() {
        return saleOrderID;
    }

    public void setSaleOrderID(int saleOrderID) {
        this.saleOrderID = saleOrderID;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Employee getEmployee() {
        return employeeThisTaskIsAssignedTo;
    }

    public void setEmployee(Employee employeeThisTaskIsAssignedTo) {
        this.employeeThisTaskIsAssignedTo = employeeThisTaskIsAssignedTo;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Task{" + "State=" + state + ", ID="+ ID + ", name=" + name + ", saleOrder=" + saleOrderID + ", projectID=" + projectID + '}';
    }
    
}
