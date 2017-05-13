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
public class BadgerEmailSettings {

    //----------------------------------------------------------------//
    //------------------------- EMAIL SETTINGS -----------------------//
    //----------------------------------------------------------------//
    public static String EMAIL_host = "smtp.gmail.com";
    public static String EMAIL_from = "badgernoreply";
    public static String EMAIL_password = "wachtwoorddatinplaintextmagstaan";
    public static String EMAIL_protocol = "smtps";
    public static int EMAIL_port = 465;//587;
    public static String EMAIL_subject = "Service is not available. Please contact service desk.";
    public static String RECIPIENT = "verbeecktom18@gmail.com";

    public static void setEMAIL_host(String EMAIL_host) {
        BadgerEmailSettings.EMAIL_host = EMAIL_host;
    }

    public static void setEMAIL_from(String EMAIL_from) {
        BadgerEmailSettings.EMAIL_from = EMAIL_from;
    }

    public static void setEMAIL_password(String EMAIL_password) {
        BadgerEmailSettings.EMAIL_password = EMAIL_password;
    }

    public static void setEMAIL_protocol(String EMAIL_protocol) {
        BadgerEmailSettings.EMAIL_protocol = EMAIL_protocol;
    }

    public static void setEMAIL_port(int EMAIL_port) {
        BadgerEmailSettings.EMAIL_port = EMAIL_port;
    }

    public static void setEMAIL_subject(String EMAIL_subject) {
        BadgerEmailSettings.EMAIL_subject = EMAIL_subject;
    }
}
