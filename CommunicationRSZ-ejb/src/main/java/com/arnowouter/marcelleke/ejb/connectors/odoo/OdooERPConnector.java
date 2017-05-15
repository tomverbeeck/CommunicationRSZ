/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arnowouter.marcelleke.ejb.connectors.odoo;

import com.arnowouter.javaodoo.OdooConnector;
import com.arnowouter.javaodoo.exceptions.OdooConnectorException;
import com.arnowouter.marcelleke.domain.entities.AddressEntity;
import com.arnowouter.marcelleke.ejb.connectors.ErpSpecificConnectorInterface;
import com.arnowouter.marcelleke.ejb.connectors.erpClasses.ERPObject;
import com.arnowouter.marcelleke.ejb.connectors.erpClasses.Employee;
import com.arnowouter.marcelleke.ejb.connectors.erpClasses.Location;
import com.arnowouter.marcelleke.ejb.connectors.erpClasses.SaleOrder;
import com.arnowouter.marcelleke.ejb.connectors.erpClasses.SalesTeam;
import com.arnowouter.marcelleke.ejb.connectors.erpClasses.Task;
import com.arnowouter.marcelleke.ejb.defaults.ApplicationDefaults;
import com.arnowouter.marcelleke.ejb.exceptions.ErpException;
import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Arno
 */
public class OdooERPConnector implements ErpSpecificConnectorInterface {
    
    OdooConnector odooConnector;
    private int odooUserID;
    //Odoo connection parameters
    private String protocol;
    private String host;
    private int port;
    private String dbName;
    private String dbLogin;
    private String dbPassword;

    
    // PREDEFINED FILTERS
    private final Object[] FILTER_NAMES = {
        OdooDefaults.FIELD_NAME  
    };
    
    private final Object[] FILTER_ALL_DATA = {
        OdooDefaults.FIELD_ID,
        OdooDefaults.FIELD_NAME,
        OdooDefaults.FIELD_WORK_ADDRESS_ID,
        OdooDefaults.FIELD_IS_A_MANAGER,
        OdooDefaults.FIELD_WORK_EMAIL,
        OdooDefaults.FIELD_MANAGER_ID,
        OdooDefaults.FIELD_IDENTIFICATION_ID
    };
            
    public OdooERPConnector(String protocol, String host, int port, String dbName, String dbLogin, String dbPassword) 
            throws ErpException
    {
        this.protocol= protocol;
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.dbLogin = dbLogin;
        this.dbPassword = dbPassword;
        //TODO: watch out! This boolean tells the library to use an UNSECURE client! (Ingores Invalid SSL)
        initializeOdooConnector(true);
        authenticate();
    }
    
    @Override
    public List<String> getAllEmployeeNames() throws ErpException {

        try {
            ArrayList<String> allEmployeeNames = new ArrayList<>();
            
            for (Object employeeObject : odooConnector.searchAndRead(OdooDefaults.MODEL_EMPLOYEES, FILTER_NAMES)) {
                HashMap<String, Object> employee = castToHashMap(employeeObject);
                String name = (String) employee.get(OdooDefaults.FIELD_NAME);
                allEmployeeNames.add(name);
            }
            
            return allEmployeeNames;
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
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
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
        }
    }

    @Override
    public Employee getEmployeeByErpId(int employeeId) throws ErpException {   
        Object[] query = {
            asList(OdooDefaults.FIELD_ID, "=", employeeId)
        }; 
        return getEmployee(query);
    }
    
    @Override
    public Employee getEmployeeByName(String employeeName) throws ErpException {     
        Object[] query = {
            asList(OdooDefaults.FIELD_NAME, "=", employeeName)
        };
        return getEmployee(query); 
    }
    
