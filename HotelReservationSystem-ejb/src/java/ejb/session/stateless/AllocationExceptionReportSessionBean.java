/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AllocationExceptionReport;
import entity.Reservation;
import entity.ReservationItem;
import java.time.LocalDate;
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
import util.exception.InputDataValidationException;
import util.exception.InvalidReportException;
import util.exception.InvalidReservationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
@Stateless
public class AllocationExceptionReportSessionBean implements AllocationExceptionReportSessionBeanRemote, AllocationExceptionReportSessionBeanLocal {

    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    public AllocationExceptionReportSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public AllocationExceptionReport retrieveReport(LocalDate day, boolean fetchReservation, boolean fetchReservationItems, boolean fetchRoomType, boolean fetchRoom) throws InvalidReportException {
        AllocationExceptionReport report;
        try {
            report = em.createNamedQuery("retrieveAllocationExceptionReportByDay", AllocationExceptionReport.class)
                    .setParameter("inDay", day)
                    .getSingleResult();
            if (fetchReservation) {
                List<Reservation> reservations = report.getReservations();
                reservations.size();
                if (fetchReservationItems) {
                    for (Reservation r : reservations) {
                        r.getReservationItems().size();
                        if (fetchRoomType || fetchRoom) {
                            List<ReservationItem> items = r.getReservationItems();
                            for (ReservationItem i : items) {
                                if (fetchRoomType) {
                                    i.getReservedRoomType();
                                }
                                if (fetchRoom) {
                                    i.getAllocatedRoom();
                                }
                            }
                        }
                    }
                }
            }
        } catch (NoResultException e) {
            throw new InvalidReportException(String.format("Report on day %s does not exist.", day));
        }
        return report;
    }
    
    @Override
    public AllocationExceptionReport createReport(LocalDate day) throws InvalidReportException, UnknownPersistenceException, InputDataValidationException {
        
        AllocationExceptionReport allocationExceptionReport = new AllocationExceptionReport(day);
        Set<ConstraintViolation<AllocationExceptionReport>> reservationConstraintViolations = validator.validate(allocationExceptionReport);
        
        if (reservationConstraintViolations.isEmpty()) {
            try {
                
                em.persist(allocationExceptionReport);
                em.flush();
                
            } catch (PersistenceException e) {
                if(e.getCause() != null && e.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    
                    if(e.getCause().getCause() != null && e.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new InvalidReportException(String.format("Allocation exception report for %s already exists.", day));
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
        
        return allocationExceptionReport;
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<AllocationExceptionReport>> constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
