/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arnowouter.marcelleke.ejb.connectors.odoo;

import com.arnowouter.marcelleke.ejb.exceptions.ErpException;

/**
 *
 * @author Arno
 */
public class OdooException extends ErpException{

    public OdooException() {
    }

    public OdooException(String message) {
        super(message);
    }

    public OdooException(String erpSystem, String exMessage) {
        super(erpSystem, exMessage, "Odoo");
    }

}
