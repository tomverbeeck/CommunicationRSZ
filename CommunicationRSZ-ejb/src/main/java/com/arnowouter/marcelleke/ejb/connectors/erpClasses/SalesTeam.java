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
public class SalesTeam extends ERPObject{
    private int teamLeaderID;
    private String teamLeaderName;

    public SalesTeam(String name) {
        this.name = name;
    }

    
    public SalesTeam(int ID, String name, int teamLeaderID, String teamLeaderName) {
        this.ID = ID;
        this.name = name;
        this.teamLeaderID = teamLeaderID;
        this.teamLeaderName = teamLeaderName;
    }

    public int getTeamLeaderID() {
        return teamLeaderID;
    }

    public void setTeamLeaderID(int teamLeaderID) {
        this.teamLeaderID = teamLeaderID;
    }

    public String getTeamLeaderName() {
        return teamLeaderName;
    }

    public void setTeamLeaderName(String teamLeaderName) {
        this.teamLeaderName = teamLeaderName;
    }

    @Override
    public String toString() {
        return "SalesTeam{" + "ID=" + ID + ", name=" + name + ", teamLeaderID=" + teamLeaderID + ", teamLeaderName=" + teamLeaderName + '}';
    }
    
    
}
