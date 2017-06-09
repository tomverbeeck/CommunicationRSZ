/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arnowouter.marcelleke.ejb.connectors.odoo;

import com.arnowouter.javaodoo.IOdooConnector;
import com.arnowouter.javaodoo.client.OdooConnector;
import com.arnowouter.javaodoo.exceptions.OdooConnectorException;
import com.arnowouter.marcelleke.ejb.connectors.ErpSpecificConnectorInterface;
import com.arnowouter.marcelleke.ejb.connectors.erpClasses.ERPObject;
import com.arnowouter.marcelleke.ejb.connectors.erpClasses.Employee;
import com.arnowouter.marcelleke.ejb.connectors.erpClasses.Location;
import com.arnowouter.marcelleke.ejb.connectors.erpClasses.SaleOrder;
import com.arnowouter.marcelleke.ejb.connectors.erpClasses.Task;
import com.arnowouter.marcelleke.ejb.exceptions.ErpException;
import com.arnowouter.marcelleke.ejb.exceptions.MarcellekeSystemException;
import com.tomverbeeck.entities.Rsz;
import com.tomverbeeck.entities.RszRegistered;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arno
 */
public class OdooERPConnector implements ErpSpecificConnectorInterface {

    IOdooConnector odooConnector;
    private int odooUserID;
    //Odoo connection parameters
    private String protocol;
    private String host;
    private int port;
    private String dbName;
    private String dbLogin;
    private String dbPassword;

    private int counter = 0;

    // PREDEFINED FILTERS
    private final Object[] FILTER_NAMES = {
        OdooDefaults.FIELD_NAME
    };

    private final Object[] FILTER_ID = {
        OdooDefaults.FIELD_ID
    };

    private final Object[] FILTER_ALL_DATA = {
        OdooDefaults.FIELD_ID,
        OdooDefaults.FIELD_NAME,
        OdooDefaults.FIELD_WORK_ADDRESS_ID,
        OdooDefaults.FIELD_IS_A_MANAGER,
        OdooDefaults.FIELD_WORK_EMAIL,
        OdooDefaults.FIELD_MANAGER_ID,
        OdooDefaults.FIELD_IDENTIF_ID
    };

    private final Object[] FILTER_TASK_ID = {
        OdooDefaults.FIELD_DATE,
        OdooDefaults.FIELD_TASK_ID

    };

    public OdooERPConnector(String protocol, String host, int port, String dbName, String dbLogin, String dbPassword)
            throws ErpException {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.dbLogin = dbLogin;
        this.dbPassword = dbPassword;
        //TODO: watch out! This boolean tells the library to use an UNSECURE client! (Ingores Invalid SSL)
        initializeOdooConnector(true);
        authenticate();
    }

    public void writeToOdoo(RszRegistered reg) throws OdooConnectorException {
        String model = OdooDefaults.MODEL_CHECKINATWORK;
        HashMap<String, String> dataToWrite = new HashMap() {
            {
                put("CheckinatworkID", reg.getCheckinAtWorkNumber());
                put("Rijksregisternummer", reg.getInss());
                put("Datum", reg.getCreationDate());
                put("PresenceRegistrationID", reg.getPresenceRegistrationId());
                put("Status", reg.getStatus());
            }
        };
        odooConnector.createRecord(model, dataToWrite);
    }

