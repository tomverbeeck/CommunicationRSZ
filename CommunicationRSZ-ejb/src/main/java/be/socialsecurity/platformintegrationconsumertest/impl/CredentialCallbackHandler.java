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
package be.socialsecurity.platformintegrationconsumertest.impl;

import java.io.IOException;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.cxf.ws.security.trust.STSUtils;
import org.apache.cxf.ws.security.trust.claims.ClaimsCallback;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * CallbackHandler for private key passwords and SAML assertion Claims.
 */
public class CredentialCallbackHandler implements CallbackHandler {

    private String defaultPassword;

    private Element claims;

    @Override
    public void handle(Callback[] callbacks) throws IOException, UnsupportedCallbackException {
        for (Callback callback : callbacks) {
            if (callback instanceof WSPasswordCallback) {
                ((WSPasswordCallback) callback).setPassword(defaultPassword);
            } else if (callback instanceof ClaimsCallback) {
                ((ClaimsCallback) callback).setClaims(claims);
            } else {
                throw new UnsupportedCallbackException(callback);
            }
        }
    }

    /**
     * Sets the default password to return to each password callback.
     * @param defaultPassword
     */
    public void setDefaultPassword(String defaultPassword) {
        this.defaultPassword = defaultPassword;
    }

    /**
     * Builds the Claims structure to send to the SecurityTokenService.
     * <p/>
     * <code>
     * <wst:Claims Dialect="http://schemas.xmlsoap.org/ws/2006/12/authorization/authclaims">
     * <auth:ClaimType Uri="urn:be:smals:expeditor:number" xmlns:auth="http://schemas.xmlsoap.org/ws/2006/12/authorization">
     * <auth:Value>#expeditor number#</auth:Value>
     * </auth:ClaimType>
     * </wst:Claims>
     * </code>
     * @param defaultExpeditorNumber
     */
    public void setDefaultExpeditorNumber(String defaultExpeditorNumber) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            Document doc = dbf.newDocumentBuilder().newDocument();

            claims = doc.createElementNS(STSUtils.WST_NS_05_12, "Claims");
            claims.setPrefix("wst");
            claims.setAttribute("Dialect", "http://schemas.xmlsoap.org/ws/2006/12/authorization/authclaims");

            Element claimType = doc.createElementNS("http://schemas.xmlsoap.org/ws/2006/12/authorization", "ClaimType");
            claimType.setPrefix("aut");
            claimType.setAttribute("Uri", "urn:be:smals:expeditor:number");
            claims.appendChild(claimType);

            Element value = doc.createElementNS("http://schemas.xmlsoap.org/ws/2006/12/authorization", "Value");
            value.setPrefix("aut");
            value.setTextContent(defaultExpeditorNumber);
            claimType.appendChild(value);
        } catch (DOMException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

}