/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import entity.Employee;
import entity.RoomType;
import java.util.Arrays;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
        if (em.find(RoomType.class, 1l) == null) {
            try {
                List<String> amenities = Arrays.asList(new String[]{"Toilet"});
                RoomType grandSuite = roomTypeSessionBean.createRoomType("Grand Suite", "A grand suite", 2000, 8, amenities, null, null);
                RoomType juniorSuite = roomTypeSessionBean.createRoomType("Junior Suite", "A junior suite", 1500, 6, amenities, grandSuite, null);
                grandSuite.setNextLowerRoomType(juniorSuite);
                RoomType familyRoom = roomTypeSessionBean.createRoomType("Family Room", "A family room", 1000, 4, amenities, juniorSuite, null);
                juniorSuite.setNextLowerRoomType(familyRoom);
                RoomType premierRoom = roomTypeSessionBean.createRoomType("Premier Room", "A premier room", 600, 2, amenities, familyRoom, null);
                familyRoom.setNextLowerRoomType(premierRoom);
                RoomType deluxeRoom = roomTypeSessionBean.createRoomType("Deluxe Room", "A deluxe room", 400, 2, amenities, premierRoom, null);
                premierRoom.setNextLowerRoomType(deluxeRoom);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
