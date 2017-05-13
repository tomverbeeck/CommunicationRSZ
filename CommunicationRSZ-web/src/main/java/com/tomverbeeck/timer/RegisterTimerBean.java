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
import javax.ejb.LocalBean;
import javax.ejb.Schedule;
import javax.ejb.Stateless;

/**
 *
 * @author Tom Verbeeck
 */
@Stateless
@LocalBean
public class RegisterTimerBean {

    boolean preRegister = false;

    @Schedule(dayOfWeek = "*", month = "*", hour = "06", dayOfMonth = "*", year = "*", minute = "00", second = "00", persistent = false)
    public void myTimerPreRegister() {
        if (preRegister) {
            try {
                //Add a register request to the list
                Client clientAddPressence = Client.create();
                WebResource webResourceAddPresence = clientAddPressence
                        .resource("http://localhost:8080/DummyRSZCXF-web/rest/RegisterPresence/addRegisterPresence");

                ClientResponse responseAddPressence = webResourceAddPresence.accept("application/xml")
                        .get(ClientResponse.class);

                if (responseAddPressence.getStatus() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + responseAddPressence.getStatus());
                }

                RegisterPresencesRequest outputAddPressence = responseAddPressence.getEntity(RegisterPresencesRequest.class);

                System.out.println("Output Add Pressence .... \n");
                System.out.println(outputAddPressence.getPresenceRegistrationRequest().get(0).getINSS());

                //Initialize the proxy for the saml token
                Client clientInitRegister = Client.create();
                WebResource wrInitRegister = clientInitRegister
                        .resource("http://localhost:8080/DummyRSZCXF-web/rest/RegisterPresence/initRegister");

                ClientResponse responseInitRegister = wrInitRegister.accept("application/xml")
                        .get(ClientResponse.class);

                if (responseInitRegister.getStatus() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + responseInitRegister.getStatus());
                }

                RegisterPresencesRequest outputInitRegister = responseInitRegister.getEntity(RegisterPresencesRequest.class);

                System.out.println("Output Init Register .... \n");
                System.out.println(outputInitRegister.getPresenceRegistrationRequest().get(0).getINSS());

                //register presences
                Client clientRegister = Client.create();
                WebResource wrRegister = clientRegister
                        .resource("http://localhost:8080/DummyRSZCXF-web/rest/RegisterPresence/register");

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
        }
    }

    @Schedule(dayOfWeek = "*", month = "*", hour = "10", dayOfMonth = "*", year = "*", minute = "00", second = "00", persistent = false)
    public void myTimerAfterRegister() {
        
    }
}
