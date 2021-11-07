/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
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
import util.exception.InputDataValidationException;
import util.exception.InvalidCredentialsException;
import util.exception.InvalidPartnerException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class PartnerSessionBeanTest {
    
    private static PartnerSessionBeanRemote partnerSessionBean;
    
    public PartnerSessionBeanTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        partnerSessionBean = lookupSessionBean();
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
     * Test of createPartner method, of class PartnerSessionBean.
     */
    @Test
    public void test01CreatePartner() throws InvalidPartnerException, UnknownPersistenceException, InputDataValidationException {
        String username = "expedia";
        String password = "password";
        String partnerName = "expedia";
        Partner result = partnerSessionBean.createPartner(username, password, partnerName);
        assertEquals(username, result.getUsername());
    }
    
    @Test(expected = InvalidPartnerException.class)
    public void test02CreatePartner() throws InvalidPartnerException, UnknownPersistenceException, InputDataValidationException {
        String username = "expedia";
        String password = "password";
        String partnerName = "expedia";
        Partner result = partnerSessionBean.createPartner(username, password, partnerName);
        assertEquals(username, result.getUsername());
    }
    
    @Test(expected = InputDataValidationException.class)
    public void test03CreatePartner() throws InvalidPartnerException, UnknownPersistenceException, InputDataValidationException {
        String username = "expedia";
        String password = "pw";
        String partnerName = "expedia";
        Partner result = partnerSessionBean.createPartner(username, password, partnerName);
        assertEquals(username, result.getUsername());
    }
    
    /**
     * Test of retrievePartnerByUsername method, of class PartnerSessionBean.
     */
    @Test
    public void test04RetrievePartnerByUsername() throws InvalidPartnerException {
        String username = "expedia";
        Partner result = partnerSessionBean.retrievePartnerByUsername(username);
        assertEquals(username, result.getUsername());
    }
    
    @Test(expected = InvalidPartnerException.class)
    public void test05RetrievePartnerByUsername() throws InvalidPartnerException {
        String username = "invalid";
        Partner result = partnerSessionBean.retrievePartnerByUsername(username);
        assertNull(result);
    }
    
    /**
     * Test of partnerLogin method, of class PartnerSessionBean.
     */
    @Test
    public void test06PartnerLogin() throws InvalidPartnerException, InvalidCredentialsException {
        String username = "expedia";
        String password = "password";
        Partner result = partnerSessionBean.partnerLogin(username, password);
        assertEquals(username, result.getUsername());
    }
    
    @Test(expected = InvalidPartnerException.class)
    public void test07PartnerLogin() throws InvalidPartnerException, InvalidCredentialsException {
        String username = "invalid";
        String password = "password";
        Partner result = partnerSessionBean.partnerLogin(username, password);
        assertNull(result);
    }
    
    @Test(expected = InvalidCredentialsException.class)
    public void test08PartnerLogin() throws InvalidPartnerException, InvalidCredentialsException {
        String username = "expedia";
        String password = "pw";
        Partner result = partnerSessionBean.partnerLogin(username, password);
        assertEquals(username, result.getUsername());
    }

    /**
     * Test of retrieveAllPartners method, of class PartnerSessionBean.
     */
    @Test
    public void test09RetrieveAllPartners() throws Exception {
        List<Partner> result = partnerSessionBean.retrieveAllPartners();
        assertEquals(1, result.size());
    }
    
    private static PartnerSessionBeanRemote lookupSessionBean() {
        try  {
            Context c = new InitialContext();
            
            return (PartnerSessionBeanRemote) c.lookup("java:global/HotelReservationSystem/HotelReservationSystem-ejb/PartnerSessionBean!ejb.session.stateless.PartnerSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
    
}
