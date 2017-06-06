
package com.arnowouter.marcelleke.ejb.connectors;

import com.arnowouter.marcelleke.ejb.connectors.erpClasses.Employee;
import com.arnowouter.marcelleke.ejb.connectors.erpClasses.Location;
import com.arnowouter.marcelleke.ejb.exceptions.ErpException;
import com.arnowouter.marcelleke.ejb.exceptions.MarcellekeSystemException;
import java.util.List;

/**
 *
 * @author Arno
 * Interface that defines what a connector (to an ERP system) should look like
 */
public interface ErpSpecificConnectorInterface {
    public List<String> getAllEmployeeNames() throws ErpException;
    public List<Employee> getAllEmployees() throws ErpException;
    public String getEmployeeByErpId(int employeeId) throws ErpException, MarcellekeSystemException;
    public Employee getEmployeeByName(String employeeName) throws ErpException, MarcellekeSystemException;
    public List<Location> getAllLocations();
    public List<Location> getAllLocationsForEmployee(Employee employee) throws ErpException;
    public Location getLocation(String locationId);
}
