/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.ReservationItem;
import entity.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.InputDataValidationException;
import util.exception.InvalidReservationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public ReservationSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Reservation retrieveReservationById(Long reservationId) throws InvalidReservationException {
        Reservation reservation = em.find(Reservation.class, reservationId);     
        if(reservation != null) {
            return reservation;
        } else {
            throw new InvalidReservationException("Rservation " + reservationId + " does not exist!");
        }               
    }
    
    @Override
    public Reservation createReservation(BigDecimal totalAmount, LocalDateTime checkInDate, LocalDateTime checkOutDate, LocalDateTime reservationDateTime, List<ReservationItem> reservationItems, User user) throws InvalidReservationException, UnknownPersistenceException, InputDataValidationException {
        Reservation reservation = new Reservation(totalAmount, checkInDate, checkOutDate, reservationDateTime, reservationItems, user);
        Set<ConstraintViolation<Reservation>>constraintViolations = validator.validate(reservation);
        
        if (constraintViolations.isEmpty()) {
            try {
                
                em.persist(reservation);
                user.addReservation(reservation);
                em.flush();
                
            } catch (PersistenceException e) {
                if(e.getCause() != null && e.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    
                    if(e.getCause().getCause() != null && e.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new InvalidReservationException();
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
        
        return reservation;
    }
    
    @Override
    public List<Reservation> retrieveAllReservationsByUser(String username) {
        List<Reservation> reservations = em.createNamedQuery("retrieveAllReservationsByUser", Reservation.class)
                .setParameter("inUsername", username)
                .getResultList();
        return reservations;
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Reservation>> constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
