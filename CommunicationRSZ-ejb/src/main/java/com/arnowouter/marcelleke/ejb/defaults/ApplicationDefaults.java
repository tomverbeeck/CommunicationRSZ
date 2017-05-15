
package com.arnowouter.marcelleke.ejb.defaults;

/**
 *
 * @author Arno
 */
public class ApplicationDefaults {
    public static final String COMPANY_NAME = "Badger";
    //----------------------------------------------------------------//
    //--------------------- PERSISTENCE UNIT -------------------------//
    //----------------------------------------------------------------//
    public static final String PERSISTENCE_UNIT = "Marcelleke_PU";
    
    //----------------------------------------------------------------//
    //--------------------- CONNECTION DEFAULTS ----------------------//
    //----------------------------------------------------------------//
    
    public static int CONN_defaultPort = 80;
    public static String CONN_defaultProtocol = "http";
    public static String CONN_defaultHostName = "Geen Hostname";
    public static String CONN_defaultDatabaseLogin = "Geen login";
    public static String CONN_defaultDatabasePassword = "Geen wachtwoord";
    public static String CONN_defaultDatabaseName = "Geen databasenaam";
    
    public static void setCONN_defaultPort(int CONN_defaultPort) {ApplicationDefaults.CONN_defaultPort = CONN_defaultPort;}
    public static void setCONN_defaultProtocol(String CONN_defaultProtocol) {ApplicationDefaults.CONN_defaultProtocol = CONN_defaultProtocol;}
    public static void setCONN_defaultHostName(String CONN_defaultHostName) {ApplicationDefaults.CONN_defaultHostName = CONN_defaultHostName;}
    public static void setCONN_defaultDatabaseLogin(String CONN_defaultDatabaseLogin) {ApplicationDefaults.CONN_defaultDatabaseLogin = CONN_defaultDatabaseLogin;}
    public static void setCONN_defaultDatabasePassword(String CONN_defaultDatabasePassword) {ApplicationDefaults.CONN_defaultDatabasePassword = CONN_defaultDatabasePassword;}
    public static void setCONN_defaultDatabaseName(String CONN_defaultDatabaseName) {ApplicationDefaults.CONN_defaultDatabaseName = CONN_defaultDatabaseName;}

    
    //----------------------------------------------------------------//
    //----------------------- ADDRESS DEFAULTS -----------------------//
    //----------------------------------------------------------------//
    
    public static String ADDRESS_defaultCountry = "Geen land";
    public static String ADDRESS_defaultCity = "Geen stad";
    public static String ADDRESS_defaultStreet = "Geen straat";
    public static int ADDRESS_defaultNumber = 0;
    public static String ADDRESS_defaultBus = "Geen Bus";
    public static String ADDRESS_defaultZipCode = "Geen Postcode";
    public static boolean ADDRESS_defaultPrimary = false;
   
    public static void setADDRESS_defaultCountry(String ADDRESS_defaultCountry) {ApplicationDefaults.ADDRESS_defaultCountry = ADDRESS_defaultCountry;}
    public static void setADDRESS_defaultCity(String ADDRESS_defaultCity) {ApplicationDefaults.ADDRESS_defaultCity = ADDRESS_defaultCity;}
    public static void setADDRESS_defaultStreet(String ADDRESS_defaultStreet) {ApplicationDefaults.ADDRESS_defaultStreet = ADDRESS_defaultStreet;}
    public static void setADDRESS_defaultNumber(int ADDRESS_defaultNumber) {ApplicationDefaults.ADDRESS_defaultNumber = ADDRESS_defaultNumber;}
    public static void setADDRESS_defaultBus(String ADDRESS_defaultBus) {ApplicationDefaults.ADDRESS_defaultBus = ADDRESS_defaultBus; }
    public static void setADDRESS_defaultZipCode(String ADDRESS_defaultZipCode) {ApplicationDefaults.ADDRESS_defaultZipCode = ADDRESS_defaultZipCode;}
    public static void setADDRESS_defaultPrimary(boolean ADDRESS_defaultPrimary) {ApplicationDefaults.ADDRESS_defaultPrimary = ADDRESS_defaultPrimary;}

    //----------------------------------------------------------------//
    //---------------------- ERPSystem DEFAULTS ----------------------//
    //----------------------------------------------------------------//
    
    public static String ERP_defaultName = "Geen ERP";
    public static String ERP_defaultNoWebsite = "Geen website";
    
    public static void setERP_defaultNoWebsite(String ERP_defaultNoWebsite) { ApplicationDefaults.ERP_defaultNoWebsite = ERP_defaultNoWebsite;}
    public static void setERP_defaultName(String ERP_defaultName) {ApplicationDefaults.ERP_defaultName = ERP_defaultName;}
    
    //----------------------------------------------------------------//
    //---------------------- CONTACT INFO DEFAULTS -------------------//
    //----------------------------------------------------------------//
    
    public static String CONTACT_defaultContactName = "Geen Contactpersoon";
    public static String CONTACT_defaultContactPhoneNumber = "000000000";
    public static String CONTACT_defaultEmail = "geen@email.none";

    public static void setCONTACT_defaultEmail(String CONTACT_defaultEmail) {ApplicationDefaults.CONTACT_defaultEmail = CONTACT_defaultEmail;}
    public static void setCONTACT_defaultContactName(String CONTACT_defaultContactName) {ApplicationDefaults.CONTACT_defaultContactName = CONTACT_defaultContactName;}
    public static void setCONTACT_defaultContactPhoneNumber(String CONTACT_defaultContactPhoneNumber) {ApplicationDefaults.CONTACT_defaultContactPhoneNumber = CONTACT_defaultContactPhoneNumber; }
    
    //----------------------------------------------------------------//
    //----- ERP SYSTEM NAMES (the ones we have a connector for -------//
    //----------------------------------------------------------------//
    
    //----------------------------------------------------------------//
    //------------------------ LOG FILES DEFAULTS --------------------//
    //----------------------------------------------------------------//
    public static String LOG_DIRECTORY_NAME = "BadgerLogs";
    public static String LOG_FILE_DEVELOP = "develop.csv";
    public static String LOG_FILE_RECEIVED_LOGS = "receivedLogs.csv";
    public static String LOG_FILE_RECEIVED_SESSIONS = "receivedSessions.csv";
    public static String LOG_FILE_RECEIVED_NACKS = "receivedNacks.csv";
    
    public static String ERP_ODOO = "odoo";
    public static String ERP_EXCEL = "excel";
}
