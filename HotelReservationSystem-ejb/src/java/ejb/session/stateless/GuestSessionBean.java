/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
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
import util.exception.InputDataValidationException;
import util.exception.InvalidCredentialsException;
import util.exception.InvalidGuestException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
@Stateless
public class GuestSessionBean implements GuestSessionBeanRemote, GuestSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public GuestSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Guest retrieveGuestByEmail(String email) throws InvalidGuestException {
        Guest guest;
        try {
            guest = em.createNamedQuery("retrieveGuestByEmail", Guest.class)
                    .setParameter("inEmail", email)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new InvalidGuestException(String.format("Guest with email %s does not exist.", email));
        }
        return guest;
    }
    
    @Override
    public Guest createGuest(String email, String password, String name) throws InvalidGuestException, UnknownPersistenceException, InputDataValidationException {
        Guest guest = new Guest(email, password, name);
        Set<ConstraintViolation<Guest>>constraintViolations = validator.validate(guest);
        
        if (constraintViolations.isEmpty()) {
            try {
                
                em.persist(guest);
                em.flush();
                
            } catch (PersistenceException e) {
                if(e.getCause() != null && e.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    
                    if(e.getCause().getCause() != null && e.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new InvalidGuestException(String.format("Guest with email %s already exists.", email));
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
        
        return guest;
    }
    
    @Override
    public Guest guestLogin(String email, String password) throws InvalidGuestException, InvalidCredentialsException {
        Guest guest = retrieveGuestByEmail(email);
        if (! guest.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid password.");
        }
        return guest;
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Guest>> constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
