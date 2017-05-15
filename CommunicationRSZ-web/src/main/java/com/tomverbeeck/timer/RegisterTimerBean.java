/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tomverbeeck.timer;

import be.socialsecurity.presenceregistration.schemas.v1.RegisterPresenceRegistrationResponse;
import be.socialsecurity.presenceregistration.v1.RegisterPresencesRequest;
import be.socialsecurity.presenceregistration.v1.RegisterPresencesResponse;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.tomverbeeck.beans.MailSender;
import com.tomverbeeck.beans.RszFacade;
import com.tomverbeeck.beans.RszRegisteredFacade;
import com.tomverbeeck.entities.Rsz;
import com.tomverbeeck.entities.RszRegistered;
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
    
    @EJB
    private MailSender mailSender;

    @EJB
    RszRegisteredFacade registeredBean;
    
    @EJB
    RszFacade rszBean;

    @Schedule(dayOfWeek = "*", month = "*", hour = "*", dayOfMonth = "5", year = "*", minute = "47", second = "00", persistent = false)
    public void myTimerPreRegister() {
        if (preRegister) {
            try {
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

                RegisterPresencesRequest outputAddPressence = responseAddPressence.getEntity(RegisterPresencesRequest.class);

                System.out.println("Output Add Pressence .... \n");
                if(!outputAddPressence.getPresenceRegistrationRequest().isEmpty())
                    System.out.println(outputAddPressence.getPresenceRegistrationRequest().get(0).getINSS());

                //Initialize the proxy for the saml token
                Client clientInitRegister = Client.create();
                WebResource wrInitRegister = clientInitRegister
                        .resource("http://localhost:8080/CommunicationRSZ-web/rest/RegisterPresence/initRegister");

                ClientResponse responseInitRegister = wrInitRegister.accept("application/xml")
                        .get(ClientResponse.class);

                if (responseInitRegister.getStatus() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + responseInitRegister.getStatus());
                }

                RegisterPresencesRequest outputInitRegister = responseInitRegister.getEntity(RegisterPresencesRequest.class);

                System.out.println("Output Init Register .... \n");
                if(!outputInitRegister.getPresenceRegistrationRequest().isEmpty())
                    System.out.println(outputInitRegister.getPresenceRegistrationRequest().get(0).getINSS());

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

                RegisterPresencesResponse outputRegister = responseRegister.getEntity(RegisterPresencesResponse.class);

                System.out.println("Output Register .... \n");
                for (RegisterPresenceRegistrationResponse response : outputRegister.getPresenceRegistrationResponse()) {
                    System.out.println("Status of " + response.getPresenceRegistration().getINSS() + ": " + response.getPresenceRegistration().getLastValidation().getStatus().toString());
                }

            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("System doesn't allow pre register");
        }
    }

    @Schedule(dayOfWeek = "*", month = "*", hour = "*", dayOfMonth = "5", year = "*", minute = "49", second = "00", persistent = false)
    public void myTimerAfterRegister() {
        List<RszRegistered> checkedInEmployees = registeredBean.findAll();
        List<RszRegistered> checkedInToday = new ArrayList<>();
        List<Rsz> preRegisterd = rszBean.findAll();

        GregorianCalendar cal = new GregorianCalendar();
        XMLGregorianCalendar xmlDate = null;
        try {
            xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(RszRegistered reg : checkedInEmployees){
            if(reg.getCreationDate().equals(xmlDate.toString())){
                checkedInToday.add(reg);
            }
        }
        
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
                    if(rszBean.filterPointAndStripe(reg.getInss()).equals(registered.getInss()) && rszBean.filterPointAndStripe(reg.getWorkPlaceId()).equals(registered.getCheckinAtWorkNumber())){
                        found = true;
                    }
                }
                if(!found){
                    System.out.println("Employee with inss: " + reg.getInss() + " was not checked in at workplace: " + reg.getWorkPlaceId());
                }
            }
        }
        
        System.out.println("After Registration Completed");
    }
}
