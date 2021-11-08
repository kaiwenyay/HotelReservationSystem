/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import util.enumeration.RateType;
import util.exception.InputDataValidationException;
import util.exception.InvalidRoomRateException;
import util.exception.UpdateRoomRateException;

/**
 *
 * @author kwpwn
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RoomRateSessionBeanTest {
    
    private static RoomRateSessionBeanRemote roomRateSessionBean;
    
    public RoomRateSessionBeanTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        roomRateSessionBean = lookupSessionBean();
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
     * Test of createRoomRate method, of class RoomRateSessionBean.
     */
    @Test
    public void test01CreateRoomRate() throws Exception {
        String name = "normal";
        RoomType roomType = new RoomType();
        RateType rateType = RateType.NORMAL;
        BigDecimal ratePerNight = new BigDecimal(100);
        LocalDate validityFrom = LocalDate.now().plusDays(1);
        LocalDate validityTo = LocalDate.now().plusDays(2);
        RoomRate result = roomRateSessionBean.createRoomRate(name, roomType, rateType, ratePerNight, validityFrom, validityTo);
        assertEquals(name, result.getName());
    }
    
    @Test(expected = InvalidRoomRateException.class)
    public void test02CreateRoomRate() throws Exception {
        String name = "normal";
        RoomType roomType = new RoomType();
        RateType rateType = RateType.NORMAL;
        BigDecimal ratePerNight = new BigDecimal(100);
        LocalDate validityFrom = LocalDate.now().plusDays(1);
        LocalDate validityTo = LocalDate.now().plusDays(3);
        RoomRate result = roomRateSessionBean.createRoomRate(name, roomType, rateType, ratePerNight, validityFrom, validityTo);
        assertEquals(name, result.getName());
    }
    
    @Test(expected = InputDataValidationException.class)
    public void test03CreateRoomRate() throws Exception {
        String name = "normal";
        RoomType roomType = new RoomType();
        RateType rateType = RateType.NORMAL;
        BigDecimal ratePerNight = new BigDecimal(-1);
        LocalDate validityFrom = LocalDate.now();
        LocalDate validityTo = LocalDate.now();
        RoomRate result = roomRateSessionBean.createRoomRate(name, roomType, rateType, ratePerNight, validityFrom, validityTo);
        assertEquals(name, result.getName());
    }
/**
     * Test of retrieveRoomRateByName method, of class RoomRateSessionBean.
     */
    @Test
    public void test04RetrieveRoomRateByName() throws Exception {
        String name = "normal";
        RoomRate result = roomRateSessionBean.retrieveRoomRateByName(name);
        assertEquals(name, result.getName());
    }
    
    @Test(expected = InvalidRoomRateException.class)
    public void test05RetrieveRoomRateByName() throws Exception {
        String name = "peak";
        RoomRate result = roomRateSessionBean.retrieveRoomRateByName(name);
        assertEquals(name, result.getName());
    }
    
    /**
     * Test of retrieveRoomRateById method, of class RoomRateSessionBean.
     */
    @Test
    public void test06RetrieveRoomRateById() throws Exception {
        Long roomRateId = 1l;
        RoomRate result = roomRateSessionBean.retrieveRoomRateById(roomRateId);
        assertEquals(roomRateId, result.getRoomRateId());
    }
    
    @Test(expected = InvalidRoomRateException.class)
    public void test07RetrieveRoomRateById() throws Exception {
        Long roomRateId = 2l;
        RoomRate result = roomRateSessionBean.retrieveRoomRateById(roomRateId);
        assertEquals(roomRateId, result.getRoomRateId());
    }
    
    /**
     * Test of retrieveAllRoomRates method, of class RoomRateSessionBean.
     */
    @Test
    public void test08RetrieveAllRoomRates() throws Exception {
        boolean fetchRoomRate = false;
        List<RoomRate> result = roomRateSessionBean.retrieveAllRoomRates(fetchRoomRate);
        assertEquals(1, result.size());
    }

    /**
     * Test of updateRoomRate method, of class RoomRateSessionBean.
     */
    @Test
    public void test09UpdateRoomRate() throws Exception {
        RoomRate roomRate = roomRateSessionBean.retrieveRoomRateById(1l);
        BigDecimal ratePerNight = new BigDecimal(200);
        roomRate.setRatePerNight(ratePerNight);
        RoomRate result = roomRateSessionBean.updateRoomRate(roomRate);
        assertEquals(ratePerNight, result.getRatePerNight());
    }
    
    @Test(expected = InvalidRoomRateException.class)
    public void test10UpdateRoomRate() throws Exception {
        roomRateSessionBean.updateRoomRate(null);
    }
    
    @Test(expected = UpdateRoomRateException.class)
    public void test11UpdateRoomRate() throws Exception {
        RoomRate roomRate = roomRateSessionBean.retrieveRoomRateById(1l);
        String name = "published";
        roomRate.setName(name);
        RoomRate result = roomRateSessionBean.updateRoomRate(roomRate);
    }
    
    @Test(expected = InputDataValidationException.class)
    public void test12UpdateRoomRate() throws Exception {
        RoomRate roomRate = roomRateSessionBean.retrieveRoomRateById(1l);
        LocalDate validFrom = LocalDate.of(2020, 01, 01);
        roomRate.setValidFrom(validFrom);
        RoomRate result = roomRateSessionBean.updateRoomRate(roomRate);
    }

    /**
     * Test of deleteRoomRate method, of class RoomRateSessionBean.
     */
    @Test
    public void test13DeleteRoomRate() throws Exception {
        roomRateSessionBean.deleteRoomRate(1l);
    }
    
    @Test(expected = InvalidRoomRateException.class)
    public void test14DeleteRoomType() throws Exception {
        roomRateSessionBean.deleteRoomRate(1l);
    }
    
    private static RoomRateSessionBeanRemote lookupSessionBean() {
        try  {
            Context c = new InitialContext();
            
            return (RoomRateSessionBeanRemote) c.lookup("java:global/HotelReservationSystem/HotelReservationSystem-ejb/RoomRateSessionBean!ejb.session.stateless.RoomRateSessionBeanRemote");
        } catch (NamingException ne) {
            throw new RuntimeException(ne);
        }
    }
    
}
