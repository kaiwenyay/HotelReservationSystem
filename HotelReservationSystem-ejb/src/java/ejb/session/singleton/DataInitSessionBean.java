/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.singleton;

import ejb.session.stateless.EmployeeSessionBeanLocal;
import entity.Employee;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.LocalBean;
import javax.ejb.Startup;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.enumeration.StaffRole;
import util.exception.InvalidEmployeeException;

/**
 *
 * @author kwpwn
 */
@Singleton
@LocalBean
@Startup
public class DataInitSessionBean {

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
            } catch (InvalidEmployeeException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