    private Employee getEmployee(Object[] query) throws ErpException {
        try {
            Employee searchedForEmployee = null;
            
            for(Object employeeObject : odooConnector.searchAndRead(OdooDefaults.MODEL_EMPLOYEES, query, FILTER_ALL_DATA)) {
                HashMap<String, Object> employee = castToHashMap(employeeObject);
                searchedForEmployee = parseEmployee(employee);
            }
            
            if(searchedForEmployee == null) throw new OdooException("No such employee was found.");
            
            searchedForEmployee.setUserID(getUserIdForEmployee(searchedForEmployee.getErpId()));
            
            return searchedForEmployee;
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
        }
    }

    
    @Override
    public Location getLocation(String locationId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<Location> getAllLocations() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public List<Location> getAllLocationsForEmployee(Employee employee) throws ErpException {
        if(employee.getUserID()<=0 ){
            employee.setUserID(getUserIdForEmployee(employee.getErpId()));
        }
        
        ArrayList<Location> locations;
        if(employee.isAManager()) {
            locations = getManagerLocations(employee);
        } else {
            locations = getEmployeeLocations(employee);
        }
        ArrayList<Location> homeAndWorkAddress = getHomeAndWorkAddress(employee.getErpId());
        locations.addAll(homeAndWorkAddress);
        return locations;
    } 
    private ArrayList<Location> getManagerLocations(Employee manager) throws ErpException {
        Object[] requestedFields = {
            OdooDefaults.FIELD_ID,
            OdooDefaults.FIELD_NAME,
            OdooDefaults.FIELD_PROJECT_ID,
            OdooDefaults.FIELD_SALE_ORDER,
            OdooDefaults.FIELD_KANBAN_STATE
        };
        
        Object[] query = {
            asList(OdooDefaults.FIELD_USER_ID, "=", manager.getUserID())            
        };
        ArrayList<Task> tasksForManager = new ArrayList<>();
        try {
            Object[] taskObjectsForManager = odooConnector.searchAndRead(OdooDefaults.MODEL_TASKS, query, requestedFields);
            if(taskObjectsForManager.length==0) throw new OdooException("No tasks found for this manager. (User ID: " + manager.getUserID() +")");
            
            for(Object taskObject : taskObjectsForManager) {
                Task task = parseTask(taskObject);
                boolean inList = inList(tasksForManager, task);
                System.out.println(task.toString());
                if(!inList && task.getState().equals("normal")) {
                    tasksForManager.add(task);
                }
            }
            
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
        }
        return tasksToLocations(tasksForManager);
    }
    
    private ArrayList<Location> OLDgetManagerLocations (Employee manager) throws ErpException {
        ArrayList<Location> locations = new ArrayList<>();
        
        SalesTeam theSalesTeam = findMatchingSalesTeam(manager.getUserID());
        if(theSalesTeam!=null){
            
            Object[] query = {
                asList(OdooDefaults.FIELD_SALES_TEAM, "=", theSalesTeam.getID())
            };
            
            ArrayList<SaleOrder> saleOrdersForSalesTeam = getSaleOrders(query);
            
            int[] shippingIDs = new int[saleOrdersForSalesTeam.size()];
            int i=0;
            for(SaleOrder saleOrder: saleOrdersForSalesTeam){
                shippingIDs[i] = saleOrder.getShippingID();
                i++;
            }
            locations = getLocationsForSalesOrder(shippingIDs, saleOrdersForSalesTeam);
        }
        
        return locations;
    }
    
    private ArrayList<Location> getEmployeeLocations(Employee employee) throws ErpException {
        int[] workOrderIds = getWorkOrdersForEmployee(employee.getUserID());
        ArrayList<Task> tasks = getTasksForEmployee(workOrderIds, employee.getUserID());
        return tasksToLocations(tasks);  
    }
    private ArrayList<Location> tasksToLocations(ArrayList<Task> tasks) throws ErpException {
        int[] saleOrdersIDs = new int[tasks.size()];
        int i=0;
        for(Task task : tasks) {
            saleOrdersIDs[i] = task.getSaleOrderID();
            i++;
        }
        
        ArrayList<SaleOrder> saleOrders = getSaleOrders(saleOrdersIDs);
        int[] shippingIDs = new int[saleOrders.size()];
        i=0;
        for(SaleOrder saleOrder: saleOrders){
            shippingIDs[i] = saleOrder.getShippingID();
            i++;
        }
        
        return getLocationsForSalesOrder(shippingIDs, saleOrders);
    }
    
    private ArrayList<Location> getLocationsForSalesOrder(int[] shippingIDs, ArrayList<SaleOrder> saleOrders) throws ErpException {
        try {
            ArrayList<Location> locations = new ArrayList<>();
            Object[] results = odooConnector.read(OdooDefaults.MODEL_RELATIONS, shippingIDs, Location.FIELDS);
            for(Object result : results ){
                Location location = parseLocation(result);
                saleOrders.stream().filter((saleOrder) -> (saleOrder.getShippingID() == location.getID())).forEachOrdered((saleOrder) -> {
                    location.setName(saleOrder.getName());
                    location.setCheckInAtWorkID(saleOrder.getCheckInAtWorkNumber());
                });
                locations.add(location);
            }
            return locations;
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
        }
    }
    
    private SalesTeam findMatchingSalesTeam(int userID) throws ErpException {
        ArrayList<SalesTeam> salesTeams = getAllSalesTeams();
        SalesTeam theSalesTeam = null;
        for(SalesTeam team : salesTeams){
            if(team.getTeamLeaderID()==userID){
                theSalesTeam = team;
                break;
            }
        }
        return theSalesTeam;
    }
    
    public ArrayList<SalesTeam> getAllSalesTeams() throws ErpException {
        try {
            Object[] requestedFields = {
                OdooDefaults.FIELD_ID,
                OdooDefaults.FIELD_NAME,
                OdooDefaults.FIELD_TEAM_LEADER
            };
            
            Object[] results = odooConnector.searchAndRead(OdooDefaults.MODEL_SALES_TEAM, requestedFields);
            ArrayList<SalesTeam> allSalesTeams = new ArrayList<>();
            for(Object result : results){
                HashMap<String,Object> resultHM = (HashMap<String, Object>) result;
                Object name = resultHM.get(OdooDefaults.FIELD_NAME);
                if(!(name instanceof Boolean)) {
                    SalesTeam salesTeam = new SalesTeam(name.toString());
                    salesTeam.setID((int)resultHM.get(OdooDefaults.FIELD_ID));
                    if(!(resultHM.get(OdooDefaults.FIELD_TEAM_LEADER) instanceof Boolean)){
                        Object[] teamLeader =  (Object[]) resultHM.get(OdooDefaults.FIELD_TEAM_LEADER);
                        salesTeam.setTeamLeaderID((int)teamLeader[0]);
                        salesTeam.setTeamLeaderName(teamLeader[1].toString());
                    }
                    allSalesTeams.add(salesTeam);
                }
            }
            return allSalesTeams;
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
        }
    }
     
    private ArrayList<SaleOrder> getSaleOrders(int[] requestedIDs) throws ErpException {
        try {
            Object[] saleOrderObjects = odooConnector.read(OdooDefaults.MODEL_SALE_ORDERS,requestedIDs, SaleOrder.FIELDS);
            return listSalesOrders(saleOrderObjects);
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
        }
    }
    
    private ArrayList<SaleOrder> getSaleOrders(Object[] query) throws ErpException {               
        try {
            Object[] saleOrderObjects = odooConnector.searchAndRead(OdooDefaults.MODEL_SALE_ORDERS, query, SaleOrder.FIELDS);
            return listSalesOrders(saleOrderObjects);
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
        }
    }
    
    private ArrayList<SaleOrder> listSalesOrders(Object[] saleOrderObjects) {
        ArrayList<SaleOrder> allSaleOrders = new ArrayList<>();
        for(Object saleOrderObject : saleOrderObjects) {
            SaleOrder saleOrder = parseSaleOrder(saleOrderObject);
            boolean inList = inList(allSaleOrders, saleOrder);
            if(!inList && !(saleOrder.getName().equals("offerte"))){
                allSaleOrders.add(saleOrder);   
            }
        }
        return allSaleOrders;
    }
    
    private int getUserIdForEmployee(int employeeId) throws ErpException {

        try {
            int userID = -1;
            
            Object[] requestedFields = {
                OdooDefaults.FIELD_ID,
                OdooDefaults.FIELD_RELATED_EMPLOYEES_IDS
            };
            
            Object[] results = odooConnector.searchAndRead(OdooDefaults.MODEL_USERS,requestedFields);
            if(results == null) throw new OdooException("We could not find any users for employee with ID: " + employeeId);
            
            boolean found=false;
            for(Object userObject : results) {
                
                HashMap<String,Object> user = castToHashMap(userObject);
                
                userID = (int) user.get(OdooDefaults.FIELD_ID);
                
                if(!(user.get(OdooDefaults.FIELD_RELATED_EMPLOYEES_IDS) instanceof Boolean)){
                    
                    Object[] relatedEmployeeIDsObject = (Object[]) user.get(OdooDefaults.FIELD_RELATED_EMPLOYEES_IDS);
                    
                    for(Object singleRelatedEmployeeIdObject : relatedEmployeeIDsObject) {
                        int relatedID = (int)singleRelatedEmployeeIdObject;
                        if(relatedID == employeeId){
                            found=true;
                            userID = (int) user.get(OdooDefaults.FIELD_ID);
                            break;
                        }
                        if(found) break;
                    }
                }
                if(found) break;
            }
            return userID;
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
        }
    }
    
    private int[] getWorkOrdersForEmployee(int userId) throws ErpException {
        Object[] requestedFields = {
            OdooDefaults.FIELD_ID,
            OdooDefaults.FIELD_TASK_ID,
            OdooDefaults.FIELD_TASK_SUMMARY,
            OdooDefaults.FIELD_LAST_UPDATED_ON,
            OdooDefaults.FIELD_WORK_ORDER
        };
        
        Object[] query = {
            asList(OdooDefaults.FIELD_DONE_BY, "=", userId)
        };
        
        int[] taskIDs=null;
        int i=0;
        try {
            Object[] workOrdersForEmployee = odooConnector.searchAndRead(OdooDefaults.MODEL_WORK_ORDERS, query, requestedFields);
            taskIDs = new int[workOrdersForEmployee.length];
            for(Object o : workOrdersForEmployee) {
                HashMap<String,Object> workOrders = castToHashMap(o);
                if(!(workOrders.get(OdooDefaults.FIELD_TASK_ID) instanceof Boolean)){
                    Object[] task = (Object[]) workOrders.get(OdooDefaults.FIELD_TASK_ID);
                    taskIDs[i] = (int) task[0];
                }
                i++;
            }
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
        }
        
        ArrayList<Integer> taskIDsArrayList = new ArrayList<>();
        for(int j=0;j<taskIDs.length;j++){
            boolean found=false;
            for(Integer integer : taskIDsArrayList){
                if(taskIDs[j]==integer){
                    found=true;
                    break;
                }
                if(found) break;
            }
            if(!found) taskIDsArrayList.add(taskIDs[j]);
        }

        int[] ids = new int[taskIDsArrayList.size()];
        i=0;
        for(Integer integer : taskIDsArrayList){
            ids[i] = integer;
            i++;
        }
            
        return ids;
    }
    
    private ArrayList<Task> getTasksForEmployee(int[] ids, int userId) throws ErpException {

        ArrayList<Task> tasksForEmployee = new ArrayList<>();

        Object[] requestedFields = {
            OdooDefaults.FIELD_ID,
            OdooDefaults.FIELD_NAME,
            OdooDefaults.FIELD_PROJECT_ID,
            OdooDefaults.FIELD_SALE_ORDER,
            OdooDefaults.FIELD_KANBAN_STATE
        };

        Object[] tasksForUser;
        try {
            tasksForUser = odooConnector.read(OdooDefaults.MODEL_TASKS, ids, requestedFields);
            
            if(tasksForUser.length==0) throw new OdooException("No tasks found for this employee. (User ID: " + userId +")");
            
            for(Object taskObject : tasksForUser) {
                Task task = parseTask(taskObject);
                boolean inList = inList(tasksForEmployee, task);
                //TODO: a lot of the code of this part and the managerlocations part is the same. 
                // REFACTOR
                System.out.println(task.toString());
                if(!inList && task.getState().equals("normal")) {
                    tasksForEmployee.add(task);
                }
            }
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
        }
        return tasksForEmployee;
    }
    
    private boolean inList(ArrayList<? extends ERPObject> allObjectsInList, ERPObject object){
        boolean inList = false;
        for(ERPObject o : allObjectsInList) {
            if((o.getID() == object.getID())) {
                inList = true;
                break;
            }
        }
        return inList;
    }

    private ArrayList<Location> getHomeAndWorkAddress(int employeeId) throws ErpException {
        try {
            ArrayList<Location> homeAndWorkAddressesArrayList = new ArrayList<>();
            
            Object[] modelEmployeesRequestedFields = {
                OdooDefaults.FIELD_HOME_ADDRESS_ID,
                OdooDefaults.FIELD_WORK_ADDRESS_ID
            };
            
            int[] requestedIDs = new int[1];
            requestedIDs[0] = employeeId;
            
            Object[] result = odooConnector.read(OdooDefaults.MODEL_EMPLOYEES,requestedIDs,modelEmployeesRequestedFields);
            for(Object personalAddresses : result) {
                HashMap<String,Object> homeAndWorkaddressesMap= castToHashMap(personalAddresses);
                if(!(homeAndWorkaddressesMap.get(OdooDefaults.FIELD_HOME_ADDRESS_ID) instanceof Boolean)){
                    Object[] homeAddressObject = (Object[]) homeAndWorkaddressesMap.get(OdooDefaults.FIELD_HOME_ADDRESS_ID);
                    Location homeAddress = getAddress((int)homeAddressObject[0]);
                    homeAddress.setName(OdooDefaults.HOME_ADDRESS);
                    homeAndWorkAddressesArrayList.add(homeAddress);
                }
                
                if(!(homeAndWorkaddressesMap.get(OdooDefaults.FIELD_WORK_ADDRESS_ID) instanceof Boolean)){
                    Object[] workAddressObject = (Object[]) homeAndWorkaddressesMap.get(OdooDefaults.FIELD_WORK_ADDRESS_ID);
                    Location workAddress = getAddress((int)workAddressObject[0]);
                    workAddress.setName(OdooDefaults.WORK_ADDRESS);
                    homeAndWorkAddressesArrayList.add(workAddress);
                }
            }
            return homeAndWorkAddressesArrayList;
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
        }
    }
    
    private Location getAddress(int id) throws ErpException { 
        try {
            Location newLocation = new Location();  
            int[] requestedIDs = new int[1];
            requestedIDs[0] = id;
            
            Object[] result = odooConnector.read(OdooDefaults.MODEL_RELATIONS,requestedIDs,Location.FIELDS);
            if(result != null) {
                for(Object addressObject : result) {
                    newLocation = parseLocation(addressObject);
                }
            }
            return newLocation;
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
        }
    }
    
    private SaleOrder parseSaleOrder(Object saleOrderObject){
        HashMap<String, Object> locationHM = (HashMap<String, Object>) saleOrderObject;
        SaleOrder saleOrder = new SaleOrder();
        if(!(locationHM.get(OdooDefaults.FIELD_PROJECT_ID) instanceof Boolean)){
            Object[] projectObject = (Object[]) locationHM.get(OdooDefaults.FIELD_PROJECT_ID);
            saleOrder.setName(projectObject[1].toString());
            saleOrder.setID((int)projectObject[0]);
        }
        
        saleOrder.setStatus(locationHM.get(OdooDefaults.FIELD_STATUS).toString());

        if(!(locationHM.get(OdooDefaults.FIELD_CHECK_IN_AT_WORK) instanceof Boolean)){
            saleOrder.setCheckInAtWorkNumber((String)locationHM.get(OdooDefaults.FIELD_CHECK_IN_AT_WORK));
        }
        saleOrder.setShippingID(0);
        if(!(locationHM.get(OdooDefaults.FIELD_SHIPPING_ID)instanceof Boolean)){
            Object[] shippingID = (Object[])locationHM.get(OdooDefaults.FIELD_SHIPPING_ID);
            saleOrder.setShippingID((int)shippingID[0]);
        } 
        
        return saleOrder;
    }
    
    private Employee parseEmployee(Object employeeObject ) throws ErpException {
        HashMap<String,Object> employeeMap = castToHashMap(employeeObject);
        //TODO: If other data need to be retrieved for employees, add it here.
        int ID = (int)employeeMap.get(OdooDefaults.FIELD_ID);
        String name = (String) employeeMap.get(OdooDefaults.FIELD_NAME);

        //TODO: if this is not activated, throws nullpointer.
        boolean isAManager = (boolean) employeeMap.get(OdooDefaults.FIELD_IS_A_MANAGER);

        String workEmail="none";
        if(!(employeeMap.get(OdooDefaults.FIELD_WORK_EMAIL) instanceof Boolean)){
            workEmail = (String) employeeMap.get(OdooDefaults.FIELD_WORK_EMAIL);
        }

        String managerID = "none";
        if(!(employeeMap.get(OdooDefaults.FIELD_MANAGER_ID) instanceof Boolean)){
            Object[] managers = (Object[]) employeeMap.get(OdooDefaults.FIELD_MANAGER_ID);
            for(Object manager : managers) {
                managerID += manager +",";
            }
        }
        
        String identificationID = "none";
        if(!(employeeMap.get(OdooDefaults.FIELD_IDENTIFICATION_ID) instanceof Boolean)){
            identificationID = (String) employeeMap.get(OdooDefaults.FIELD_IDENTIFICATION_ID);
        }
        
        return new Employee(name, ID, getUserIdForEmployee(ID), workEmail, isAManager, managerID, identificationID);
    }
    
    private Task parseTask(Object taskObject) throws OdooException {
        HashMap<String, Object> taskMap = castToHashMap(taskObject);
        
        Task newTask = new Task();
        
        newTask.setID((int)taskMap.get(OdooDefaults.FIELD_ID));
        newTask.setDescription(taskMap.get(OdooDefaults.FIELD_NAME).toString());

        if (!(taskMap.get(OdooDefaults.FIELD_PROJECT_ID) instanceof Boolean)) {
            Object[] projectObject = (Object[]) taskMap.get(OdooDefaults.FIELD_PROJECT_ID);
            newTask.setProjectID((int) projectObject[0]);
            newTask.setName(projectObject[1].toString());
        }
        
        if(!(taskMap.get(OdooDefaults.FIELD_KANBAN_STATE) instanceof Boolean)){
            newTask.setState((String)taskMap.get(OdooDefaults.FIELD_KANBAN_STATE));
        }
        //TODO: this field has to be added by an Odoo admin to the project.task model
        if(!(taskMap.get(OdooDefaults.FIELD_SALE_ORDER) instanceof Boolean)){
            Object[] saleOrderObject = (Object[]) taskMap.get(OdooDefaults.FIELD_SALE_ORDER);
            newTask.setSaleOrderID((int)saleOrderObject[0]);
        }
        
        return newTask;
    }
    
    private Location parseLocation(Object locationObject) throws ErpException {
        HashMap<String, Object> locationMap = castToHashMap(locationObject);
        String street = new String();
        String city = new String();
        String zip = new String();
        String country = new String();
        
        double latitude = 1.1;
        double longitude = 1.1;
        
        int ID = (int) locationMap.get(OdooDefaults.FIELD_ID);
        if(!(locationMap.get(OdooDefaults.FIELD_STREET)instanceof Boolean)){
            street = (String)locationMap.get(OdooDefaults.FIELD_STREET);
        }
        if(!(locationMap.get(OdooDefaults.FIELD_CITY)instanceof Boolean)){
            city =(String) locationMap.get(OdooDefaults.FIELD_CITY);
        }
        if(!(locationMap.get(OdooDefaults.FIELD_ZIP)instanceof Boolean)){
            zip =(String) locationMap.get(OdooDefaults.FIELD_ZIP);
        }
        if(!(locationMap.get(OdooDefaults.FIELD_COUNTRY)instanceof Boolean)){
            Object[] c = (Object[])locationMap.get(OdooDefaults.FIELD_COUNTRY);
            country = c[1].toString();
        }
        if(!(locationMap.get(OdooDefaults.FIELD_LATITUDE)instanceof Boolean)){
            latitude = (double) locationMap.get(OdooDefaults.FIELD_LATITUDE);
            longitude = (double) locationMap.get(OdooDefaults.FIELD_LONGITUDE);
        }

        AddressEntity address = new AddressEntity(country,city,zip,street);
        Location location = new Location("",ID,address,latitude,longitude,true);
        return location;
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
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
        }
    }
    
    private void authenticate() throws ErpException {
        try {
            odooUserID = odooConnector.authenticate();
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
        }
    }
    
    private void getVersion() throws ErpException {
        try {
            Object versionInfo = odooConnector.getVersion();
            System.out.println("Connection to ODOO ok. Version: " + versionInfo.toString());
        } catch (OdooConnectorException ex) {
            throw new ErpException(ex.getMessage(), ex, ApplicationDefaults.ERP_ODOO);
        }      
    }
    
    public void setProtocol(String protocol) {this.protocol = protocol;}
    public void setHost(String host) {this.host = host;}
    public void setPort(int port) {this.port = port;}
    public void setDbName(String dbName) {this.dbName = dbName;}
    public void setDbLogin(String dbLogin) {this.dbLogin = dbLogin;}
    public void setDbPassword(String dbPassword) {this.dbPassword = dbPassword;}

    public String getProtocol() {return protocol;}
    public String getHost() {return host;}
    public int getPort() {return port;}
    public String getDbName() {return dbName;}
    public String getDbLogin() {return dbLogin;}
    public String getDbPassword() {return dbPassword;}

} 
