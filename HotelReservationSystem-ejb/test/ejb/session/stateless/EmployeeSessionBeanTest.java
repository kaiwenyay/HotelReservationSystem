/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import util.exception.InvalidCredentialsException;
import util.exception.InvalidEmployeeException;

/**
 *
 * @author kwpwn
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)

public class EmployeeSessionBeanTest {
    
    private final EmployeeSessionBeanRemote employeeSessionBean = lookupEmployeeSessionBeanRemote();
    
    public EmployeeSessionBeanTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
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
     * Test of retrieveEmployeeByUsername method, of class EmployeeSessionBean.
     */
    @Test
    public void test01RetrieveEmployeeByUsername() throws Exception {
        System.out.println("retrieveEmployeeByUsername");
        String username = "sysadmin";
        Employee result = employeeSessionBean.retrieveEmployeeByUsername(username);
        System.out.println(result);
        assertNotNull(result);
    }
    
    /**
     * Test of retrieveEmployeeByUsername method, of class EmployeeSessionBean.
     */
    @Test
    public void test02RetrieveEmployeeByUsername() throws Exception {
        System.out.println("retrieveEmployeeByUsername");
        String username = "invalid";
        Employee result = employeeSessionBean.retrieveEmployeeByUsername(username);
        assertNull(result);
    }

    /**
     * Test of employeeLogin method, of class EmployeeSessionBean.
     */
   @Test
    public void test03EmployeeLogin() throws Exception {
        System.out.println("employeeLogin");
        String username = "sysadmin";
        String password = "password";
        Employee result = employeeSessionBean.employeeLogin(username, password);
        System.out.println(result);
        assertNotNull(result);
    }
    
    /**
     * Test of employeeLogin method, of class EmployeeSessionBean.
     */
    @Test(expected = InvalidEmployeeException.class)
    public void test04EmployeeLogin() throws Exception {
        System.out.println("employeeLogin");
        String username = "invalid";
        String password = "password";
        Employee result = employeeSessionBean.employeeLogin(username, password);
        assertNotNull(result);
    }
    
    /**
     * Test of employeeLogin method, of class EmployeeSessionBean.
     */
    @Test(expected = InvalidCredentialsException.class)
    public void test05EmployeeLogin() throws Exception {
        System.out.println("employeeLogin");
        String username = "sysadmin";
        String password = "invalid";
        Employee result = employeeSessionBean.employeeLogin(username, password);
        assertNotNull(result);
    }

    /**
     * Test of createEmployee method, of class EmployeeSessionBean.
     */
    @Test(expected = InvalidEmployeeException.class)
    public void test06CreateEmployee() throws Exception {
        System.out.println("createEmployee");
        String username = "sysadmin";
        String password = "password";
        StaffRole staffRole = StaffRole.ADMIN;
        Employee result = employeeSessionBean.createEmployee(username, password, staffRole);
        assertNotNull(result);

    }

    /**
     * Test of retrieveAllEmployees method, of class EmployeeSessionBean.
     */
    @Test
    public void test07RetrieveAllEmployees() throws Exception {
        System.out.println("retrieveAllEmployees");
        List<Employee> result = employeeSessionBean.retrieveAllEmployees();
        System.out.println(result);
        assertNotNull(result);
        assertEquals(4, result.size());
    }
    
    private EmployeeSessionBeanRemote lookupEmployeeSessionBeanRemote() 
    {
        try 
        {
            Context context = new InitialContext();
            return (EmployeeSessionBeanRemote) context.lookup("java:global/HotelReservationSystem/HotelReservationSystem-ejb/EmployeeSessionBean!ejb.session.stateless.EmployeeSessionBeanRemote");
        }
        catch (NamingException ne)
        {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
}
