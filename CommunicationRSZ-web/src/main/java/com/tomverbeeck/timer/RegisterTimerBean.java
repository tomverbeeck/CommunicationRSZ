/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tomverbeeck.timer;

import be.socialsecurity.presenceregistration.schemas.v1.RegisterPresenceRegistrationResponse;
import be.socialsecurity.presenceregistration.v1.RegisterPresencesResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.tomverbeeck.beans.MailSender;
import com.tomverbeeck.beans.RszFacade;
import com.tomverbeeck.beans.RszRegisteredFacade;
import com.tomverbeeck.entities.Rsz;
import com.tomverbeeck.entities.RszRegistered;
import com.tomverbeeck.validator.Validator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 *
 * @author Tom Verbeeck
 */
@Stateless
@LocalBean
public class RegisterTimerBean {

    boolean preRegister = true;
    private Validator validator = new Validator();
    
    @EJB
    private MailSender mailSender;

    @EJB
    RszRegisteredFacade registeredBean;
    
    @EJB
    RszFacade rszBean;

    @Schedule(dayOfWeek = "*", month = "*", hour = "20", dayOfMonth = "*", year = "*", minute = "45", second = "00", persistent = false)
    public void myTimerPreRegister() {
        if (preRegister) {
            try {
                for(Rsz r : rszBean.findAll()){
                    rszBean.remove(r);
                }
                
                System.out.println("### Pre registration starting ###");
                
                //Add a register request to the list
                Client clientAddPressence = Client.create();
                WebResource webResourceAddPresence = clientAddPressence
                        .resource("http://localhost:8080/CommunicationRSZ-web/rest/RegisterPresence/addRegisterPresence");

                ClientResponse responseAddPressence = webResourceAddPresence.accept("application/xml")
                        .get(ClientResponse.class);

                if (responseAddPressence.getStatus() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + responseAddPressence.getStatus());
                }

                if(rszBean.getRegisterPresenceList().getPresenceRegistrationRequest().isEmpty()){
                    System.out.println("There was no one in the schedule");
                    return;
                }

                System.out.println("### Pre registration start valid registrations ###");

                //register presences
                Client clientRegister = Client.create();
                WebResource wrRegister = clientRegister
                        .resource("http://localhost:8080/CommunicationRSZ-web/rest/RegisterPresence/register");

                ClientResponse responseRegister = wrRegister.accept("application/xml")
                        .get(ClientResponse.class);

                if (responseRegister.getStatus() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + responseRegister.getStatus());
                }
                
                System.out.println("### Pre registration schedule registered ###");

                RegisterPresencesResponse outputRegister = responseRegister.getEntity(RegisterPresencesResponse.class);

                System.out.println("\tOutput Register .... \n");
                for (RegisterPresenceRegistrationResponse response : outputRegister.getPresenceRegistrationResponse()) {
                    System.out.println("\t\tStatus of " + response.getPresenceRegistration().getINSS() + ": " + response.getPresenceRegistration().getLastValidation().getStatus().toString());
                }

                System.out.println("### Pre registration ended ###");

            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("System doesn't allow pre register");
        }
    }

    @Schedule(dayOfWeek = "*", month = "*", hour = "20", dayOfMonth = "*", year = "*", minute = "50", second = "00", persistent = false)
    public void myTimerAfterRegister() {
        List<RszRegistered> checkedInToday = new ArrayList<>();
        List<Rsz> preRegisterd = rszBean.findAll();

        GregorianCalendar cal = new GregorianCalendar();
        XMLGregorianCalendar xmlDate = null;
        try {
            xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        checkedInToday = registeredBean.getByCreationDate(xmlDate.toString());
        
        for(RszRegistered reg : checkedInToday){
            if(reg.getStatus().equals("SUCCESSFULLY_REGISTERED")){
                System.out.println("Succes: " + reg.getInss() + " with status: " + reg.getStatus());
            }else if(reg.getStatus().equals("FAILED")){
                System.out.println("Failed: " + reg.getInss() + " with status: " + reg.getStatus());
            }
        }
        
        if(preRegisterd.size() != checkedInToday.size()){
            System.out.println("Not everyone is checked in");
            System.out.println("Searching ...");
            
            for(Rsz reg : preRegisterd){
                boolean found = false;
                for(RszRegistered registered : checkedInToday){
                    if(reg.getInss().isEmpty() || reg.getWorkPlaceId().isEmpty())
                        continue;
                    if(validator.filterPointAndStripe(reg.getInss()).equals(registered.getInss()) 
                            && validator.filterPointAndStripe(reg.getWorkPlaceId()).equals(registered.getCheckinAtWorkNumber())){
                        found = true;
                    }
                }
                if(!found){
                    System.out.println("Employee with inss: " + reg.getInss() + " was not checked in at workplace: " + reg.getWorkPlaceId());
                    String errorMessage = "";
                    if(reg.getWorkPlaceId().isEmpty()){
                        errorMessage = "\tEmployee doesn't have a checkinatwork number";
                    }else if(reg.getCompanyId().isEmpty()){
                        errorMessage = "\tEmployee doesn't have a company ID";
                    }else if(errorMessage.isEmpty()){
                        errorMessage = "\tUnknown reasons this checkin failed: " + reg.toString();
                    }
                    
                    try {
                        mailSender.sendMailFailedRegistrationInput(reg.getInss(), errorMessage, reg.getWorkPlaceId());
                    } catch (MessagingException ex) {
                        Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    RszRegistered regE = new RszRegistered();
                    regE.setInss(validator.filterPointAndStripe(reg.getInss()));
                    regE.setCheckinAtWorkNumber(validator.filterPointAndStripe(reg.getWorkPlaceId()));
                    regE.setCreationDate(xmlDate.toString());
                    regE.setCompanyID(validator.filterPointAndStripe(reg.getCompanyId()));
                    regE.setPresenceRegistrationId("Not yet Registered");
                    regE.setStatus("Failed Manual Checkin Requested");
                    registeredBean.create(regE);
                }
            }
        }else{
            System.out.println("Everyone is checked in");
        }
        
        System.out.println("After Registration Completed");
    }
}
