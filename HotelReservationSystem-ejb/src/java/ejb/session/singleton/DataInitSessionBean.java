/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.RoomRateSessionBeanLocal;
import ejb.session.stateless.RoomSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Employee;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.RateType;
import util.enumeration.RoomStatus;
import util.enumeration.StaffRole;

/**
 *
 * @author kwpwn
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

    @EJB
    private RoomSessionBeanLocal roomSessionBean;

    @EJB
    private RoomRateSessionBeanLocal roomRateSessionBean;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;

    @EJB
    private EmployeeSessionBeanLocal employeeSessionBean;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @PostConstruct
    public void postConstruct() {
        if (em.find(Employee.class, 1l) == null) {
            try {
                employeeSessionBean.createEmployee("sysadmin", "password", StaffRole.ADMIN);
                employeeSessionBean.createEmployee("opmanager", "password", StaffRole.OPERATIONS);
                employeeSessionBean.createEmployee("salesmanager", "password", StaffRole.SALES);
                employeeSessionBean.createEmployee("guestrelo", "password", StaffRole.GUEST_RELATIONS);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        RoomType grandSuite = null;
        RoomType juniorSuite = null;
        RoomType familyRoom = null;
        RoomType premierRoom = null;
        RoomType deluxeRoom = null;
        if (em.find(RoomType.class, 1l) == null) {
            try {
                String amenities = "Toilet, TV";
                grandSuite = roomTypeSessionBean.createRoomType("Grand Suite", "A grand suite", 2000, 8, amenities);
                juniorSuite = roomTypeSessionBean.createRoomType("Junior Suite", "A junior suite", 1500, 6, amenities);
                familyRoom = roomTypeSessionBean.createRoomType("Family Room", "A family room", 1000, 4, amenities);
                premierRoom = roomTypeSessionBean.createRoomType("Premier Room", "A premier room", 600, 2, amenities);
                deluxeRoom = roomTypeSessionBean.createRoomType("Deluxe Room", "A deluxe room", 400, 2, amenities);
                
                grandSuite.setNextLowerRoomType(juniorSuite);
                roomTypeSessionBean.updateRoomType(grandSuite);
                
                juniorSuite.setNextLowerRoomType(familyRoom);
                juniorSuite.setNextHigherRoomType(grandSuite);
                roomTypeSessionBean.updateRoomType(juniorSuite);
                
                familyRoom.setNextLowerRoomType(premierRoom);
                familyRoom.setNextHigherRoomType(juniorSuite);
                roomTypeSessionBean.updateRoomType(familyRoom);
                
                premierRoom.setNextLowerRoomType(deluxeRoom);
                premierRoom.setNextHigherRoomType(familyRoom);
                roomTypeSessionBean.updateRoomType(premierRoom);
                
                deluxeRoom.setNextHigherRoomType(premierRoom);
                roomTypeSessionBean.updateRoomType(deluxeRoom);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        if (em.find(RoomRate.class, 1l) == null) {
            try {
                
                RoomRate deluxeRoomPublished = roomRateSessionBean.createRoomRate("Deluxe Room Published", deluxeRoom.getRoomTypeId(), RateType.PUBLISHED, new BigDecimal(100), null, null);
                RoomRate deluxeRoomNormal = roomRateSessionBean.createRoomRate("Deluxe Room Normal", deluxeRoom.getRoomTypeId(), RateType.NORMAL, new BigDecimal(50), null, null);
                deluxeRoom.addRoomRate(deluxeRoomPublished);
                deluxeRoom.addRoomRate(deluxeRoomNormal);

                RoomRate premierRoomPublished = roomRateSessionBean.createRoomRate("Premier Room Published", premierRoom.getRoomTypeId(), RateType.PUBLISHED, new BigDecimal(200), null, null);
                RoomRate premierRoomNormal = roomRateSessionBean.createRoomRate("Premier Room Normal", premierRoom.getRoomTypeId(), RateType.NORMAL, new BigDecimal(100), null, null);
                premierRoom.addRoomRate(premierRoomPublished);
                premierRoom.addRoomRate(premierRoomNormal);

                RoomRate familyRoomPublished = roomRateSessionBean.createRoomRate("Family Room Published", familyRoom.getRoomTypeId(), RateType.PUBLISHED, new BigDecimal(300), null, null);
                RoomRate familyRoomNormal = roomRateSessionBean.createRoomRate("Family Room Normal", familyRoom.getRoomTypeId(), RateType.NORMAL, new BigDecimal(150), null, null);
                familyRoom.addRoomRate(familyRoomPublished);
                familyRoom.addRoomRate(familyRoomNormal);

                RoomRate juniorSuitePublished = roomRateSessionBean.createRoomRate("Junior Suite Published", juniorSuite.getRoomTypeId(), RateType.PUBLISHED, new BigDecimal(400), null, null);
                RoomRate juniorSuiteNormal = roomRateSessionBean.createRoomRate("Junior Suite Normal", juniorSuite.getRoomTypeId(), RateType.NORMAL, new BigDecimal(200), null, null);
                juniorSuite.addRoomRate(juniorSuitePublished);
                juniorSuite.addRoomRate(juniorSuiteNormal);

                RoomRate grandSuitePublished = roomRateSessionBean.createRoomRate("Grand Suite Published", grandSuite.getRoomTypeId(), RateType.PUBLISHED, new BigDecimal(500), null, null);
                RoomRate grandSuiteNormal = roomRateSessionBean.createRoomRate("Grand Suite Normal", grandSuite.getRoomTypeId(), RateType.NORMAL, new BigDecimal(250), null, null);
                grandSuite.addRoomRate(grandSuitePublished);
                grandSuite.addRoomRate(grandSuiteNormal);

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        if (em.find(Room.class, 1l) == null) {
            try {
                for (int i = 1; i <= 5; i++) {
                    for (int j = 1; j <= 5; j++) {
                        RoomType roomType = null;
                        switch (j) {
                            case 1:
                                roomType = deluxeRoom;
                                break;
                            case 2:
                                roomType = premierRoom;
                                break;
                            case 3:
                                roomType = familyRoom;
                                break;
                            case 4:
                                roomType = juniorSuite;
                                break;
                            case 5:
                                roomType = grandSuite;
                                break;                           
                        }
                        roomSessionBean.createRoom("0" + i + "0" + j, RoomStatus.AVAILABLE, roomType.getRoomTypeId());
                    }
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
