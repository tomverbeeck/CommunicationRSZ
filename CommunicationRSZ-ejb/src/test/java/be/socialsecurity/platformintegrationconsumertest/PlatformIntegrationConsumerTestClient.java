/*
 * Hoewel al de mogelijke zorg is besteed aan de creatie van dit programma, wordt geen enkele waarborg gegeven met betrekking tot de actualiteit, accuraatheid, correctheid, volledigheid of geschiktheid hiervan.
 * Noch de RSZ, noch enige eventueel andere instantie verantwoordelijk voor de inhoud van het portaal kunnen op
 * enigerlei wijze aansprakelijk worden gesteld voor eventuele gevolgen bij het raadplegen of het gebruik van dit
 * programma.
 *
 * Bien que le plus grand soin ait été accordé à la création de ce programme, il n’est donné aucune garantie
 * quant à son actualité, sa précision, son exactitude, son exhaustivité ou sa pertinence. Ni l’ONSS ni
 * toute autre instance éventuelle responsable du contenu du portail ne peuvent être tenus responsables de
 * quelque manière que ce soit des éventuelles conséquences de la consultation ou de l’utilisation de ce programme.
 */
package be.socialsecurity.platformintegrationconsumertest;

import be.socialsecurity.presenceregistration.schemas.v1.PresenceRegistrationSubmitType;
import be.socialsecurity.presenceregistration.v1.BusinessError;
import be.socialsecurity.presenceregistration.v1.PresenceRegistrationPortType;
import be.socialsecurity.presenceregistration.v1.PresenceRegistrationService;
import be.socialsecurity.presenceregistration.v1.RegisterPresencesRequest;
import be.socialsecurity.presenceregistration.v1.RegisterPresencesResponse;
import com.tomverbeeck.beans.RszFacade;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import static org.junit.Assert.assertNotNull;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Web service client to call the PlatformIntegrationConsumerTest service on the
 * NSSO services platform.
 *
 * @see
 * <a href="https://www.socialsecurity.be/site_nl/general/helpcentre/soa/access.htm">Toegang
 * tot het SOA-platform testen</a>
 * @see
 * <a href="https://www.socialsecurity.be/site_fr/general/helpcentre/soa/access.htm">Tester
 * l'accès à la plateforme SOA</a>
 */
public class PlatformIntegrationConsumerTestClient {

    private static PresenceRegistrationPortType port;

    private static RegisterPresencesRequest requestGlobal;

    @BeforeClass
    public static void init() throws DatatypeConfigurationException {
        PresenceRegistrationService service = new PresenceRegistrationService();
        port = service.getPresenceRegistrationSOAP11();
        requestGlobal = new RegisterPresencesRequest();

        // next three lines are optional, they dump the SOAP request/response
        Client client = ClientProxy.getClient(port);
        client.getInInterceptors().add(new LoggingInInterceptor());
        client.getOutInterceptors().add(new LoggingOutInterceptor());

        PresenceRegistrationSubmitType individualRequest = new PresenceRegistrationSubmitType();

        individualRequest.setINSS("95092027717");
        individualRequest.setCompanyID((long) 873909226);
        individualRequest.setWorkPlaceId("1Y101DRUNWB5Z");
        GregorianCalendar cal = new GregorianCalendar();
        XMLGregorianCalendar xmlDate = null;
        try {
            xmlDate = DatatypeFactory.newInstance().newXMLGregorianCalendarDate(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH), DatatypeConstants.FIELD_UNDEFINED);
            System.out.println("Date is " + xmlDate.toString());
        } catch (DatatypeConfigurationException ex) {
            Logger.getLogger(RszFacade.class.getName()).log(Level.SEVERE, null, ex);
        }
        individualRequest.setRegistrationDate(xmlDate);
        requestGlobal.getPresenceRegistrationRequest().add(individualRequest);
    }

    /**
     * Calls the checkBrokeredAccessControl operation using SAML Holder-of-Key
     * Token Profile security policy. The client first interacts with the
     * SecurityTokenService (STS) to retrieve a SAML token. Using the signed
     * SAML token the client invokes the checkBrokeredAccessControl operation.
     *
     * @see
     * <a href="https://www.socialsecurity.be/site_nl/general/helpcentre/soa/security_saml.htm">WS-Security
     * SAML Token Profile (Holder-of-Key) (nl)</a>
     * <a href="https://www.socialsecurity.be/site_fr/general/helpcentre/soa/security_saml.htm">WS-Security
     * SAML Token Profile (Holder-of-Key) (fr)</a>
     */
    @Test
    public void testCheckBrokeredAccessControl() throws be.socialsecurity.presenceregistration.v1.SystemError, BusinessError {
        RegisterPresencesResponse response = port.registerPresences(requestGlobal);
        assertNotNull(response);
        System.out.println("##########");
        System.out.println("Response status is: " + response.getPresenceRegistrationResponse().get(0).getPresenceRegistration().getLastValidation().getStatus());
        System.out.println("##########");
    }
}
