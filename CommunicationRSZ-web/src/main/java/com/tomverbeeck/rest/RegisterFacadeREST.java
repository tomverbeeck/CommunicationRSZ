/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tomverbeeck.rest;

import be.socialsecurity.presenceregistration.schemas.v1.PresenceRegistrationSubmitType;
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
import com.arnowouter.marcelleke.ejb.connectors.odoo.OdooException;
import com.arnowouter.marcelleke.ejb.exceptions.ErpException;
import com.arnowouter.marcelleke.ejb.exceptions.MarcellekeSystemException;
import com.tomverbeeck.beans.RszFacade;
import com.tomverbeeck.entities.Rsz;
import com.tomverbeeck.entities.RszRegistered;
import com.tomverbeeck.timer.RegisterTimerBean;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
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
public class RegisterFacadeREST {

    @EJB
    RszFacade rszBean;

    @EJB
    RegisterTimerBean timeBean;

    private DateTime date;

    private static PresenceRegistrationService presenceService;
    private static PresenceRegistrationPortType port;

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("registerPresenceList")
    public RegisterPresencesRequest getListPreRegistered() {
        return rszBean.getRegisterPresenceList().get(0);
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
    public RegisterPresencesRequest addRegisterRequest() {
        rszBean.addRequest();
        return rszBean.getRegisterPresenceList().get(0);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("preRegister")
    public String preRegister() {
        timeBean.myTimerPreRegister();
        return "Kleir";
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("afterRegister")
    public String afterRegister() {
        timeBean.myTimerAfterRegister();
        return "Kleir";
    }

    @POST
    @Produces(MediaType.APPLICATION_XML)
    @Path("addCancelPresence")
    public CancelPresencesRequest addCancelRequest(@QueryParam("inss") String inss, @QueryParam("reason") String reason, @QueryParam("workplaceid") String workplaceID) {
        GregorianCalendar cal = new GregorianCalendar();
        XMLGregorianCalendar xmlDate = null;
        try {
            xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        rszBean.addCancelRequest(xmlDate.toString(), inss, workplaceID, reason);
        return rszBean.getCancelPresenceList();
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("initRegister")
    public RegisterPresencesRequest initRegister() throws OdooException, ErpException, MarcellekeSystemException {
        initProxy();

        date = new DateTime();

        return rszBean.getRegisterPresenceList().get(0);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("register")
    public RegisterPresencesResponse registerRequest() throws MessagingException {
        checkTime();

        System.out.println("### Pre registration register schedule ###");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        if (presenceService == null) {
            System.out.println("Service is zero");
        }

        if (rszBean.getRegisterPresenceList().get(0).getPresenceRegistrationRequest().isEmpty()) {
            return new RegisterPresencesResponse();
        }

        RegisterPresencesResponse response = new RegisterPresencesResponse();
        if (port == null) {
            return response;
        } else {
            int dummy = 0;
            try {
                for (int i = 0; i < rszBean.getRegisterPresenceList().size(); i++) {
                    response = port.registerPresences(rszBean.getRegisterPresenceList().get(i));
                    dummy++;
                }
            } catch (SystemError ex) {
                if (!rszBean.systemError(ex, "One of the isnss's: " + rszBean.getRegisterPresenceList().get(dummy).getPresenceRegistrationRequest().get(0).getINSS(), "Not found (register request)")) {
                    System.out.println("System error in the registration: " + ex.toString());
                    return new RegisterPresencesResponse();
                }
            } catch (BusinessError ex) {
                System.out.println("Business error in the registration: " + ex.toString());
            }
        }

        System.out.println("### Pre registration registered schedule starting processing ###");

        rszBean.processRegisterPresence(response);
        //TODO if successfull send data to odoo that update was succes otherwise send fail to ODOO
        for (int i = 0; i < rszBean.getRegisterPresenceList().size(); i++) {
            rszBean.getRegisterPresenceList().get(i).getPresenceRegistrationRequest().clear();
            if(i > 1){
                rszBean.getRegisterPresenceList().remove(i);
            }
        }
        return response;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("registerEmployee")
    public RegisterPresencesResponse completeRegisterRequest(@QueryParam("inss") String inss, @QueryParam("workplaceid") String workplaceID, @QueryParam("companyid") String companyID) throws MessagingException {
        checkTime();

        Rsz ent = new Rsz();
        ent.setInss(inss);
        ent.setCompanyId(companyID);
        ent.setWorkPlaceId(workplaceID);
        GregorianCalendar cal = new GregorianCalendar();
        XMLGregorianCalendar xmlDate = null;
        try {
            xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Check if data from input is correct otherwise it will 
        System.out.println("### Check if data from input is correct otherwise it will  ###");
        List<Rsz> foundInss = rszBean.getEmployeesByInss(ent.getInss());
        if (rszBean.checkRegisterRequest(ent, false)) {
            //Normally only one employee will be returned
            if (foundInss.isEmpty()) {
                rszBean.create(ent);
                RszRegistered reg = new RszRegistered();
                reg.setCheckinAtWorkNumber(ent.getWorkPlaceId());
                reg.setCreationDate(xmlDate.toString());
                reg.setPresenceRegistrationId("Not yet registered");
                reg.setInss(ent.getInss());
                reg.setCompanyID(ent.getCompanyId());
                reg.setStatus("Failed Manual Checkin Requested");

                rszBean.getRegisteredBean().create(reg);
            }
            return new RegisterPresencesResponse();
        }

        //When the data is ok, the application will see if the employee is already registered
        System.out.println("### When the data is ok, the application will see if the employee is already registered ###");
        List<RszRegistered> alreadyReg = rszBean.getRegisteredBean().getByInss(rszBean.getValidator().filterPointAndStripe(ent.getInss()));
        System.out.println("Size already reg is " + alreadyReg.size());
        if (!alreadyReg.isEmpty()) {
            RszRegistered regiEmploye = null;
            for (RszRegistered reg : alreadyReg) {
                System.out.println("Reg is: " + reg.toString() + " and date  is " + xmlDate.toString());
                if (xmlDate.toString().equals(reg.getCreationDate())) {
                    System.out.println("\t\t\tFOOUUND");
                    regiEmploye = reg;
                    break;
                }
            }
            //if the right registration is found it will be returned to the backend
            System.out.println("### if the right registration is found it will be returned to the backend ###");
            if (regiEmploye != null) {
                if (!regiEmploye.getStatus().equals("SUCCESSFULLY_REGISTERED")) {
                    regiEmploye.setStatus("");
                    rszBean.getRegisteredBean().edit(regiEmploye);
                } else {
                    return new RegisterPresencesResponse();
                }
            } else {
                //No registration is found for this employee on this date, registration will be needed
                System.out.println("### No registration is found for this employee on this date, registration will be needed ###");
            }
        }

        PresenceRegistrationSubmitType request = new PresenceRegistrationSubmitType();
        request.setINSS(rszBean.getValidator().filterPointAndStripe(ent.getInss()));
        request.setCompanyID(Long.parseLong(rszBean.getValidator().filterPointAndStripe(ent.getCompanyId())));
        request.setWorkPlaceId(rszBean.getValidator().filterPointAndStripe(ent.getWorkPlaceId()));
        request.setRegistrationDate(xmlDate);
        //When the employee is not yet checked in, the application will first check the pre registered tabel 
        System.out.println("### When the employee is not yet checked in, the application will first check the pre registered tabel ###");
        if (foundInss.isEmpty()) {
            rszBean.create(ent);
        } else {
            //only pre registration of one day in this tabel but multiple construction sites is possible
            System.out.println("### only pre registration of one day in this tabel but multiple construction sites is possible ###");
            for (Rsz r : foundInss) {
                if (r.getWorkPlaceId().isEmpty()) {
                    int id = r.getId();
                    ent.setId(id);
                    rszBean.edit(ent);
                }
            }
        }

        rszBean.getRegisterPresenceList().get(0).getPresenceRegistrationRequest().add(request);

        System.out.println("registerRequest");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        if (presenceService == null) {
            System.out.println("Service is zero");
        }

        RegisterPresencesResponse response = new RegisterPresencesResponse();
        if (port == null) {
            return response;
        } else {
            try {
                response = port.registerPresences(rszBean.getRegisterPresenceList().get(0));
            } catch (SystemError ex) {
                if (!rszBean.systemError(ex, "INSS: " + rszBean.getRegisterPresenceList().get(0).getPresenceRegistrationRequest().get(0).getINSS(), "Not found (register one employee request)")) {
                    System.out.println("System error in the one employee registration: " + ex.toString());
                    return new RegisterPresencesResponse();
                }
            } catch (BusinessError ex) {
                System.out.println("Business error in the one employee registration: " + ex.toString());
            }
        }
        rszBean.processRegisterPresence(response);
        //TODO if successfull send data to odoo that update was succes otherwise send fail to ODOO
        rszBean.getRegisterPresenceList().get(0).getPresenceRegistrationRequest().clear();
        return response;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("cancelRequest")
    public CancelPresencesResponse cancelRequest() throws MessagingException {
        checkTime();

        System.out.println("cancelRequest");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        if (presenceService == null) {
            System.out.println("Service is zero");
        }

        if (rszBean.getCancelPresenceList().getCancelPresenceRequest().isEmpty()) {
            return new CancelPresencesResponse();
        }

        CancelPresencesResponse response = new CancelPresencesResponse();
        if (port == null) {
            System.out.println("Port is null");
        } else {
            try {
                response = port.cancelPresences(rszBean.getCancelPresenceList());
            } catch (SystemError ex) {
                if (!rszBean.systemError(ex, "Not found but registration id is " + rszBean.getCancelPresenceList().getCancelPresenceRequest().get(0).getPresenceRegistrationId(), "Not found (cancellation request)")) {
                    return new CancelPresencesResponse();
                }
                Logger.getLogger(RegisterFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BusinessError ex) {
                Logger.getLogger(RegisterFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        rszBean.processCancelPresence(response);
        //TODO if successfull send data to odoo that update was succes otherwise send fail to ODOO
        rszBean.getCancelPresenceList().getCancelPresenceRequest().clear();
        return response;
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("getRequest")
    public GetPresenceRegistrationResponse getRequest(@QueryParam("inss") String inss, @QueryParam("workplaceid") String workplaceID) throws BusinessError, SystemError, MessagingException {
        System.out.println("Check Time");
        checkTime();

        System.out.println("getRequest");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        if (presenceService == null) {
            System.out.println("Service is zero");
        }

        GetPresenceRegistrationResponse response;
        if (port == null && rszBean.getGetPresenceRequest().getGetPresenceRegistrationRequest() == null) {
            System.out.println("Port or request is null");
            response = new GetPresenceRegistrationResponse();
        } else {
            response = new GetPresenceRegistrationResponse();
            rszBean.getPrecensesRequest(inss, workplaceID);
            try {
                response = port.getPresenceRegistration(rszBean.getGetPresenceRequest());
            } catch (SystemError ex) {
                if (!rszBean.systemError(ex, inss, workplaceID)) {
                    return new GetPresenceRegistrationResponse();
                }
                rszBean.printLocalisedString(ex.getFaultInfo().getMessage());
                //Logger.getLogger(RegisterFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BusinessError ex) {
                rszBean.printLocalisedString(ex.getFaultInfo().getMessage());
                Logger.getLogger(RegisterFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //TODO if successfull send data to odoo that update was succes otherwise send fail to ODOO
        return rszBean.processGetRequest(response);
    }

    @GET
    @Produces(MediaType.APPLICATION_XML)
    @Path("searchPreference")
    public SearchPresencesResponse searchPresence(@QueryParam("type") String typeQuery,
            @QueryParam("value") String valueQuery) throws MessagingException {
        System.out.println("Check Time");
        checkTime();

        System.out.println("getRequest");
        System.setProperty("com.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump", "true");
        if (presenceService == null) {
            System.out.println("Service is zero");
        }

        SearchPresencesResponse response = new SearchPresencesResponse();
        if (port == null && rszBean.getGetPresenceRequest().getGetPresenceRegistrationRequest() == null) {
            System.out.println("Port or request is null");
        } else {
            try {
                rszBean.searchPreferences(typeQuery, valueQuery);
                response = port.searchPresences(rszBean.getSearchRequest());
            } catch (SystemError ex) {
                return new SearchPresencesResponse();
            } catch (BusinessError ex) {
                Logger.getLogger(RegisterFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return response;
    }

    private void checkTime() {
        if (date == null) {
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

    private void initProxy() {
        presenceService = new PresenceRegistrationService();
        port = presenceService.getPresenceRegistrationSOAP11();

        // next three lines are optional, they dump the SOAP request/response
        Client client = ClientProxy.getClient(port);
        client.getInInterceptors().add(new LoggingInInterceptor());
        client.getOutInterceptors().add(new LoggingOutInterceptor());
    }
}
