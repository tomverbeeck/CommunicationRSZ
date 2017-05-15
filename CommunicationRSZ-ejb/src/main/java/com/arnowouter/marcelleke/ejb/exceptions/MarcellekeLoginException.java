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
public class MarcellekeLoginException extends MarcellekeSystemException {

    public MarcellekeLoginException() {
    }

    public MarcellekeLoginException(String message) {
        super(message);
    }

    public MarcellekeLoginException(String message, Exception ex) {
        super(message, ex);
    }
    
}
