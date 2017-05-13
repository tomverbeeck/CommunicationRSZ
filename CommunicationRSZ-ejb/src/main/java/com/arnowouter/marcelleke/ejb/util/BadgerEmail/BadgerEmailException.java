/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arnowouter.marcelleke.ejb.util.BadgerEmail;

/**
 *
 * @author Arno
 */
public class BadgerEmailException extends Exception {

    public BadgerEmailException() {
    }

    public BadgerEmailException(String message) {
        super(message);
    }

    public BadgerEmailException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
