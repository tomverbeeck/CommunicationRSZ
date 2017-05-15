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
public class MarcellekeSystemException extends Exception{

    public MarcellekeSystemException() {
    }

    public MarcellekeSystemException(String message) {
        super(message);
    }

    public MarcellekeSystemException(String message, Exception ex) {
        super(message + ": " + ex.getMessage());
    }
    
}
