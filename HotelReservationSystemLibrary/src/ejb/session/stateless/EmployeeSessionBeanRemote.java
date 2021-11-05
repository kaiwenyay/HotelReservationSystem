/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Remote;
import util.enumeration.StaffRole;
import util.exception.InvalidCredentialsException;
import util.exception.InvalidEmployeeException;

/**
 *
 * @author kwpwn
 */
@Remote
public interface EmployeeSessionBeanRemote {

    public Employee retrieveEmployeeByUsername(String username);

    public Employee employeeLogin(String username, String password) throws InvalidEmployeeException, InvalidCredentialsException;
    
    public Employee createEmployee(String username, String password, StaffRole staffRole) throws InvalidEmployeeException;

    public List<Employee> retrieveAllEmployees();
    
}
