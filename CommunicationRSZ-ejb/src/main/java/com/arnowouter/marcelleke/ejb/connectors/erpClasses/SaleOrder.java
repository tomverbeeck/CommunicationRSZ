/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arnowouter.marcelleke.ejb.connectors.erpClasses;

import com.arnowouter.marcelleke.ejb.connectors.odoo.OdooDefaults;

/**
 *
 * @author Arno
 */
public class SaleOrder extends ERPObject{

    private String status;
    private String checkInAtWorkID;
    private int shippingID;
    
    static public Object[] FIELDS = {
            OdooDefaults.FIELD_ID,
            OdooDefaults.FIELD_PROJECT_ID,
            OdooDefaults.FIELD_STATUS,
            OdooDefaults.FIELD_SHIPPING_ID,
            OdooDefaults.FIELD_CHECK_IN_AT_WORK,
            OdooDefaults.FIELD_COUNTRY
    };
    
    public SaleOrder(int ID, String name, String status) {
        this.ID = ID;
        this.name = name;
        this.status = status;
    }

    public SaleOrder() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCheckInAtWorkNumber() {
        return checkInAtWorkID;
    }

    public void setCheckInAtWorkNumber(String checkInAtWorkNumber) {
        this.checkInAtWorkID = checkInAtWorkNumber;
    }

    public int getShippingID() {
        return shippingID;
    }

    public void setShippingID(int shippingID) {
        this.shippingID = shippingID;
    }

    @Override
    public String toString() {
        return "SaleOrder{" + "ID=" + ID + ", name=" + name + ", status=" + status + ", checkInAtWorkNumber=" + checkInAtWorkID + ", shippingID=" + shippingID + '}';
    }
    
}
