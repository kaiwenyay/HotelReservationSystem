/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.util.Arrays;
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
import util.exception.InvalidRoomTypeException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author kwpwn
 */

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class RoomTypeSessionBeanTest {
    
    private static RoomTypeSessionBeanRemote roomTypeSessionBean;
    
    public RoomTypeSessionBeanTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
//        roomTypeSessionBean = lookupSessionBean();
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
     * Test of createRoomType method, of class RoomTypeSessionBean.
     */
//    @Test
//    public void test01CreateRoomType() throws InvalidRoomTypeException, UnknownPersistenceException, InputDataValidationException {
//        String name = "Deluxe Room";
//        String description = "a";
//        Integer size = 300;
//        Integer bedCapacity = 2;
//        List<String> amenities = Arrays.asList(new String[]{"a"});
//        RoomType nextHigherRoomType = null;
//        RoomType nextLowerRoomType = null;
//        RoomRate roomRate = null;
//        RoomType result = roomTypeSessionBean.createRoomType(name, description, size, bedCapacity, amenities, nextHigherRoomType, nextLowerRoomType, roomRate);
//        assertEquals(name, result.getName());
//    }
//
//    /**
//     * Test of retrieveAllRoomTypes method, of class RoomTypeSessionBean.
//     */
//    @Test(expected = InvalidRoomTypeException.class)
//    public void test02CreateRoomType() throws Exception {
//        String name = "Deluxe Room";
//        String description = "a";
//        Integer size = 300;
//        Integer bedCapacity = 2;
//        List<String> amenities = Arrays.asList(new String[]{"a"});
//        RoomType nextHigherRoomType = null;
//        RoomType nextLowerRoomType = null;
//        RoomRate roomRate = null;
//        RoomType result = roomTypeSessionBean.createRoomType(name, description, size, bedCapacity, amenities, nextHigherRoomType, nextLowerRoomType, roomRate);
//        assertEquals(name, result.getName());
//    }
//    
//    @Test(expected = InputDataValidationException.class)
//    public void test03CreateRoomType() throws InvalidRoomTypeException, UnknownPersistenceException, InputDataValidationException {
//        String name = "Premier Room";
//        String description = "a";
//        Integer size = -1;
//        Integer bedCapacity = -1;
//        List<String> amenities = null;
//        RoomType nextHigherRoomType = null;
//        RoomType nextLowerRoomType = null;
//        RoomRate roomRate = null;
//        RoomType result = roomTypeSessionBean.createRoomType(name, description, size, bedCapacity, amenities, nextHigherRoomType, nextLowerRoomType, roomRate);
//        assertEquals(name, result.getName());
//    }
//    
//    /**
//     * Test of retrieveRoomTypeByName method, of class RoomTypeSessionBean.
//     */
//    @Test
//    public void test04RetrieveRoomTypeByName() throws InvalidRoomTypeException {
//        String name = "Deluxe Room";
//        RoomType result = roomTypeSessionBean.retrieveRoomTypeByName(name);
//        assertEquals(name, result.getName());
//    }
//    
//    @Test(expected = InvalidRoomTypeException.class)
//    public void test05RetrieveRoomTypeByName() throws InvalidRoomTypeException {
//        String name = "Premier Room";
//        RoomType result = roomTypeSessionBean.retrieveRoomTypeByName(name);
//        assertEquals(name, result.getName());
//    }
//    
//    /**
//     * Test of retrieveRoomTypeById method, of class RoomTypeSessionBean.
//     */
//    @Test
//    public void test06RetrieveRoomTypeById() throws InvalidRoomTypeException {
//        Long roomTypeId = 1l;
//        RoomType result = roomTypeSessionBean.retrieveRoomTypeById(roomTypeId);
//        assertEquals(roomTypeId, result.getRoomTypeId());
//    }
//    
//    @Test(expected = InvalidRoomTypeException.class)
//    public void test07RetrieveRoomTypeById() throws InvalidRoomTypeException {
//        Long roomTypeId = 2l;
//        RoomType result = roomTypeSessionBean.retrieveRoomTypeById(roomTypeId);
//        assertEquals(roomTypeId, result.getRoomTypeId());
//    }
//    
//    /**
//     * Test of retrieveAllRoomTypes method, of class RoomTypeSessionBean.
//     */
//    @Test
//    public void test08RetrieveAllRoomTypes() throws Exception {
//        boolean fetchRooms = false;
//        boolean fetchRoomRates = false;
//        List<RoomType> result = roomTypeSessionBean.retrieveAllRoomTypes(fetchRooms, fetchRoomRates);
//        assertEquals(1, result.size());
//    }
//
//    /**
//     * Test of updateRoomType method, of class RoomTypeSessionBean.
//     */
//    @Test
//    public void test09UpdateRoomType() throws Exception {
//        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeById(1l);
//        Integer size = 500;
//        roomType.setSize(size);
//        roomTypeSessionBean.updateRoomType(roomType);
//        roomType = roomTypeSessionBean.retrieveRoomTypeById(1l);
//        assertEquals(size, roomType.getSize());
//    }
//    
//    @Test(expected = InvalidRoomTypeException.class)
//    public void test10UpdateRoomType() throws Exception {
//        roomTypeSessionBean.updateRoomType(null);
//    }
//    
//    @Test(expected = UpdateRoomTypeException.class)
//    public void test11UpdateRoomType() throws Exception {
//        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeById(1l);
//        String name = "Premier Room";
//        roomType.setName(name);
//        roomTypeSessionBean.updateRoomType(roomType);
//        roomType = roomTypeSessionBean.retrieveRoomTypeById(1l);
//        assertEquals(name, roomType.getName());
//    }
//    
//    @Test(expected = InputDataValidationException.class)
//    public void test12UpdateRoomType() throws Exception {
//        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeById(1l);
//        Integer size = -1;
//        roomType.setSize(size);
//        roomTypeSessionBean.updateRoomType(roomType);
//        roomType = roomTypeSessionBean.retrieveRoomTypeById(1l);
//    }
//
//    /**
//     * Test of deleteRoomType method, of class RoomTypeSessionBean.
//     */
////    @Test
////    public void test13DeleteRoomType() throws Exception {
////        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeByName("Deluxe Room");
////        Room room = new Room();
////        room.setRoomType(roomType);
////        roomType.addRoom(room);
////        roomTypeSessionBean.updateRoomType(roomType);
////        roomTypeSessionBean.deleteRoomType(1l);
////        roomType = roomTypeSessionBean.retrieveRoomTypeById(1l);
////        assertTrue(roomType.isDisabled());
////    }
//    
//    @Test
//    public void test14DeleteRoomType() throws Exception {
//        roomTypeSessionBean.deleteRoomType(1l);
//    }
//    
//    @Test(expected = InvalidRoomTypeException.class)
//    public void test15DeleteRoomType() throws Exception {
//        roomTypeSessionBean.deleteRoomType(1l);
//    }
//    
//    private static RoomTypeSessionBeanRemote lookupSessionBean() {
//        try  {
//            Context c = new InitialContext();
//            
//            return (RoomTypeSessionBeanRemote) c.lookup("java:global/HotelReservationSystem/HotelReservationSystem-ejb/RoomTypeSessionBean!ejb.session.stateless.RoomTypeSessionBeanRemote");
//        } catch (NamingException ne) {
//            throw new RuntimeException(ne);
//        }
//    }
    
}
