/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tomverbeeck.beans;

import be.socialsecurity.errors.v1.LocalisedString;
import be.socialsecurity.presenceregistration.schemas.v1.CancelPresenceRegistrationResponse;
import be.socialsecurity.presenceregistration.schemas.v1.CancelPresenceRequestType;
import be.socialsecurity.presenceregistration.schemas.v1.GetPresenceRegistrationRequestType;
import be.socialsecurity.presenceregistration.schemas.v1.PresenceRegistrationSubmitType;
import be.socialsecurity.presenceregistration.schemas.v1.PresenceRegistrationType;
import be.socialsecurity.presenceregistration.schemas.v1.RegisterPresenceRegistrationResponse;
import be.socialsecurity.presenceregistration.schemas.v1.SearchPresenceRegistrationCriteria;
import be.socialsecurity.presenceregistration.schemas.v1.SearchPresencesRequestType;
import be.socialsecurity.presenceregistration.schemas.v1.StatusListType;
import be.socialsecurity.presenceregistration.schemas.v1.StatusType;
import be.socialsecurity.presenceregistration.v1.CancelPresencesRequest;
import be.socialsecurity.presenceregistration.v1.CancelPresencesResponse;
import be.socialsecurity.presenceregistration.v1.GetPresenceRegistrationRequest;
import be.socialsecurity.presenceregistration.v1.GetPresenceRegistrationResponse;
import be.socialsecurity.presenceregistration.v1.RegisterPresencesRequest;
import be.socialsecurity.presenceregistration.v1.RegisterPresencesResponse;
import be.socialsecurity.presenceregistration.v1.SearchPresencesRequest;
import be.socialsecurity.presenceregistration.v1.SystemError;
import com.arnowouter.marcelleke.ejb.connectors.odoo.OdooERPConnector;
import com.arnowouter.marcelleke.ejb.exceptions.ErpException;
import com.arnowouter.marcelleke.ejb.exceptions.MarcellekeSystemException;
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
import javax.ejb.Stateless;
import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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
public class RszFacade extends AbstractFacade<Rsz> {

    @PersistenceContext(unitName = PU)
    private EntityManager em;

    private RegisterPresencesRequest registerPresenceList;
    private CancelPresencesRequest cancelPresenceList;
    private GetPresenceRegistrationRequest getPresenceRequest;
    private SearchPresencesRequest searchRequest;

    private Validator validator = new Validator();

    @EJB
    RszRegisteredFacade registeredBean;

    @EJB
    MailSender mailSender;
    
    private OdooERPConnector test;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public Validator getValidator() {
        return validator;
    }

    public RszRegisteredFacade getRegisteredBean() {
        return registeredBean;
    }

