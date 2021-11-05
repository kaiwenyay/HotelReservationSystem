/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.enumeration.StaffRole;
import util.exception.InvalidCredentialsException;
import util.exception.InvalidEmployeeException;

/**
 *
 * @author kwpwn
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    @Override
    public Employee retrieveEmployeeByUsername(String username) {
        try {
            Employee employee = em.createNamedQuery("retrieveEmployeeByUsername", Employee.class)
                    .setParameter("inUsername", username)
                    .getSingleResult();
            return employee;
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @Override
    public Employee employeeLogin(String username, String password) throws InvalidEmployeeException, InvalidCredentialsException {
        Employee employee = retrieveEmployeeByUsername(username);
        if (employee == null) {
            throw new InvalidEmployeeException(String.format("Employee with username %s does not exist.", username));
        }
        if (! employee.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid password.");
        }
        return employee;
    }

    @Override
    public Long createEmployee(String username, String password, StaffRole staffRole) throws InvalidEmployeeException {
        Employee employee = retrieveEmployeeByUsername(username);
        if (employee != null) {
            throw new InvalidEmployeeException(String.format("Employee with username %s already exists.", username));
        }
        employee = new Employee(username, password, staffRole);
        em.persist(employee);
        em.flush();
        return employee.getUserId();
    }
    
    @Override
    public List<Employee> retrieveAllEmployees() {
        List<Employee> employees = em.createNamedQuery("retrieveAllEmployees", Employee.class)
                .getResultList();
        return employees;
    }
}
