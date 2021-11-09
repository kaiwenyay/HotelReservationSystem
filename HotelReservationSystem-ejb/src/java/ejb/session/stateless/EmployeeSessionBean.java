/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Employee;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.StaffRole;
import util.exception.InputDataValidationException;
import util.exception.InvalidCredentialsException;
import util.exception.InvalidEmployeeException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
@Stateless
public class EmployeeSessionBean implements EmployeeSessionBeanRemote, EmployeeSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public EmployeeSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Employee retrieveEmployeeByUsername(String username) throws InvalidEmployeeException {
        Employee employee;
        try {
            employee = em.createNamedQuery("retrieveEmployeeByUsername", Employee.class)
                    .setParameter("inUsername", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new InvalidEmployeeException(String.format("Employee with username %s does not exist.", username));
        }
        return employee;
    }
    
    @Override
    public Employee employeeLogin(String username, String password) throws InvalidEmployeeException, InvalidCredentialsException {
        Employee employee = retrieveEmployeeByUsername(username);
        if (! employee.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid password.");
        }
        return employee;
    }

    @Override
    public Employee createEmployee(String username, String password, StaffRole staffRole) throws InvalidEmployeeException, UnknownPersistenceException, InputDataValidationException {
        Employee employee = new Employee(username, password, staffRole);
        Set<ConstraintViolation<Employee>>constraintViolations = validator.validate(employee);
        
        if (constraintViolations.isEmpty()) {
            try {
                
                em.persist(employee);
                em.flush();
                
            } catch (PersistenceException e) {
                if(e.getCause() != null && e.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    
                    if(e.getCause().getCause() != null && e.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new InvalidEmployeeException(String.format("Employee with username %s already exists.", username));
                    } else {
                        throw new UnknownPersistenceException(e.getMessage());
                    }
                    
                } else {
                    throw new UnknownPersistenceException(e.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
        
        return employee;
    }
    
    @Override
    public List<Employee> retrieveAllEmployees() {
        List<Employee> employees = em.createNamedQuery("retrieveAllEmployees", Employee.class)
                .getResultList();
        return employees;
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Employee>> constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
