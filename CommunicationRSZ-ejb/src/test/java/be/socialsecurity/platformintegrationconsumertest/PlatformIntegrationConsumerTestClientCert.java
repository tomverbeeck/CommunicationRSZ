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

import be.socialsecurity.platformintegrationconsumertest.types.v1.IntegrationTestMessage;
import be.socialsecurity.platformintegrationconsumertest.v1.PlatformIntegrationConsumerTestPortType;
import be.socialsecurity.platformintegrationconsumertest.v1.PlatformIntegrationConsumerTestService;
import be.socialsecurity.platformintegrationconsumertest.v1.SystemError;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Web service client to call the PlatformIntegrationConsumerTest service on the NSSO services platform.
 *
 * @see <a href="https://www.socialsecurity.be/site_nl/general/helpcentre/soa/access.htm">Toegang tot het SOA-platform testen</a>
 * @see <a href="https://www.socialsecurity.be/site_fr/general/helpcentre/soa/access.htm">Tester l'accès à la plateforme SOA</a>
 */
public class PlatformIntegrationConsumerTestClientCert {

    private static PlatformIntegrationConsumerTestPortType PortPlatform;

    private static IntegrationTestMessage requestCert;

    @BeforeClass
    public static void init() throws DatatypeConfigurationException {
        PlatformIntegrationConsumerTestService service = new PlatformIntegrationConsumerTestService();
        PortPlatform = service.getPlatformIntegrationConsumerTestSOAP11();

        // next three lines are optional, they dump the SOAP request/response
        Client client = ClientProxy.getClient(PortPlatform);
        client.getInInterceptors().add(new LoggingInInterceptor());
        client.getOutInterceptors().add(new LoggingOutInterceptor());

        requestCert = new IntegrationTestMessage();
        requestCert.setMessage("Some kind of data string to echo.");
        requestCert.setTimestamp(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
    }


    /**
     * Calls the checkConnection operation using a 1-way SSL connection.
     *
     * @see <a href="https://www.socialsecurity.be/site_nl/general/helpcentre/soa/transport_security.htm">SSL/TLS Transport security (nl)</a>
     *      <a href="https://www.socialsecurity.be/site_fr/general/helpcentre/soa/transport_security.htm">SSL/TLS Transport security (fr)</a>
     */
    @Test
    public void testCheckConnection() throws DatatypeConfigurationException, SystemError {
        IntegrationTestMessage response = PortPlatform.checkConnection(requestCert);

        assertEquals(requestCert.getMessage(), response.getMessage());
        assertNull(requestCert.getAuthenticatedConsumer());
    }

    /**
     * Calls the checkAccessControl operation using X.509 Token Profile security policy. The body, timestamp and certificate of the request
     * are signed.
     *
     * @see <a href="https://www.socialsecurity.be/site_nl/general/helpcentre/soa/certificate_token.htm">WS-Security X.509 Certificate Token Profile (nl)</a>
     *      <a href="https://www.socialsecurity.be/site_fr/general/helpcentre/soa/certificate_token.htm">WS-Security X.509 Certificate Token Profile (fr)</a>
     */
    @Test
    public void testCheckAccessControl() throws DatatypeConfigurationException, SystemError {
        IntegrationTestMessage response = PortPlatform.checkAccessControl(requestCert);

        assertEquals(requestCert.getMessage(), response.getMessage());
        assertNotNull(response.getAuthenticatedConsumer());
    }

    /**
     * Calls the checkBrokeredAccessControl operation using SAML Holder-of-Key Token Profile security policy. The client first interacts
     * with the SecurityTokenService (STS) to retrieve a SAML token. Using the signed SAML token the client invokes the
     * checkBrokeredAccessControl operation.
     *
     * @see <a href="https://www.socialsecurity.be/site_nl/general/helpcentre/soa/security_saml.htm">WS-Security SAML Token Profile (Holder-of-Key) (nl)</a>
     *      <a href="https://www.socialsecurity.be/site_fr/general/helpcentre/soa/security_saml.htm">WS-Security SAML Token Profile (Holder-of-Key) (fr)</a>
     */
    @Test
    public void testCheckBrokeredAccessControl() throws DatatypeConfigurationException, SystemError {
        IntegrationTestMessage response = PortPlatform.checkBrokeredAccessControl(requestCert);

        assertEquals(requestCert.getMessage(), response.getMessage());
        assertNotNull(response.getAuthenticatedConsumer());
    }
}
