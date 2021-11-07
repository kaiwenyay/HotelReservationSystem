/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import util.enumeration.StaffRole;
import util.exception.InputDataValidationException;
import util.exception.InvalidCredentialsException;
import util.exception.InvalidEmployeeException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class EmployeeSessionBeanTest {
    
    private static EmployeeSessionBeanRemote employeeSessionBean;
    
    public EmployeeSessionBeanTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        employeeSessionBean = lookupSessionBean();
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of createEmployee method, of class EmployeeSessionBean.
     */
    @Test
    public void test01CreateEmployee() throws InvalidEmployeeException, UnknownPersistenceException, InputDataValidationException {
        String username = "sysadmin";
        String password = "password";
        StaffRole staffRole = StaffRole.ADMIN;
        Employee result = employeeSessionBean.createEmployee(username, password, staffRole);
        assertEquals(1l, result.getUserId().longValue());
    }
    
    @Test(expected = InvalidEmployeeException.class)
    public void test02CreateEmployee() throws InvalidEmployeeException, UnknownPersistenceException, InputDataValidationException {
        String username = "sysadmin";
        String password = "password";
        StaffRole staffRole = StaffRole.ADMIN;
        Employee result = employeeSessionBean.createEmployee(username, password, staffRole);
        assertEquals(1l, result.getUserId().longValue());
    }
    
    @Test(expected = InputDataValidationException.class)
    public void test03CreateEmployee() throws InvalidEmployeeException, UnknownPersistenceException, InputDataValidationException {
        String username = "test";
        String password = "password";
        StaffRole staffRole = null;
        Employee result = employeeSessionBean.createEmployee(username, password, staffRole);
        assertEquals(1l, result.getUserId().longValue());
    }
    
    /**
     * Test of retrieveEmployeeByUsername method, of class EmployeeSessionBean.
     */
    @Test
    public void test04RetrieveEmployeeByUsername() throws InvalidEmployeeException {
        String username = "sysadmin";
        Employee result = employeeSessionBean.retrieveEmployeeByUsername(username);
        assertEquals(username, result.getUsername());
    }
    
    @Test(expected = InvalidEmployeeException.class)
    public void test05RetrieveEmployeeByUsername() throws InvalidEmployeeException {
        String username = "invalid";
        Employee result = employeeSessionBean.retrieveEmployeeByUsername(username);
        assertEquals(username, result.getUsername());
    }
    
    /**
     * Test of employeeLogin method, of class EmployeeSessionBean.
     */
    @Test
    public void test06EmployeeLogin() throws InvalidEmployeeException, InvalidCredentialsException {
        String username = "sysadmin";
        String password = "password";
        Employee result = employeeSessionBean.employeeLogin(username, password);
        assertEquals(username, result.getUsername());
    }
    
    @Test(expected = InvalidEmployeeException.class)
    public void test07EmployeeLogin() throws InvalidEmployeeException, InvalidCredentialsException {
        String username = "invalid";
        String password = "password";
        Employee result = employeeSessionBean.employeeLogin(username, password);
        assertEquals(username, result.getUsername());
    }
    
    @Test(expected = InvalidCredentialsException.class)
    public void test08EmployeeLogin() throws InvalidEmployeeException, InvalidCredentialsException {
        String username = "sysadmin";
        String password = "pw";
        Employee result = employeeSessionBean.employeeLogin(username, password);
        assertEquals(username, result.getUsername());
    }

    /**
     * Test of retrieveAllEmployees method, of class EmployeeSessionBean.
     */
    @Test
    public void test09RetrieveAllEmployees() throws Exception {
        List<Employee> result = employeeSessionBean.retrieveAllEmployees();
        assertEquals(1, result.size());
    }
    
    private static EmployeeSessionBeanRemote lookupSessionBean() {
        try  {
            Context c = new InitialContext();
            
            return (EmployeeSessionBeanRemote) c.lookup("java:global/HotelReservationSystem/HotelReservationSystem-ejb/EmployeeSessionBean!ejb.session.stateless.EmployeeSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
    
}
