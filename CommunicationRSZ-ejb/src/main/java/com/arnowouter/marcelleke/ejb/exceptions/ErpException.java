/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arnowouter.marcelleke.ejb.exceptions;

/**
 *
 * @author Arno
 */
public class ErpException extends Exception {

    public ErpException() {
    }

    public ErpException(String message) {
        super(message);
    }

    public ErpException(String message, Throwable cause, String erpSystem) {
        super(message + "in System: " + erpSystem, cause);
    }
    
    public ErpException(String message, String exMessage, String erpSystem) {
        super(message + erpSystem +". Exception:" + exMessage);
    }
    
}
