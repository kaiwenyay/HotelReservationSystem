/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
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
import util.exception.InputDataValidationException;
import util.exception.InvalidCredentialsException;
import util.exception.InvalidGuestException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GuestSessionBeanTest {
    private static GuestSessionBeanRemote guestSessionBean;
    
    public GuestSessionBeanTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        guestSessionBean = lookupSessionBean();
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
     * Test of createGuest method, of class GuestSessionBean.
     */
    @Test
    public void test01CreateGuest() throws InvalidGuestException, UnknownPersistenceException, InputDataValidationException {
        String username = "guest@email.com";
        String password = "password";
        Guest result = guestSessionBean.createGuest(username, password);
        assertEquals(username, result.getUsername());
    }
    
    @Test(expected = InvalidGuestException.class)
    public void test02CreateGuest() throws InvalidGuestException, UnknownPersistenceException, InputDataValidationException {
        String username = "guest@email.com";
        String password = "password";
        Guest result = guestSessionBean.createGuest(username, password);
        assertEquals(username, result.getUsername());    
    }
    
    @Test(expected = InputDataValidationException.class)
    public void test03CreateGuest() throws InvalidGuestException, UnknownPersistenceException, InputDataValidationException {
        String username = "guest@email.com";
        String password = "pw";
        Guest result = guestSessionBean.createGuest(username, password);
        assertEquals(username, result.getUsername());    
    }
    
    /**
     * Test of retrieveGuestByEmail method, of class GuestSessionBean.
     */
    @Test
    public void test04RetrieveGuestByEmail() throws InvalidGuestException {
        String username = "guest@email.com";
        Guest result = guestSessionBean.retrieveGuestByEmail(username);
        assertEquals(username, result.getUsername());
    }
    
    @Test(expected = InvalidGuestException.class)
    public void test05RetrieveGuestByEmail() throws InvalidGuestException {
        String username = "invalid";
        Guest result = guestSessionBean.retrieveGuestByEmail(username);
        assertEquals(username, result.getUsername());
    }
    
    /**
     * Test of guestLogin method, of class GuestSessionBean.
     */
    @Test
    public void test06GuestLogin() throws InvalidGuestException, InvalidCredentialsException {
        String username = "guest@email.com";
        String password = "password";
        Guest result = guestSessionBean.guestLogin(username, password);
        assertEquals(username, result.getUsername());
    }
    
    @Test(expected = InvalidGuestException.class)
    public void test07GuestLogin() throws InvalidGuestException, InvalidCredentialsException {
        String username = "invalid";
        String password = "password";
        Guest result = guestSessionBean.guestLogin(username, password);
        assertEquals(username, result.getUsername());
    }
    
    @Test(expected = InvalidCredentialsException.class)
    public void test08GuestLogin() throws InvalidGuestException, InvalidCredentialsException {
        String username = "guest@email.com";
        String password = "pw";
        Guest result = guestSessionBean.guestLogin(username, password);
        assertEquals(username, result.getUsername());
    }

    private static GuestSessionBeanRemote lookupSessionBean() {
        try  {
            Context c = new InitialContext();
            
            return (GuestSessionBeanRemote) c.lookup("java:global/HotelReservationSystem/HotelReservationSystem-ejb/GuestSessionBean!ejb.session.stateless.GuestSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
    
}
