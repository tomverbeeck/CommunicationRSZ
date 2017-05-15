/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tomverbeeck.beans;

import com.arnowouter.marcelleke.ejb.util.BadgerEmail.BadgerEmailSettings;
import java.util.Properties;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Tom Verbeeck
 */
@Stateless
@LocalBean
public class MailSender {

    private Properties props;
    private Session session;
    private Message message;
    private Transport connection;
    private Address[] recipients;
    private boolean developMode = true;

    public void sendMailServiceDesk() throws MessagingException {
        System.out.println("Send mail method");
        setupEmailRecipients();
        setupEmailProperties();
        setupEmailSession();
        setupMessageServiceDesk();
        setupConnection();
        sendEmailAndCloseConnection();
        System.out.println("Send mail ended");
    }
    
    public void sendMailFailedRegistrationInput(String inss, String reason, String workplace) throws MessagingException {
        System.out.println("Send mail method");
        setupEmailRecipients();
        setupEmailProperties();
        setupEmailSession();
        setupMessageFailedRegisterationInput(inss, reason, workplace);
        setupConnection();
        sendEmailAndCloseConnection();
        System.out.println("Send mail ended");
    }
    
    public void sendMailFailedRegistrationAfterCheck(String messageText) throws MessagingException {
        System.out.println("Send mail method");
        setupEmailRecipients();
        setupEmailProperties();
        setupEmailSession();
        setupMessageFailedRegisterationAfterCheck(messageText);
        setupConnection();
        sendEmailAndCloseConnection();
        System.out.println("Send mail ended");
    }

    private void setupEmailProperties() throws MessagingException{
        props = System.getProperties();
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", BadgerEmailSettings.EMAIL_host);
        props.put("mail.smtp.socketFactory.port", BadgerEmailSettings.EMAIL_port);
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.user", BadgerEmailSettings.EMAIL_from);
        props.put("mail.smtp.password", BadgerEmailSettings.EMAIL_password);
        props.put("mail.smtp.port", BadgerEmailSettings.EMAIL_port);
        props.put("mail.smtp.auth", "true");
    }

    private void setupEmailRecipients() throws AddressException {
        if (!developMode) {
        } else {
            recipients = new Address[1];
            recipients[0] = new InternetAddress(BadgerEmailSettings.RECIPIENT);
        }
    }

    private void setupEmailSession() {
        session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(BadgerEmailSettings.EMAIL_from, BadgerEmailSettings.EMAIL_password);
            }
        }
        );
    }

    private void setupMessageServiceDesk() throws MessagingException {
        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(BadgerEmailSettings.EMAIL_from));
        message.addRecipients(Message.RecipientType.TO, recipients);
        message.setSubject(BadgerEmailSettings.EMAIL_subject_servicedesk);
        message.setText("Service desk is unavailable, please log in the eployees manual");
    }
    
    private void setupMessageFailedRegisterationInput(String inss, String reason, String workplace) throws MessagingException {
        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(BadgerEmailSettings.EMAIL_from));
        message.addRecipients(Message.RecipientType.TO, recipients);
        message.setSubject(BadgerEmailSettings.EMAIL_subject_failed_registration);
        message.setText("Employee with inss: " + inss + " failed because of: \n\n" + reason + "\n\n And needs a manual registration at workplace: " + workplace + ".");
    }
    
    private void setupMessageFailedRegisterationAfterCheck(String messageText) throws MessagingException {
        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(BadgerEmailSettings.EMAIL_from));
        message.addRecipients(Message.RecipientType.TO, recipients);
        message.setSubject(BadgerEmailSettings.EMAIL_subject_failed_registration);
        message.setText(messageText);
    }

    private void setupConnection() throws MessagingException {
        connection = session.getTransport(BadgerEmailSettings.EMAIL_protocol);
        connection.connect(
                BadgerEmailSettings.EMAIL_host,
                BadgerEmailSettings.EMAIL_from,
                BadgerEmailSettings.EMAIL_password
        );
    }

    private void sendEmailAndCloseConnection() throws MessagingException {
        connection.sendMessage(message, message.getAllRecipients());
        connection.close();
    }
}
