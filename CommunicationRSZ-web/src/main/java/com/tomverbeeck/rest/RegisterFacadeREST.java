/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tomverbeeck.rest;

import be.socialsecurity.errors.v1.LocalisedString;
import be.socialsecurity.presenceregistration.v1.BusinessError;
import be.socialsecurity.presenceregistration.v1.CancelPresencesRequest;
import be.socialsecurity.presenceregistration.v1.CancelPresencesResponse;
import be.socialsecurity.presenceregistration.v1.GetPresenceRegistrationResponse;
import be.socialsecurity.presenceregistration.v1.PresenceRegistrationPortType;
import be.socialsecurity.presenceregistration.v1.PresenceRegistrationService;
import be.socialsecurity.presenceregistration.v1.RegisterPresencesRequest;
import be.socialsecurity.presenceregistration.v1.RegisterPresencesResponse;
import be.socialsecurity.presenceregistration.v1.SearchPresencesResponse;
import be.socialsecurity.presenceregistration.v1.SystemError;
import com.tomverbeeck.beans.RszFacade;
import com.tomverbeeck.entities.Rsz;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.joda.time.DateTime;

/**
 *
 * @author Tom Verbeeck
 */
@Stateless
@Path("/RegisterPresence")
public class RegisterFacadeREST extends AbstractFacade<Rsz> {

    @PersistenceContext(unitName = PU)
    private EntityManager em;

    @EJB
    RszFacade rszBean;

    private DateTime date;

    private static PresenceRegistrationService presenceService;
    private static PresenceRegistrationPortType port;

    public RegisterFacadeREST() {
        super(Rsz.class);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("registerPresenceList")
    public RegisterPresencesRequest getListPreRegistered() {
        return rszBean.getRegisterPresenceList();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("cancelPresenceList")
    public CancelPresencesRequest getListPreCancelled() {
        return rszBean.getCancelPresenceList();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("addRegisterPresence")
    public RegisterPresencesRequest getRszXml() {
        rszBean.addRequest();
        return rszBean.getRegisterPresenceList();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("addCancelPresence")
    public CancelPresencesRequest getRszXml(@QueryParam("inss") String inss, @QueryParam("reason") String reason, @QueryParam("workplaceid") String workplaceID) {
        GregorianCalendar cal = new GregorianCalendar();
        XMLGregorianCalendar xmlDate = null;
        try {
            xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        String id = rszBean.getRegistrationID(xmlDate.toString(), inss, workplaceID);
        rszBean.addCancelRequest(id, reason);
        return rszBean.getCancelPresenceList();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("initRegister")
    public RegisterPresencesRequest initRegister() {
        initProxy();

        date = new DateTime();

        return rszBean.getRegisterPresenceList();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("register")
    public RegisterPresencesResponse registerRequest() throws BusinessError, SystemError {
        checkTime();

        System.out.println("registerRequest");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        if (presenceService == null) {
            System.out.println("Service is zero");
        }

        if (rszBean.getRegisterPresenceList().getPresenceRegistrationRequest().isEmpty()) {
            return new RegisterPresencesResponse();
        }

        RegisterPresencesResponse response;
        if (port == null) {
            return new RegisterPresencesResponse();
        } else {
            response = port.registerPresences(rszBean.getRegisterPresenceList());
        }

        rszBean.processRegisterPresence(response);
        //TODO if successfull send data to odoo that update was succes otherwise send fail to ODOO
        rszBean.getRegisterPresenceList().getPresenceRegistrationRequest().clear();
        return response;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("cancelRequest")
    public CancelPresencesResponse cancelRequest() throws BusinessError, SystemError {
        checkTime();

        System.out.println("cancelRequest");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        if (presenceService == null) {
            System.out.println("Service is zero");
        }

        if (rszBean.getCancelPresenceList().getCancelPresenceRequest().isEmpty()) {
            return new CancelPresencesResponse();
        }

        CancelPresencesResponse response;
        if (port == null) {
            System.out.println("Port is null");
            return new CancelPresencesResponse();
        } else {
            response = port.cancelPresences(rszBean.getCancelPresenceList());
        }

        rszBean.processCancelPresence(response);
        //TODO if successfull send data to odoo that update was succes otherwise send fail to ODOO
        rszBean.getCancelPresenceList().getCancelPresenceRequest().clear();
        return response;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("getRequest")
    public GetPresenceRegistrationResponse getRequest(@QueryParam("inss") String inss, @QueryParam("workplaceid") String workplaceID) throws BusinessError, SystemError {
        checkTime();

        System.out.println("getRequest");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        if (presenceService == null) {
            System.out.println("Service is zero");
        }

        GetPresenceRegistrationResponse response;
        if (port == null && rszBean.getGetPresenceRequest().getGetPresenceRegistrationRequest() == null) {
            System.out.println("Port or request is null");
            return new GetPresenceRegistrationResponse();
        } else {
            response = new GetPresenceRegistrationResponse();
            rszBean.getPrecensesRequest(inss, workplaceID);
            try {
                response = port.getPresenceRegistration(rszBean.getGetPresenceRequest());
            } catch (SystemError e) {
                printLocalisedString(e.getFaultInfo().getMessage());
            } catch (BusinessError e) {
                printLocalisedString(e.getFaultInfo().getMessage());
            }
        }

        //TODO if successfull send data to odoo that update was succes otherwise send fail to ODOO
        return rszBean.processGetRequest(response);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("searchPreference")
    public SearchPresencesResponse searchPresence(@QueryParam("type") String typeQuery,
            @QueryParam("value") String valueQuery) throws SystemError, BusinessError {
        checkTime();

        System.out.println("getRequest");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        if (presenceService == null) {
            System.out.println("Service is zero");
        }

        SearchPresencesResponse response;
        if (port == null && rszBean.getGetPresenceRequest().getGetPresenceRegistrationRequest() == null) {
            System.out.println("Port or request is null");
            return new SearchPresencesResponse();
        } else {
            rszBean.searchPreferences(typeQuery, valueQuery);
            response = port.searchPresences(rszBean.getSearchRequest());
        }

        return response;
    }

    private void checkTime() {
        if(date == null){
            date = new DateTime();
            initProxy();
            return;
        }
        DateTime dateNow = new DateTime();

        // get hours
        double hours = (dateNow.getMillis() - date.getMillis()) / 1000 / 60 / 60;
        if (hours > 1) {
            initProxy();
        }
    }
    
    private void initProxy(){
        presenceService = new PresenceRegistrationService();
            port = presenceService.getPresenceRegistrationSOAP11();

            // next three lines are optional, they dump the SOAP request/response
            Client client = ClientProxy.getClient(port);
            client.getInInterceptors().add(new LoggingInInterceptor());
            client.getOutInterceptors().add(new LoggingOutInterceptor());
    }

    private void printLocalisedString(List<LocalisedString> s) {
        String error = "";
        for (LocalisedString fault : s) {
            error += fault.getValue();
            error += " ";
        }
        System.out.println("Error: '" + error  + "'");
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
