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
import java.time.LocalDate;
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
import util.enumeration.ReservationStatus;
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
    public List<Reservation> retrieveReservationsByCheckInDate(LocalDate checkInDate) {
        List<Reservation> employees = em.createNamedQuery("retrieveReservationByCheckInDate", Reservation.class)
                .setParameter("inCheckInDate", checkInDate)
                .getResultList();
        return employees;
    }
    
    @Override
    public Reservation createReservation(BigDecimal totalAmount, LocalDate checkInDate, LocalDate checkOutDate, LocalDateTime reservationDateTime, ReservationStatus reservationStatus, List<ReservationItem> reservationItems, User user) throws InvalidReservationException, UnknownPersistenceException, InputDataValidationException {
        Reservation reservation = new Reservation(totalAmount, checkInDate, checkOutDate, reservationDateTime, reservationStatus, reservationItems, user);
        Set<ConstraintViolation<Reservation>> reservationConstraintViolations = validator.validate(reservation);
        
        if (reservationConstraintViolations.isEmpty()) {
            try {
                
                em.persist(reservation);
                user.addReservation(reservation);
                em.flush();
                
                for (ReservationItem i : reservationItems) {
                    em.persist(i);
                }
                
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
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(reservationConstraintViolations));
        }
        
        return reservation;
    }
    
    @Override
    public List<Reservation> retrieveReservationsByUser(String username) {
        List<Reservation> reservations = em.createNamedQuery("retrieveReservationsByUser", Reservation.class)
                .setParameter("inUsername", username)
                .getResultList();
        return reservations;
    }
    
    @Override
    public List<Reservation> retrieveReservationsByPeriod(LocalDate checkInDate, LocalDate checkOutDate) {
        List<Reservation> reservations = em.createNamedQuery("retrieveReservationsByPeriod", Reservation.class)
                .setParameter("inCheckInDate", checkInDate)
                .setParameter("inCheckOutDate", checkOutDate)
                .getResultList();
        reservations.forEach(x -> x.getReservationItems().size());
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