    @Override
    public List<String> getAllEmployeeNames() throws ErpException {

        try {
            ArrayList<String> allEmployeeNames = new ArrayList<>();

            for (Object employeeObject : odooConnector.searchAndRead(OdooDefaults.MODEL_EMPLOYEES, FILTER_NAMES)) {
                HashMap<String, Object> employee = castToHashMap(employeeObject);
                String name = (String) employee.get(OdooDefaults.FIELD_NAME);
                System.out.println(name);
                allEmployeeNames.add(name);
            }

            return allEmployeeNames;
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, OdooDefaults.ERP_SYSTEM_NAME);
        }
    }

    @Override
    public List<Employee> getAllEmployees() throws ErpException {
        try {
            ArrayList<Employee> allEmployees = new ArrayList<>();

            for (Object employeeObject : odooConnector.searchAndRead(OdooDefaults.MODEL_EMPLOYEES, FILTER_ALL_DATA)) {
                HashMap<String, Object> employee = castToHashMap(employeeObject);
                allEmployees.add(parseEmployee(employee));
            }

            return allEmployees;
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, OdooDefaults.ERP_SYSTEM_NAME);
        }
    }

    public List<String> getDate() throws ErpException, OdooConnectorException {
        ArrayList<String> allDates = new ArrayList<>();
        for (Object dateObject : odooConnector.searchAndRead(OdooDefaults.MODEL_TASKS, FILTER_ID)) {
            HashMap<String, Object> date = castToHashMap(dateObject);
            int id = (int) date.get(OdooDefaults.FIELD_ID);
            System.out.println(id);

        }
        return allDates;
    }

    public List<Rsz> testFilterWorkOrdersOnDate() throws ErpException, ErpException, MarcellekeSystemException {

        Object[] requestedFields = {
            OdooDefaults.FIELD_ID,
            OdooDefaults.FIELD_NAME,
            OdooDefaults.FIELD_DATE,
            OdooDefaults.FIELD_TASK_ID,
            OdooDefaults.FIELD_DONE_BY
        };

        List<Rsz> tasks = new ArrayList<>();

        System.out.println("Output of date:" + new Date().toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(new Date());
        System.out.println(dateString);
        Object[] query = {
            asList(OdooDefaults.FIELD_DATE, ">", dateString + " 00:00:00")
        };
        int counter = 0;
        try {
            Object[] result = odooConnector.searchAndRead(OdooDefaults.MODEL_WORK_ORDERS, query, requestedFields);
            System.out.println("ODOO Aantal matches: " + result.length);
            for (Object o : result) {
                try {
                    Rsz r = parseWork(o);
                    if (!r.getInss().isEmpty()) {
                        tasks.add(r);
                    }
                    counter++;
                } catch (OdooException ex) {
                    Logger.getLogger(OdooERPConnector.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (OdooConnectorException ex) {
            Logger.getLogger(OdooERPConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Counter is " + counter);

        return tasks;
    }

    public int getSizeSchedule() throws ErpException, ErpException, MarcellekeSystemException {

        Object[] requestedFields = {
            OdooDefaults.FIELD_ID,
            OdooDefaults.FIELD_NAME,
            OdooDefaults.FIELD_DATE,
            OdooDefaults.FIELD_TASK_ID,
            OdooDefaults.FIELD_DONE_BY
        };

        List<Rsz> tasks = new ArrayList<>();

        System.out.println("Output of date:" + new Date().toString());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(new Date());
        System.out.println(dateString);
        Object[] query = {
            asList(OdooDefaults.FIELD_DATE, ">", dateString + " 00:00:00")
        };
        int size = 0;
        try {
            Object[] result = odooConnector.searchAndRead(OdooDefaults.MODEL_WORK_ORDERS, query, requestedFields);
            size = result.length;
        } catch (OdooConnectorException ex) {
            Logger.getLogger(OdooERPConnector.class.getName()).log(Level.SEVERE, null, ex);
        }

        return size;
    }

    /*@Override
    public String getEmployeeByErpId(int employeeId) throws ErpException {   
        Object[] query = {
            asList(OdooDefaults.FIELD_ID, "=", employeeId)
        }; 
        return getEmployee(query);
    }*/
    public String getIdentifIDByName(String employeeName) throws ErpException {
        Object[] query = {
            asList(OdooDefaults.FIELD_NAME, "=", employeeName)
        };
        return getEmployee(query);
    }

//    public String getTaskByDate(String employeeName) throws ErpException {     
//        Object[] query = {
//            asList(OdooDefaults., "=", employeeName)
//        };
//        return getEmployee(query); 
//    }
    public String getCheckInAtWorkByProjectID(int projectID) throws ErpException {
        Object[] query = {
            asList(OdooDefaults.FIELD_ID, "=", projectID)
        };
        return parseCheckInAtWorkId(query);
    }

    public String getCheckInAtWorkByTask_ID(int taskid) throws OdooException {
        Task searchedTask = null;
        Object[] requestedFields = {
            OdooDefaults.FIELD_PROJECT_ID,
            OdooDefaults.FIELD_SALE_ORDER,
            OdooDefaults.FIELD_ID,
            OdooDefaults.FIELD_NAME

        };

        Object[] query = {
            asList(OdooDefaults.FIELD_ID, "=", taskid)
        };
        try {

            Object[] result = odooConnector.searchAndRead(OdooDefaults.MODEL_TASKS, query, requestedFields);
            // System.out.println("Aantal matches: " + result.length);
            for (Object o : result) {

                //  System.out.println("WORK ORDER:" + o.toString());
                HashMap<String, Object> task = castToHashMap(o);

                searchedTask = parseTask(task);

            }
            if (searchedTask == null) {
                return "";
            }
            //System.out.println("saleorder" + searchedTask.toString());
            //  System.out.println("saleorder" + searchedTask.getSaleOrderID());
        } catch (OdooConnectorException ex) {
            Logger.getLogger(OdooERPConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        return getCheckInAtWorkbySale_ID(searchedTask.getSaleOrderID());

    }

    public String getCheckInAtWorkbySale_ID(int saleid) throws OdooException {
        SaleOrder searchedSaleOrder = null;
        Object[] requestedFields = {
            OdooDefaults.FIELD_PROJECT_ID,
            OdooDefaults.FIELD_STATUS,
            OdooDefaults.FIELD_SHIPPING_ID,
            OdooDefaults.FIELD_CHECK_IN_AT_WORK,
            OdooDefaults.FIELD_COUNTRY

        };

        Object[] query = {
            asList(OdooDefaults.FIELD_ID, "=", saleid)
        };
        try {

            Object[] result = odooConnector.searchAndRead(OdooDefaults.MODEL_SALE_ORDERS, query, requestedFields);
            //System.out.println("Aantal matches sales: " + result.length);
            for (Object o : result) {

                //  System.out.println("WORK ORDER:" + o.toString());
                HashMap<String, Object> saleOrder = castToHashMap(o);

                searchedSaleOrder = parseSaleOrder(saleOrder);
            }

        } catch (OdooConnectorException ex) {
            Logger.getLogger(OdooERPConnector.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (searchedSaleOrder == null) {
            //System.out.println("No Sale Order found");
            return "";
        }
        //System.out.println("saleorder" + searchedSaleOrder.toString());
        //System.out.println("CheckInAtWorkNumber= " + searchedSaleOrder.getCheckInAtWorkNumber());
        return searchedSaleOrder.getCheckInAtWorkNumber();
    }

    private String getEmployee(Object[] query) throws ErpException {
        try {
            Employee searchedForEmployee = null;
            Object[] result = odooConnector.searchAndRead(OdooDefaults.MODEL_EMPLOYEES, query, FILTER_ALL_DATA);
            //System.out.println("Aantal matches: " + result.length);

            for (Object employeeObject : result) {
                //System.out.println("Employee:" + employeeObject.toString());
                HashMap<String, Object> employee = castToHashMap(employeeObject);
                searchedForEmployee = parseEmployee(employee);
                //System.out.println("Employee Searched:" + searchedForEmployee.toString());
            }

            if (searchedForEmployee == null) {
                return "";
            }

            searchedForEmployee.setUserID(getUserIdForEmployee(searchedForEmployee.getErpId()));
            //System.out.println("identifID = " + searchedForEmployee.getidentifID());
            return searchedForEmployee.getidentifID();
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, OdooDefaults.ERP_SYSTEM_NAME);
        }
    }

    private ArrayList<SaleOrder> getSaleOrders(int[] requestedIDs) throws ErpException {
        try {
            Object[] saleOrderObjects = odooConnector.read(OdooDefaults.MODEL_SALE_ORDERS, requestedIDs, SaleOrder.FIELDS);
            return listSalesOrders(saleOrderObjects);
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, OdooDefaults.ERP_SYSTEM_NAME);
        }
    }

    private ArrayList<SaleOrder> getSaleOrders(Object[] query) throws ErpException {
        try {
            Object[] saleOrderObjects = odooConnector.searchAndRead(OdooDefaults.MODEL_SALE_ORDERS, query, SaleOrder.FIELDS);
            return listSalesOrders(saleOrderObjects);
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, OdooDefaults.ERP_SYSTEM_NAME);
        }
    }

    public ArrayList<SaleOrder> listSalesOrders(Object[] saleOrderObjects) {
        ArrayList<SaleOrder> allSaleOrders = new ArrayList<>();
        for (Object saleOrderObject : saleOrderObjects) {
            SaleOrder saleOrder = parseSaleOrder(saleOrderObject);
            boolean inList = inList(allSaleOrders, saleOrder);
            if (!inList) {
                allSaleOrders.add(saleOrder);
                System.out.println(saleOrder.toString());
            }
        }
        return allSaleOrders;
    }
//    

    private int getUserIdForEmployee(int employeeId) throws ErpException {

        try {
            int userID = -1;

            Object[] requestedFields = {
                OdooDefaults.FIELD_ID,
                OdooDefaults.FIELD_RELATED_EMPLOYEES_IDS
            };

            Object[] results = odooConnector.searchAndRead(OdooDefaults.MODEL_USERS, requestedFields);
            if (results == null) {
                throw new OdooException("We could not find any users for employee with ID: " + employeeId);
            }

            boolean found = false;
            for (Object userObject : results) {

                HashMap<String, Object> user = castToHashMap(userObject);

                userID = (int) user.get(OdooDefaults.FIELD_ID);

                if (!(user.get(OdooDefaults.FIELD_RELATED_EMPLOYEES_IDS) instanceof Boolean)) {

                    Object[] relatedEmployeeIDsObject = (Object[]) user.get(OdooDefaults.FIELD_RELATED_EMPLOYEES_IDS);

                    for (Object singleRelatedEmployeeIdObject : relatedEmployeeIDsObject) {
                        int relatedID = (int) singleRelatedEmployeeIdObject;
                        if (relatedID == employeeId) {
                            found = true;
                            userID = (int) user.get(OdooDefaults.FIELD_ID);
                            break;
                        }
                        if (found) {
                            break;
                        }
                    }
                }
                if (found) {
                    break;
                }
            }
            return userID;
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, OdooDefaults.ERP_SYSTEM_NAME);
        }
    }

    private ArrayList<Task> getTasksForEmployee(int userId) throws ErpException {
        try {
            ArrayList<Task> tasksForEmployee = new ArrayList<>();

            Object[] requestedFields = {
                OdooDefaults.FIELD_ID,
                OdooDefaults.FIELD_NAME,
                OdooDefaults.FIELD_PROJECT_ID,
                OdooDefaults.FIELD_SALE_ORDER
            };

            Object[] filter = {
                asList(OdooDefaults.FIELD_USER_ID, "=", userId),
                asList(OdooDefaults.FIELD_ACTIVE, "=", true)
            };

            Object[] tasksForUser = odooConnector.searchAndRead(OdooDefaults.MODEL_TASKS, filter, requestedFields);

            if (tasksForUser.length == 0) {
                throw new OdooException("No tasks found for this employee. (User ID: " + userId + ")");
            }

            for (Object taskObject : tasksForUser) {
                Task task = parseTask(taskObject);
                boolean inList = inList(tasksForEmployee, task);
                if (!inList) {
                    tasksForEmployee.add(task);
                }
            }
            return tasksForEmployee;
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, OdooDefaults.ERP_SYSTEM_NAME);
        }
    }

    private boolean inList(ArrayList<? extends ERPObject> allObjectsInList, ERPObject object) {
        boolean inList = false;
        for (ERPObject o : allObjectsInList) {
            if ((o.getID() == object.getID())) {
                inList = true;
                break;
            }
        }
        return inList;
    }

    private SaleOrder parseSaleOrder(Object saleOrderObject) {
        HashMap<String, Object> locationHM = (HashMap<String, Object>) saleOrderObject;
        SaleOrder saleOrder = new SaleOrder();
        if (!(locationHM.get(OdooDefaults.FIELD_PROJECT_ID) instanceof Boolean)) {
            Object[] projectObject = (Object[]) locationHM.get(OdooDefaults.FIELD_PROJECT_ID);
            if (!(projectObject[1].toString().toLowerCase().equals("offerte"))) {
                saleOrder.setStatus(locationHM.get(OdooDefaults.FIELD_STATUS).toString());
                if (saleOrder.getStatus().equals(OdooDefaults.PROJECT_STATUS_PROGRESS) || saleOrder.getStatus().equals(OdooDefaults.PROJECT_STATUS_MANUAL)) {
                    saleOrder.setName(projectObject[1].toString());
                    saleOrder.setID((int) projectObject[0]);
                    if (!(locationHM.get(OdooDefaults.FIELD_CHECK_IN_AT_WORK) instanceof Boolean)) {
                        saleOrder.setCheckInAtWorkNumber((String) locationHM.get(OdooDefaults.FIELD_CHECK_IN_AT_WORK));
                    } else {
                        saleOrder.setCheckInAtWorkNumber("");
                    }
                    saleOrder.setShippingID(0);
                    if (!(locationHM.get(OdooDefaults.FIELD_SHIPPING_ID) instanceof Boolean)) {
                        Object[] shippingID = (Object[]) locationHM.get(OdooDefaults.FIELD_SHIPPING_ID);
                        saleOrder.setShippingID((int) shippingID[0]);
                    }
                }
            }
        }

        return saleOrder;
    }

    private String parseCheckInAtWorkId(Object saleOrderObject) {
        HashMap<String, Object> locationHM = (HashMap<String, Object>) saleOrderObject;
        SaleOrder saleOrder = new SaleOrder();
        if (!(locationHM.get(OdooDefaults.FIELD_PROJECT_ID) instanceof Boolean)) {
            Object[] projectObject = (Object[]) locationHM.get(OdooDefaults.FIELD_PROJECT_ID);
            if (!(projectObject[1].toString().toLowerCase().equals("offerte"))) {
                saleOrder.setStatus(locationHM.get(OdooDefaults.FIELD_STATUS).toString());
                if (saleOrder.getStatus().equals(OdooDefaults.PROJECT_STATUS_PROGRESS)) {
                    saleOrder.setName(projectObject[1].toString());
                    saleOrder.setID((int) projectObject[0]);
                    if (!(locationHM.get(OdooDefaults.FIELD_CHECK_IN_AT_WORK) instanceof Boolean)) {
                        saleOrder.setCheckInAtWorkNumber((String) locationHM.get(OdooDefaults.FIELD_CHECK_IN_AT_WORK));
                    } else {
                        saleOrder.setCheckInAtWorkNumber("");
                    }
                    saleOrder.setShippingID(0);
                    if (!(locationHM.get(OdooDefaults.FIELD_SHIPPING_ID) instanceof Boolean)) {
                        Object[] shippingID = (Object[]) locationHM.get(OdooDefaults.FIELD_SHIPPING_ID);
                        saleOrder.setShippingID((int) shippingID[0]);
                    }
                }
            }
        }
        System.out.println(saleOrder.toString());
        return saleOrder.getCheckInAtWorkNumber();
    }

    private Employee parseEmployee(Object employeeObject) throws ErpException {
        HashMap<String, Object> employeeMap = castToHashMap(employeeObject);
        //TODO: If other data need to be retrieved for employees, add it here.
        int ID = (int) employeeMap.get(OdooDefaults.FIELD_ID);
        String name = (String) employeeMap.get(OdooDefaults.FIELD_NAME);

        //TODO: if this is not activated, throws nullpointer.
        boolean isAManager = (boolean) employeeMap.get(OdooDefaults.FIELD_IS_A_MANAGER);

        String workEmail = "none";
        if (!(employeeMap.get(OdooDefaults.FIELD_WORK_EMAIL) instanceof Boolean)) {
            workEmail = (String) employeeMap.get(OdooDefaults.FIELD_WORK_EMAIL);
        }

        String managerID = "none";
        if (!(employeeMap.get(OdooDefaults.FIELD_MANAGER_ID) instanceof Boolean)) {
            Object[] managers = (Object[]) employeeMap.get(OdooDefaults.FIELD_MANAGER_ID);
            for (Object manager : managers) {
                managerID += manager + ",";
            }
        }

        String identificationID = "";
        if (!(employeeMap.get(OdooDefaults.FIELD_IDENTIF_ID) instanceof Boolean)) {
            identificationID = (String) employeeMap.get(OdooDefaults.FIELD_IDENTIF_ID);
        }

        return new Employee(name, ID, getUserIdForEmployee(ID), workEmail, isAManager, managerID, identificationID);
    }

    private Task parseTask(Object taskObject) throws OdooException {
        HashMap<String, Object> taskMap = castToHashMap(taskObject);
        //System.out.println(taskMap.toString());
        Task newTask = new Task();

        newTask.setID((int) taskMap.get(OdooDefaults.FIELD_ID));
        newTask.setDescription(taskMap.get(OdooDefaults.FIELD_NAME).toString());

        if (!(taskMap.get(OdooDefaults.FIELD_PROJECT_ID) instanceof Boolean)) {
            Object[] projectObject = (Object[]) taskMap.get(OdooDefaults.FIELD_PROJECT_ID);
            newTask.setProjectID((int) projectObject[0]);
            newTask.setName(projectObject[1].toString());
        }

        if (!(taskMap.get(OdooDefaults.FIELD_SALE_ORDER) instanceof Boolean)) {
            Object[] saleOrderObject = (Object[]) taskMap.get(OdooDefaults.FIELD_SALE_ORDER);
            newTask.setSaleOrderID((int) saleOrderObject[0]);
        }
        // System.out.println(newTask.toString());
        return newTask;
    }

    private Rsz parseWork(Object workObject) throws OdooException, ErpException, MarcellekeSystemException {
        HashMap<String, Object> workMap = castToHashMap(workObject);

        Rsz newWork = new Rsz();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = sdf.format(new Date());
        //dateString = "2017-06-01";
        Object date = (Object) workMap.get(OdooDefaults.FIELD_DATE);
        String dateTask = date.toString();
        String[] arrayOfString = dateTask.split("\\s+");
        String shortDateTask = arrayOfString[0];

        if (dateString.equals(shortDateTask)) {
            Object[] task = (Object[]) workMap.get(OdooDefaults.FIELD_TASK_ID);
            int id = (int) task[0];
            if (getCheckInAtWorkByTask_ID(id) == null) {
                newWork.setWorkPlaceId("");
            } else if (getCheckInAtWorkByTask_ID(id).isEmpty()) {
                newWork.setWorkPlaceId("");
            } else if (getCheckInAtWorkByTask_ID(id).equals("nee-_______-__-_")) {
                newWork.setWorkPlaceId("");
            } else if (getCheckInAtWorkByTask_ID(id).equals("NEE-_______-__-_")) {
                newWork.setWorkPlaceId("");
            } else {
                newWork.setWorkPlaceId(getCheckInAtWorkByTask_ID(id));
            }

            Object[] employee = (Object[]) workMap.get(OdooDefaults.FIELD_USER_ID);
            int idEmployee = (int) employee[0];
            String e = getEmployeeByErpId(idEmployee);
            newWork.setInss(e);

            newWork.setCompanyId("873909226");
        } else {
            newWork.setInss("");
        }

        return newWork;
    }

    private HashMap<String, Object> castToHashMap(Object toBeCasted) throws OdooException {
        HashMap<String, Object> casted = new HashMap<>();
        try {
            casted = (HashMap<String, Object>) toBeCasted;
        } catch (ClassCastException castException) {
            throw new OdooException("Error during cast to HashMap, contact system admin.", castException.getMessage());
        }
        return casted;
    }

    /*
     * ODOO SPECIFIC CODE
     */
    private void initializeOdooConnector(boolean ignoreInvalidSSL) throws ErpException {
        try {
            odooConnector = new OdooConnector(protocol, host, port, dbName, dbLogin, dbPassword, ignoreInvalidSSL);
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, OdooDefaults.ERP_SYSTEM_NAME);
        }
    }

    private void authenticate() throws ErpException {
        try {
            odooUserID = odooConnector.authenticate();
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, OdooDefaults.ERP_SYSTEM_NAME);
        }
    }

    private void getVersion() throws ErpException {
        try {
            Object versionInfo = odooConnector.getVersion();
            System.out.println("Connection to ODOO ok. Version: " + versionInfo.toString());
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, OdooDefaults.ERP_SYSTEM_NAME);
        }
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public void setDbLogin(String dbLogin) {
        this.dbLogin = dbLogin;
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDbName() {
        return dbName;
    }

    public String getDbLogin() {
        return dbLogin;
    }

    public String getDbPassword() {
        return dbPassword;
    }
//
//    @Override
//    public List<Employee> getAllEmployees() throws ErpException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Employee getEmployeeByErpId(int employeeId) throws ErpException, MarcellekeSystemException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Employee getEmployeeByName(String employeeName) throws ErpException, MarcellekeSystemException {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    @Override
    public List<Location> getAllLocations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Location> getAllLocationsForEmployee(Employee employee) throws ErpException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Location getLocation(String locationId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getEmployeeByErpId(int employeeId) throws ErpException, MarcellekeSystemException {
        Object[] query = {
            asList(OdooDefaults.FIELD_USER_ID, "=", employeeId)
        };
        return getEmployee(query);
    }

    @Override
    public Employee getEmployeeByName(String employeeName) throws ErpException, MarcellekeSystemException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
