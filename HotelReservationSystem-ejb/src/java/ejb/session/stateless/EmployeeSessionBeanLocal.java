/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import javax.ejb.Local;
import util.enumeration.StaffRole;
import util.exception.InputDataValidationException;
import util.exception.InvalidEmployeeException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
@Local
public interface EmployeeSessionBeanLocal {

    public Employee createEmployee(String username, String password, StaffRole staffRole) throws InvalidEmployeeException, UnknownPersistenceException, InputDataValidationException;
    
}
