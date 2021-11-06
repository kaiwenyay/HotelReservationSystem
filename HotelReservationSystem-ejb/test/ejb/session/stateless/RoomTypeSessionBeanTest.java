/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.util.ArrayList;
import java.util.Arrays;
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
import util.exception.InputDataValidationException;
import util.exception.InvalidRoomTypeException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author kwpwn
 */
public class RoomTypeSessionBeanTest {
    
    private final RoomTypeSessionBeanRemote roomTypeSessionBean = lookupRoomTypeSessionBeanRemote();
    
    public RoomTypeSessionBeanTest() {
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
     * Test of retrieveRoomTypeByName method, of class RoomTypeSessionBean.
     */
    @Test
    public void testRetrieveRoomTypeByName() throws Exception {
        System.out.println("retrieveRoomTypeByName");
        String name = "Deluxe Room";
        RoomType result = roomTypeSessionBean.retrieveRoomTypeByName(name);
        System.out.println(result);
        assertNotNull(result);
    }
    
//    /**
//     * Test of createEmployee method, of class EmployeeSessionBean.
//     */
//    @Test(expected = InputDataValidationException.class)
//    public void testCreateRoomType() throws Exception {
//        System.out.println("createEmployee");
//        String name = "Deluxe Room";
//        String description = "";
//        Integer size = 0;
//        Integer bedCapacity = 0;
//        List<String> amenities = new ArrayList<>();
//        RoomType result = roomTypeSessionBean.createRoomType(name, description, size, bedCapacity, amenities, null, null, null);
//        assertNotNull(result);
//
//    }
//    
//    /**
//     * Test of createEmployee method, of class EmployeeSessionBean.
//     */
//    @Test(expected = InvalidRoomTypeException.class)
//    public void testCreateRoomType2() throws Exception {
//        System.out.println("createEmployee");
//        String name = "Deluxe Room";
//        String description = "A deluxe room";
//        Integer size = 400;
//        Integer bedCapacity = 2;
//        List<String> amenities = Arrays.asList(new String[]{"Toilet"});
//        RoomType result = roomTypeSessionBean.createRoomType(name, description, size, bedCapacity, amenities, null, null, null);
//        assertNotNull(result);
//
//    }

    /**
     * Test of retrieveAllRoomTypes method, of class RoomTypeSessionBean.
     */
    @Test
    public void testRetrieveAllRoomTypes() throws Exception {
        System.out.println("retrieveAllRoomTypes");
        List<RoomType> result = roomTypeSessionBean.retrieveAllRoomTypes();
        System.out.println(result);
        assertNotNull(result);
        assertEquals(5, result.size());
    }
    
    /**
     * Test of updateRoomType method, of class RoomTypeSessionBean.
     */
    @Test
    public void updateRoomType() throws Exception {
        System.out.println("updateRoomType");
        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeById(1l);
        roomType.setDescription("after test");
        RoomType result = roomTypeSessionBean.updateRoomType(roomType);
        assertEquals(roomType.getDescription(), result.getDescription());
    }
    
    /**
     * Test of updateRoomType method, of class RoomTypeSessionBean.
     */
    @Test(expected = UpdateRoomTypeException.class)
    public void updateRoomType2() throws Exception {
        System.out.println("updateRoomType");
        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeById(1l);
        roomType.setName("room");
        RoomType result = roomTypeSessionBean.updateRoomType(roomType);
    }
    
    /**
     * Test of updateRoomType method, of class RoomTypeSessionBean.
     */
    @Test(expected = InvalidRoomTypeException.class)
    public void updateRoomType3() throws Exception {
        System.out.println("updateRoomType");
        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeById(1l);
        roomType.setRoomTypeId(null);
        RoomType result = roomTypeSessionBean.updateRoomType(roomType);
    }
    
    /**
     * Test of updateRoomType method, of class RoomTypeSessionBean.
     */
    @Test(expected = InputDataValidationException.class)
    public void updateRoomType4() throws Exception {
        System.out.println("updateRoomType");
        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeById(1l);
        roomType.setSize(-1);
        RoomType result = roomTypeSessionBean.updateRoomType(roomType);
    }
    
    /**
     * Test of deleteRoomType method, of class RoomTypeSessionBean.
     */
    
    // @Test(expected = InvalidRoomTypeException.class)
    public void deleteRoomType() throws Exception {
        System.out.println("deleteRoomType");
        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeByName("Deluxe Room");
        roomTypeSessionBean.deleteRoomType(roomType.getRoomTypeId());
        roomTypeSessionBean.retrieveRoomTypeByName("Deluxe Room");
    }
    
    /**
     * Test of deleteRoomType method, of class RoomTypeSessionBean.
     */
    
    // @Test
    public void deleteRoomType2() throws Exception {
        System.out.println("deleteRoomType");
        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeByName("Premier Room");
        roomType.addRoom(new Room());
        roomTypeSessionBean.deleteRoomType(roomType.getRoomTypeId());
        assertEquals(true, roomType.isDisabled());
    }
    
    private RoomTypeSessionBeanRemote lookupRoomTypeSessionBeanRemote() 
    {
        try 
        {
            Context context = new InitialContext();
            return (RoomTypeSessionBeanRemote) context.lookup("java:global/HotelReservationSystem/HotelReservationSystem-ejb/RoomTypeSessionBean!ejb.session.stateless.RoomTypeSessionBeanRemote");
        }
        catch (NamingException ne)
        {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
}
