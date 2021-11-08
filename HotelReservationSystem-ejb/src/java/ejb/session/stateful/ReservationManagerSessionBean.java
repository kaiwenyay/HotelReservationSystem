/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import ejb.session.stateless.ReservationSessionBeanLocal;
import ejb.session.stateless.RoomTypeSessionBeanLocal;
import ejb.session.stateless.UserSessionBeanLocal;
import entity.Reservation;
import entity.ReservationItem;
import entity.RoomType;
import entity.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.ReservationStatus;
import util.exception.InputDataValidationException;
import util.exception.InvalidReservationException;
import util.exception.InvalidRoomException;
import util.exception.InvalidRoomTypeException;
import util.exception.InvalidUserException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
@Stateful
public class ReservationManagerSessionBean implements ReservationManagerSessionBeanRemote, ReservationManagerSessionBeanLocal {

    @EJB
    private UserSessionBeanLocal userSessionBean;

    @EJB
    private ReservationSessionBeanLocal reservationSessionBean;

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;
     
    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private List<ReservationItem> reservationItems;
    private BigDecimal totalAmount;

    public ReservationManagerSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
        initialiseState();
    }
    
    private void initialiseState() {
        reservationItems = new ArrayList<>();
        totalAmount = new BigDecimal("0.00");
    }

    public List<RoomType> searchRoom(LocalDate checkInDate, LocalDate checkOutDate) {
        List<RoomType> roomTypes = roomTypeSessionBean.retrieveAllRoomTypes(false, true);
        if (checkInDate.equals(LocalDate.now())) {
            return roomTypes;
        }
        List<Reservation> clashingReservations = reservationSessionBean.retrieveReservationsByPeriod(checkInDate, checkOutDate);
        roomTypes.forEach(x -> em.detach(x));
        for (Reservation r : clashingReservations) {
            List<ReservationItem> items = r.getReservationItems();
            for (ReservationItem i : items) {
                i.getReservedRoomType().decreaseTotalRooms();
            }
        }
        return roomTypes;
    }
    
    public void addReservationItem(BigDecimal subTotal, String roomTypeName) throws InvalidRoomTypeException, InputDataValidationException {
        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeByName(roomTypeName);
        ReservationItem reservationItem = new ReservationItem(subTotal, roomType);
        Set<ConstraintViolation<ReservationItem>> constraintViolations = validator.validate(reservationItem);
        if (! constraintViolations.isEmpty()) {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
        reservationItems.add(reservationItem);
        totalAmount.add(reservationItem.getSubTotal());
    }
    
    public Reservation reserveRooms(String username, LocalDate checkInDate, LocalDate checkOutDate) throws InvalidRoomException, InvalidUserException, InvalidReservationException, UnknownPersistenceException, InputDataValidationException {
        User user = userSessionBean.retrieveUserByUsername(username);
        LocalDateTime reservationDateTime = LocalDateTime.now();
        Reservation reservation = reservationSessionBean.createReservation(totalAmount, checkInDate, checkOutDate, reservationDateTime, ReservationStatus.RESERVED, reservationItems, user);
        initialiseState();
        return reservation;
    }
    
    public void clear() {
        initialiseState();
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<ReservationItem>> constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
