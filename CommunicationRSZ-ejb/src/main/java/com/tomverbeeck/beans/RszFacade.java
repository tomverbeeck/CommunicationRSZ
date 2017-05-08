/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tomverbeeck.beans;

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
import com.tomverbeeck.entities.Rsz;
import com.tomverbeeck.entities.RszRegistered;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

    @EJB
    RszRegisteredFacade registeredBean;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public RszFacade() {
        super(Rsz.class);
        System.out.println("Constructor rsz facade");
        registerPresenceList = new RegisterPresencesRequest();
        cancelPresenceList = new CancelPresencesRequest();
        getPresenceRequest = new GetPresenceRegistrationRequest();
        searchRequest = new SearchPresencesRequest();
    }

    public void addRequest() {
        if (registerPresenceList == null) {
            System.out.println("List is null");
        }
        List<Rsz> employees;
        employees = findAll();

        for (Rsz e : employees) {
            PresenceRegistrationSubmitType request = new PresenceRegistrationSubmitType();

            request.setCompanyID(Long.parseLong(filterPointAndStripe(e.getCompanyId())));
            request.setINSS(filterPointAndStripe(e.getInss()));
            request.setWorkPlaceId(filterPointAndStripe(e.getWorkPlaceId()));

            GregorianCalendar cal = new GregorianCalendar();
            XMLGregorianCalendar xmlDate = null;
            try {
                xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
                //System.out.println("Date is " + xmlDate.toString());
            } catch (DatatypeConfigurationException ex) {
                Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
            }
            request.setRegistrationDate(xmlDate);

            registerPresenceList.getPresenceRegistrationRequest().add(request);
        }
    }

    public RegisterPresencesRequest getRegisterPresenceList() {
        return registerPresenceList;
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
                ent.setPresenceRegistrationId(submitRequest.getClientPresenceRegistrationReference());
                ent.setStatus("FAILED");
                ent.setCreationDate(submitRequest.getRegistrationDate().toString());
                ent.setCheckinAtWorkNumber(submitRequest.getWorkPlaceId());

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
            entity.setPresenceRegistrationId("" + response.getPresenceRegistration().getPresenceRegistrationId());
            entity.setCheckinAtWorkNumber(response.getPresenceRegistration().getPresenceRegistrationSubmitted().getWorkPlaceId());
            if (response.getPresenceRegistration().getLastValidation().getStatus() == StatusType.SUCCESSFULLY_REGISTERED) {
                entity.setStatus(response.getPresenceRegistration().getLastValidation().getStatus().toString());
            } else if (response.getPresenceRegistration().getLastValidation().getStatus() == StatusType.FAILED) {
                entity.setStatus(response.getPresenceRegistration().getLastValidation().getStatus().toString());
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
                    entity.setStatus(response.getPresenceRegistrationError().getErrorList().getError().get(0).getErrorDescription());
                    registeredBean.remove(registeredEnt);
                    registeredBean.create(entity);
                }
                continue;
            }

            RszRegistered entity = new RszRegistered();
            entity.setInss(response.getPresenceRegistration().getINSS());
            entity.setPresenceRegistrationId("" + response.getPresenceRegistration().getPresenceRegistrationId());
            entity.setCheckinAtWorkNumber(response.getPresenceRegistration().getPresenceRegistrationSubmitted().getWorkPlaceId());
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

    public String getRegistrationID(String date, String inss, String workplaceID) {
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

    public void addCancelRequest(String id, String reason) {
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

    public void getPrecensesRequest(String inss, String workplaceID) {
        GetPresenceRegistrationRequestType request = new GetPresenceRegistrationRequestType();

        GregorianCalendar cal = new GregorianCalendar();
        XMLGregorianCalendar xmlDate = null;
        try {
            xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(getRegistrationID(xmlDate.toString(), inss, workplaceID));
        if (getRegistrationID(xmlDate.toString(), inss, workplaceID).isEmpty()) {
            System.out.println("No registrations found");
        } else {
            request.setPresenceRegistrationId(Long.parseLong(getRegistrationID(xmlDate.toString(), inss, workplaceID)));
            getPresenceRequest.setGetPresenceRegistrationRequest(request);
        }
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

    private String filterPointAndStripe(String toFilter) {
        String returnString = "";
        if (toFilter.contains("-") || toFilter.contains(".")) {
            String[] partssInss = toFilter.split("-|\\.");
            for (String s : partssInss) {
                returnString += s;
            }
        } else {
            returnString = toFilter;
        }
        return returnString;
    }
}