    public RszFacade() {
        super(Rsz.class);
        System.out.println("Constructor rsz facade");
        if (registerPresenceList == null) {
            registerPresenceList = new RegisterPresencesRequest();
        }
        if (cancelPresenceList == null) {
            cancelPresenceList = new CancelPresencesRequest();
        }
        if (getPresenceRequest == null) {
            getPresenceRequest = new GetPresenceRegistrationRequest();
        }
        if (searchRequest == null) {
            searchRequest = new SearchPresencesRequest();
        }
        try {
            if (test == null) {
                test = new OdooERPConnector("https", "intern.isoltechnics.com", 443, "DDI2", "admin@isoltechnics.be", "DDIsoltechnics90");
                System.out.println("Connected to ODOO");
            }
        } catch (ErpException ex) {
            Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<Rsz> getEmployeesByInss(String inss) {
        inss = inss.toLowerCase();
        TypedQuery<Rsz> query = em.createNamedQuery(Rsz.FIND_BY_INSS, Rsz.class);
        query.setParameter("inss", inss);
        return query.getResultList();
    }

    public void addRequest() {
        if (registerPresenceList == null) {
            System.out.println("List is null");
        }
        List<Rsz> employees = new ArrayList<>();
        System.out.println("### Pre registration get schedule ###");
        try {
            employees = test.testFilterWorkOrdersOnDate();
        } catch (ErpException | MarcellekeSystemException ex) {
            Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("### Pre registration schedule loaded ###");

        System.out.println("### Pre registration check data ###");
        for (Rsz e : employees) {
            PresenceRegistrationSubmitType request = new PresenceRegistrationSubmitType();

            create(e);

            if (checkRegisterRequest(e, true)) {
                continue;
            }

            request.setCompanyID(Long.parseLong(validator.filterPointAndStripe(e.getCompanyId())));
            request.setINSS(validator.filterPointAndStripe(e.getInss()));
            request.setWorkPlaceId(validator.filterPointAndStripe(e.getWorkPlaceId()));

            GregorianCalendar cal = new GregorianCalendar();
            XMLGregorianCalendar xmlDate = null;
            try {
                xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
            } catch (DatatypeConfigurationException ex) {
                Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
            }
            request.setRegistrationDate(xmlDate);

            registerPresenceList.getPresenceRegistrationRequest().add(request);
        }

        System.out.println("### Pre registration data checked, Size before " + employees.size() + " and after " + registerPresenceList.getPresenceRegistrationRequest().size() + "###");
    }

    public void addCancelRequest(String xmlDate, String inss, String workplaceID, String reason) {
        if (!validator.checkINSS(inss)) {
            System.out.println("INSS is not allowed max 11 chars and this inss has " + inss.length() + " chars");
            return;
        } else if (!validator.checkCheckinAtWorkNumber(workplaceID)) {
            System.out.println("CheckinAtWork number is not allowed max 13 chars and this inss has " + workplaceID.length() + " chars");
            return;
        } else if (!validator.checkCancelReason(reason)) {
            System.out.println("Reason must be Holiday, Disease, Planning, C32a or Other instead of " + reason);
            return;
        }

        String id = getRegistrationID(xmlDate, validator.filterPointAndStripe(inss), validator.filterPointAndStripe(workplaceID));

        if (registerPresenceList == null) {
            System.out.println("List is null");
        }

        if (id.isEmpty()) {
            System.out.println("No reg ID found");
            return;
        }

        CancelPresenceRequestType request = new CancelPresenceRequestType();
        request.setPresenceRegistrationId(Long.parseLong(id));
        request.setCancellationReason(reason);

        cancelPresenceList.getCancelPresenceRequest().add(request);
    }

    public void searchPreferences(String type, String value) {
        SearchPresencesRequestType request = new SearchPresencesRequestType();
        SearchPresenceRegistrationCriteria criteria = new SearchPresenceRegistrationCriteria();

        if (type.equals("INSS")) {
            criteria.setINSS(value);
        } else if (type.equals("CampanyID")) {
            criteria.setCompanyID(Long.parseLong(value));
        } else if (type.equals("Status")) {
            StatusListType remark = new StatusListType();
            if (value.equals("SUCCESSFULLY_REGISTERED")) {
                remark.getStatus().add(StatusType.SUCCESSFULLY_REGISTERED);
            } else if (value.equals("FAILED")) {
                remark.getStatus().add(StatusType.FAILED);
            }
            criteria.setStatusList(remark);
        }

        request.setSearchPresenceRegistrationCriteria(criteria);
        searchRequest.setSearchPresenceRequest(request);
    }

    public void getPrecensesRequest(String inss, String workplaceID) {
        GetPresenceRegistrationRequestType request = new GetPresenceRegistrationRequestType();

        GregorianCalendar cal = new GregorianCalendar();
        XMLGregorianCalendar xmlDate = null;
        try {
            xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
            System.out.println(getRegistrationID(xmlDate.toString(), inss, workplaceID));
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (getRegistrationID(xmlDate.toString(), inss, workplaceID).isEmpty()) {
            System.out.println("No registrations found");
        } else {
            request.setPresenceRegistrationId(Long.parseLong(getRegistrationID(xmlDate.toString(), inss, workplaceID)));
            getPresenceRequest.setGetPresenceRegistrationRequest(request);
        }
    }

    public RegisterPresencesResponse processRegisterPresence(RegisterPresencesResponse responseList) {
        if (responseList.getPresenceRegistrationResponse() == null) {
            System.out.println("Error REGISTER received");
            return new RegisterPresencesResponse();
        }
        for (RegisterPresenceRegistrationResponse response : responseList.getPresenceRegistrationResponse()) {
            if (response.getPresenceRegistrationError() != null) {
                PresenceRegistrationSubmitType submitRequest = response.getPresenceRegistrationError().getPresenceRegistrationSubmitted();
                RszRegistered ent = new RszRegistered();
                ent.setInss(submitRequest.getINSS());
                ent.setCompanyID("" + submitRequest.getCompanyID());
                ent.setPresenceRegistrationId(submitRequest.getClientPresenceRegistrationReference());
                ent.setStatus("Failed Manual Checkin Requested");
                ent.setCreationDate(submitRequest.getRegistrationDate().toString());
                ent.setCheckinAtWorkNumber(submitRequest.getWorkPlaceId());

                try {
                    mailSender.sendMailFailedRegistrationAfterCheck("Employee with inss: " + ent.getInss() + " not checked in, status: " + ent.getStatus()
                            + "\nAnd error message: " + response.getPresenceRegistrationError().getErrorList().getError().get(0).getErrorDescription()
                            + "\nAnd needs a manual registration at workplace: " + ent.getCheckinAtWorkNumber() + ".");
                } catch (MessagingException ex) {
                    Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
                }

                registeredBean.create(ent);
                continue;
            } else if (response.getPresenceRegistration() == null) {
                System.out.println("Something went very wrong");
                continue;
            }
            RszRegistered entity = new RszRegistered();
            GregorianCalendar cal = new GregorianCalendar();
            XMLGregorianCalendar xmlDate = null;
            try {
                xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
                entity.setCreationDate(xmlDate.toString());
            } catch (DatatypeConfigurationException ex) {
                Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
            }
            entity.setInss(response.getPresenceRegistration().getINSS());
            entity.setCompanyID("" + response.getPresenceRegistration().getPresenceRegistrationSubmitted().getCompanyID());
            entity.setPresenceRegistrationId("" + response.getPresenceRegistration().getPresenceRegistrationId());
            entity.setCheckinAtWorkNumber(response.getPresenceRegistration().getPresenceRegistrationSubmitted().getWorkPlaceId());
            if (response.getPresenceRegistration().getLastValidation().getStatus() == StatusType.SUCCESSFULLY_REGISTERED) {
                entity.setStatus(response.getPresenceRegistration().getLastValidation().getStatus().toString());
            } else if (response.getPresenceRegistration().getLastValidation().getStatus() == StatusType.FAILED) {
                entity.setStatus("Failed Manual Checkin Requested");
                try {
                    mailSender.sendMailFailedRegistrationAfterCheck("Employee with inss: " + entity.getInss() + " not checked in, status: " + entity.getStatus()
                            + "\nAnd needs a manual registration at workplace: " + entity.getCheckinAtWorkNumber() + ".");
                } catch (MessagingException ex) {
                    Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            registeredBean.create(entity);

        }
        return responseList;
    }

    public CancelPresencesResponse processCancelPresence(CancelPresencesResponse responseList) {
        if (responseList.getCancelPresenceResponse() == null) {
            System.out.println("Error CANCEL received");
            return new CancelPresencesResponse();
        }
        for (CancelPresenceRegistrationResponse response : responseList.getCancelPresenceResponse()) {

            //Check if you get a good response or a systemerror
            if (response.getPresenceRegistrationError() != null) {
                RszRegistered entity = new RszRegistered();
                GregorianCalendar cal = new GregorianCalendar();
                XMLGregorianCalendar xmlDate = null;
                try {
                    xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
                    entity.setCreationDate(xmlDate.toString());
                } catch (DatatypeConfigurationException ex) {
                    Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
                }
                String id = "";
                for (RszRegistered e : registeredBean.findAll()) {
                    String regID = "" + response.getPresenceRegistrationError().getCancelPresenceRequest().getPresenceRegistrationId();
                    if (e.getPresenceRegistrationId().equals(regID)) {
                        id = e.getId().toString();
                    }
                }
                if (id.isEmpty()) {
                    continue;
                }
                RszRegistered registeredEnt = registeredBean.find(Integer.parseInt(id));
                if (registeredEnt == null) {
                    System.out.println("Already deleted from registered db");
                } else {
                    entity.setInss(registeredEnt.getInss());
                    entity.setPresenceRegistrationId(registeredEnt.getPresenceRegistrationId());
                    entity.setCheckinAtWorkNumber(registeredEnt.getCheckinAtWorkNumber());
                    entity.setCompanyID(registeredEnt.getCompanyID());
                    entity.setStatus(response.getPresenceRegistrationError().getErrorList().getError().get(0).getErrorDescription());

                    try {
                        mailSender.sendMailFailedRegistrationAfterCheck("Employee with inss: " + entity.getInss() + " not cancelled, status: " + entity.getStatus()
                                + "\nAnd error message: " + response.getPresenceRegistrationError().getErrorList().getError().get(0).getErrorDescription()
                                + "\nAnd needs a manual registration at workplace: " + entity.getCheckinAtWorkNumber() + ".");
                    } catch (MessagingException ex) {
                        Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    registeredBean.remove(registeredEnt);
                    registeredBean.create(entity);
                }
                continue;
            }

            RszRegistered entity = new RszRegistered();
            entity.setInss(response.getPresenceRegistration().getINSS());
            entity.setPresenceRegistrationId("" + response.getPresenceRegistration().getPresenceRegistrationId());
            entity.setCheckinAtWorkNumber(response.getPresenceRegistration().getPresenceRegistrationSubmitted().getWorkPlaceId());
            entity.setCompanyID("" + response.getPresenceRegistration().getPresenceRegistrationSubmitted().getCompanyID());
            GregorianCalendar cal = new GregorianCalendar();
            XMLGregorianCalendar xmlDate = null;
            try {
                xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
                entity.setCreationDate(xmlDate.toString());
            } catch (DatatypeConfigurationException ex) {
                Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
            }

            if (response.getPresenceRegistration().getLastValidation().getStatus() == StatusType.SUCCESSFULLY_CANCELLED) {
                String id = "";
                entity.setStatus(response.getPresenceRegistration().getLastValidation().getStatus().toString());
                for (RszRegistered e : registeredBean.findAll()) {
                    String regID = "" + response.getPresenceRegistration().getPresenceRegistrationId();
                    if (e.getPresenceRegistrationId().equals(regID)) {
                        id = "" + e.getId();
                    }
                }
                if (id.isEmpty()) {
                    continue;
                }

                RszRegistered registeredEnt = registeredBean.find(Integer.parseInt(id));
                if (registeredEnt == null) {
                    System.out.println("Nothing found for this is (" + id + ")");
                } else {
                    registeredBean.remove(registeredEnt);
                }
            } else if (response.getPresenceRegistration().getLastValidation().getStatus() == StatusType.FAILED) {
                entity.setStatus(response.getPresenceRegistration().getLastValidation().getStatus().toString());
            }
            registeredBean.create(entity);

        }
        return responseList;
    }

    public GetPresenceRegistrationResponse processGetRequest(GetPresenceRegistrationResponse response) {
        if (response.getGetPresenceRegistrationResponseType() == null) {
            System.out.println("Error GET received");
            return new GetPresenceRegistrationResponse();
        }
        PresenceRegistrationType realResponse = response.getGetPresenceRegistrationResponseType().getGetPresenceRegistrationType().getPresenceRegistrationType();

        if (realResponse.getLastValidation().getStatus() == StatusType.SUCCESSFULLY_REGISTERED) {
            System.out.println("Employee is registered successfully");
        } else {
            System.out.println("Employee is not registered");
        }

        return response;
    }

    public RegisterPresencesRequest getRegisterPresenceList() {
        return registerPresenceList;
    }

    public void setRegisterPresenceList(RegisterPresencesRequest registerPresenceList) {
        this.registerPresenceList = registerPresenceList;
    }

    public CancelPresencesRequest getCancelPresenceList() {
        return cancelPresenceList;
    }

    public GetPresenceRegistrationRequest getGetPresenceRequest() {
        return getPresenceRequest;
    }

    public SearchPresencesRequest getSearchRequest() {
        return searchRequest;
    }

    public boolean systemError(SystemError error, String inss, String workplaceid) throws MessagingException {
        boolean returnBool = true;
        printLocalisedString(error.getFaultInfo().getMessage());

        if (error.getFaultInfo().getCode().equals("SOA-02001")) {
            mailSender.sendMailServiceDesk();
            returnBool = false;
        } else if (error.getFaultInfo().getCode().equals("SOA-03006")) {
            String errorMsg = "Employees registration " + inss + " failed with reason: \n\t" + getLocalisedString(error.getFaultInfo().getMessage());
            errorMsg += "\n" + "Please registrate this employee manually at workplace: " + workplaceid;
            mailSender.sendMailFailedRegistrationAfterCheck(errorMsg);
            returnBool = false;
        }

        return returnBool;
    }

    public void printLocalisedString(List<LocalisedString> s) {
        String error = "";
        for (LocalisedString fault : s) {
            error += fault.getValue();
            error += " ";
        }
        System.out.println("Error: '" + error + "'");
    }

    public String getLocalisedString(List<LocalisedString> s) {
        String error = "";
        for (LocalisedString fault : s) {
            error += fault.getValue();
            error += " ";
        }
        return error;
    }

    public String getRegistrationID(String date, String inss, String workplaceID) {
        if (date == null) {
            return "";
        }
        String regID = "";
        List<RszRegistered> employees;
        employees = registeredBean.findAll();
        System.out.println("Size is " + employees.size());

        for (RszRegistered e : employees) {
            if (e.getInss().equals(inss) && e.getCreationDate().equals(date) && e.getCheckinAtWorkNumber().equals(workplaceID)) {
                regID = "" + e.getPresenceRegistrationId();
                System.out.println("Reg ID gevonden");
            }
        }
        return regID;
    }

    public boolean checkRegisterRequest(Rsz e, boolean preRegister) {
        boolean error = false;
        String errorMessage = "";
        if (!validator.checkINSS(e.getInss()) || e.getInss().isEmpty()) {
            errorMessage = "\t- INSS is not allowed, max 11 chars and > 0 and this inss has " + e.getInss().length() + " chars";
            error = true;
        }
        if (!validator.checkCheckinAtWorkNumber(e.getWorkPlaceId()) || e.getWorkPlaceId().isEmpty()) {
            if (errorMessage.isEmpty()) {
                errorMessage = "\t- CheckinAtWork number is not allowed max 13 chars and > 0 and this checkinatworkID has " + e.getWorkPlaceId().length() + " chars";
            } else {
                errorMessage += "\n\t- CheckinAtWork number is not allowed max 13 chars and > 0 and this checkinatworkID has " + e.getWorkPlaceId().length() + " chars";
            }
            error = true;
        }
        if (!validator.checkCompanyID(e.getCompanyId()) || e.getCompanyId().isEmpty()) {
            if (errorMessage.isEmpty()) {
                errorMessage = "\t- Company id is not allowed max 13 chars and > 0 and this comany id has " + e.getCompanyId().length() + " chars";
            } else {
                errorMessage += "\n\t- Company id is not allowed max 13 chars and > 0 and this comany id has " + e.getCompanyId().length() + " chars";
            }
            error = true;
        }
        errorMessage += "";
        if (error) {
            try {
                //Pre registration doesn't send an email to ODOO
                if (!preRegister) {
                    mailSender.sendMailFailedRegistrationInput(e.getInss(), errorMessage, e.getWorkPlaceId());
                }
            } catch (MessagingException ex) {
                System.out.println("!!! ERROR check register request: " + ex.toString());
            }
        }
        return error;
    }
}
