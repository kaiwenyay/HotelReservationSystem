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
import entity.RoomRate;
import entity.RoomType;
import entity.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
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
import util.enumeration.AllocationExceptionType;
import util.enumeration.RateType;
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
    
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private List<RoomType> availableRoomTypes;
    private List<ReservationItem> reservationItems;
    private BigDecimal totalAmount;

    public ReservationManagerSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
        initialiseState();
    }
    
    private void initialiseState() {
        checkInDate = null;
        checkOutDate = null;
        availableRoomTypes = new ArrayList<>();
        reservationItems = new ArrayList<>();
        totalAmount = new BigDecimal(0.00);
    }
       
    @Override
    public List<RoomType> searchRooms(LocalDate checkInDate, LocalDate checkOutDate, Integer noOfRooms) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        List<RoomType> roomTypes = roomTypeSessionBean.retrieveAllRoomTypes(false, false, false, true);
        List<Reservation> clashingReservations = reservationSessionBean.retrieveReservationsByPeriod(checkInDate, checkOutDate);
        roomTypes.forEach(x -> em.detach(x));
        for (Reservation r : clashingReservations) {
            if (r.getCheckOutDate().equals(checkInDate) || r.getCheckInDate().equals(checkOutDate)) {
                continue;
            }
            List<ReservationItem> items = r.getReservationItems();
            for (ReservationItem i : items) {
                if (i.getAllocationExceptionType() != AllocationExceptionType.TYPE_TWO) {
                    roomTypes.get(roomTypes.indexOf(i.getReservedRoomType())).decreaseTotalRooms();
                }
            }
        }
        roomTypes.removeIf(x -> x.getTotalRooms() < noOfRooms);
        availableRoomTypes = roomTypes;
        return roomTypes;
    }
    
    @Override
    public void addReservationItem(BigDecimal subTotal, String roomTypeName) throws InvalidRoomTypeException, InputDataValidationException {
        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeByName(roomTypeName);
        ReservationItem reservationItem = new ReservationItem(subTotal, roomType);
        Set<ConstraintViolation<ReservationItem>> constraintViolations = validator.validate(reservationItem);
        if (! constraintViolations.isEmpty()) {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
        reservationItems.add(reservationItem);
        totalAmount = totalAmount.add(reservationItem.getSubTotal());
    }
    
    public Reservation reserveRooms(String username) throws InvalidRoomException, InvalidUserException, InvalidReservationException, UnknownPersistenceException, InputDataValidationException {
        User user = userSessionBean.retrieveUserByUsername(username);
        LocalDateTime reservationDateTime = LocalDateTime.now();
        Reservation reservation = reservationSessionBean.createReservation(totalAmount, checkInDate, checkOutDate, reservationDateTime, ReservationStatus.RESERVED, reservationItems, user);
        initialiseState();
        return reservation;
    }
    
    @Override
    public List<BigDecimal> calculateSubTotals() {
        List<BigDecimal> subTotals = new ArrayList<>();
        for (int i = 0; i < availableRoomTypes.size(); i++) {
            BigDecimal subTotal = new BigDecimal(0);
            RoomType roomType = availableRoomTypes.get(i);
            List<RoomRate> roomRates = roomType.getRoomRates();
            Long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
            LocalDate nightCounter = checkInDate;

            for (int j = 0; j < nights; j++) {
                boolean foundPrevailingRate = false;
                BigDecimal ratePerNight = new BigDecimal(0);
                for (RoomRate r: roomRates) {
                    if (r.getRateType() == RateType.PROMOTION && ! r.isDisabled()) {
                        LocalDate validFrom = r.getValidFrom();
                        LocalDate validTo = r.getValidTo();
                        if (validFrom.compareTo(nightCounter) <= 0 && validTo.compareTo(nightCounter) >= 0) {
                            foundPrevailingRate = true;
                            ratePerNight = r.getRatePerNight();
                        }
                    } 
                    if (r.getRateType() == RateType.PEAK && ! r.isDisabled() && ! foundPrevailingRate) {
                        LocalDate validFrom = r.getValidFrom();
                        LocalDate validTo = r.getValidTo();
                        if (validFrom.compareTo(nightCounter) <= 0 && validTo.compareTo(nightCounter) >= 0) {
                            foundPrevailingRate = true;
                            ratePerNight = r.getRatePerNight();
                        }
                    } 
                    if (r.getRateType() == RateType.NORMAL && ! r.isDisabled() && ! foundPrevailingRate) {
                        ratePerNight = r.getRatePerNight();
                    }
                }
                
                nightCounter = nightCounter.plusDays(1);
                subTotal = subTotal.add(ratePerNight);

            }
            subTotals.add(subTotal);
        }
        return subTotals;
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
