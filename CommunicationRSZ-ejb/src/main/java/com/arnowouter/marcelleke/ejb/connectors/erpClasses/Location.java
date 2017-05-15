/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arnowouter.marcelleke.ejb.connectors.erpClasses;

import com.arnowouter.marcelleke.domain.entities.AddressEntity;
import com.arnowouter.marcelleke.ejb.connectors.odoo.OdooDefaults;
import java.io.Serializable;

/**
 *
 * @author Arno
 */
public class Location extends ERPObject implements Serializable {

    private AddressEntity address;
    private double latitude;
    private double longitude;
    private boolean toBeTracked;
    private int erpId;
    private String checkInAtWorkID;
    
    public static Object[] FIELDS = {
                OdooDefaults.FIELD_ID,
                OdooDefaults.FIELD_STREET,
                OdooDefaults.FIELD_CITY,
                OdooDefaults.FIELD_LATITUDE,
                OdooDefaults.FIELD_LONGITUDE,
                OdooDefaults.FIELD_ZIP,
                OdooDefaults.FIELD_COUNTRY
    };
    
    public Location() {
    }

    public Location(String name, int erpId, AddressEntity address, double latitude, double longitude, boolean toBeTracked) {
        this.name = name;
        this.ID = erpId;
        this.erpId = this.ID;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.toBeTracked = toBeTracked;
        this.checkInAtWorkID = "none";
    }

    public AddressEntity getAddress() {
        return address;
    }

    public void setAddress(AddressEntity address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getErpId() {
        return erpId;
    }

    public void setErpId(int erpId) {
        this.erpId = erpId;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isToBeTracked() {
        return toBeTracked;
    }

    public void setToBeTracked(boolean toBeTracked) {
        this.toBeTracked = toBeTracked;
    }   

    public String getCheckInAtWorkID() {
        return checkInAtWorkID;
    }

    public void setCheckInAtWorkID(String checkInAtWorkID) {
        this.checkInAtWorkID = checkInAtWorkID;
    }
 
    @Override
    public String toString() {
        return "Location{" + "name=" + name + ", erpId=" + ID + ", checkIn@Work ID=" + checkInAtWorkID + ", address=" + address + ", latitude=" + latitude + ", longitude=" + longitude + ", toBeTracked=" + toBeTracked + '}';
    }
    
}
