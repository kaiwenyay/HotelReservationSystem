/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.ReservationItem;
import entity.Room;
import entity.RoomType;
import entity.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Schedule;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.AllocationExceptionType;
import util.enumeration.ReservationStatus;
import util.enumeration.RoomStatus;
import util.exception.InputDataValidationException;
import util.exception.InvalidReservationException;
import util.exception.InvalidRoomException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
@Stateless
public class ReservationSessionBean implements ReservationSessionBeanRemote, ReservationSessionBeanLocal {

    @EJB
    private RoomSessionBeanLocal roomSessionBean;

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
        List<Reservation> reservations = em.createNamedQuery("retrieveReservationByCheckInDate", Reservation.class)
                .setParameter("inCheckInDate", checkInDate)
                .getResultList();
        return reservations;
    }
    
    @Override
    public List<Reservation> retrieveReservationsByCheckInDate(
            LocalDate checkInDate, 
            boolean fetchReservationItems, 
            boolean fetchUser, 
            boolean fetchItemRoomType
    ) {
        List<Reservation> reservations = retrieveReservationsByCheckInDate(checkInDate);
        if (! fetchReservationItems && ! fetchUser && ! fetchItemRoomType) {
            return reservations;
        }
        for (Reservation r : reservations) {
            if (fetchReservationItems) {
                r.getReservationItems().size();
            }
            if (fetchUser) {
                r.getUser();
            }
            if (fetchItemRoomType) {
                r.getReservationItems().forEach(x -> x.getReservedRoomType());
            }
        }
        return reservations;
    }
    
    @Override
    public Reservation createReservation(
            BigDecimal totalAmount, 
            LocalDate checkInDate, 
            LocalDate checkOutDate, 
            LocalDateTime reservationDateTime, 
            ReservationStatus reservationStatus, 
            List<ReservationItem> reservationItems, 
            User user
    ) throws InvalidReservationException, UnknownPersistenceException, InputDataValidationException, InvalidRoomException {
        
        Reservation reservation = new Reservation(totalAmount, checkInDate, checkOutDate, reservationDateTime, reservationStatus, reservationItems, user);
        Set<ConstraintViolation<Reservation>> reservationConstraintViolations = validator.validate(reservation);
        
        if (reservationConstraintViolations.isEmpty()) {
            try {
                
                em.persist(reservation);
                user.addReservation(reservation);

//                
//                for (ReservationItem i : reservationItems) {
//                    em.persist(i);
//                }
                
                em.flush();
                
            } catch (PersistenceException e) {
                if(e.getCause() != null && e.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    
                    if(e.getCause().getCause() != null && e.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new InvalidReservationException("Error.");
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
        
        System.out.println(checkInDate);
        System.out.println(reservationDateTime.getHour());
        if (checkInDate.equals(LocalDate.now()) && reservationDateTime.getHour() > 1) {
            System.out.println("here");
            allocateRoom(reservation);
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
    
    @Schedule(hour = "2", minute = "0", info = "allocateRooms")  
    public void allocateRooms() throws InvalidRoomException {
        List<Reservation> reservations = retrieveReservationsByCheckInDate(LocalDate.now(), true, false, true);
        reservations.sort((x,y) -> x.getReservationDateTime().compareTo(y.getReservationDateTime()));
        for (Reservation r : reservations) {

            List<ReservationItem> items = r.getReservationItems();
            for (ReservationItem i : items) {
                RoomType roomType = i.getReservedRoomType();
                try {
                    Room room = roomSessionBean.retrieveFirstAvailableRoomByRoomType(roomType);
                    room.allocateRoom();
                    roomType.decreaseCurrentAvailableRooms();
                    i.setAllocatedRoom(room);
                } catch (InvalidRoomException e) {
                    roomType = roomType.getNextHigherRoomType();
                    if (roomType != null) {
                        try {
                            Room room = roomSessionBean.retrieveFirstAvailableRoomByRoomType(roomType);
                            room.setRoomStatus(RoomStatus.NOT_AVAILABLE);
                            roomType.decreaseCurrentAvailableRooms();
                            i.setAllocatedRoom(room);
                            i.setAllocationExceptionType(AllocationExceptionType.TYPE_ONE);
                        } catch (InvalidRoomException ex) {
                            i.setAllocationExceptionType(AllocationExceptionType.TYPE_TWO);
                        }
                    } else {
                        i.setAllocationExceptionType(AllocationExceptionType.TYPE_TWO);
                    }
                }
            }
            r.setReservationStatus(ReservationStatus.ALLOCATED);
        }
    }
    
    public void allocateRooms(LocalDate date) throws InvalidRoomException {
        List<Reservation> reservations = retrieveReservationsByCheckInDate(date, true, false, true);
        reservations.sort((x,y) -> x.getReservationDateTime().compareTo(y.getReservationDateTime()));
        for (Reservation r : reservations) {

            List<ReservationItem> items = r.getReservationItems();
            for (ReservationItem i : items) {
                RoomType roomType = i.getReservedRoomType();
                try {
                    Room room = roomSessionBean.retrieveFirstAvailableRoomByRoomType(roomType);
                    room.allocateRoom();
                    roomType.decreaseCurrentAvailableRooms();
                    i.setAllocatedRoom(room);
                } catch (InvalidRoomException e) {
                    roomType = roomType.getNextHigherRoomType();
                    if (roomType != null) {
                        try {
                            Room room = roomSessionBean.retrieveFirstAvailableRoomByRoomType(roomType);
                            room.setRoomStatus(RoomStatus.NOT_AVAILABLE);
                            roomType.decreaseCurrentAvailableRooms();
                            i.setAllocatedRoom(room);
                            i.setAllocationExceptionType(AllocationExceptionType.TYPE_ONE);
                        } catch (InvalidRoomException ex) {
                            i.setAllocationExceptionType(AllocationExceptionType.TYPE_TWO);
                        }
                    } else {
                        i.setAllocationExceptionType(AllocationExceptionType.TYPE_TWO);
                    }
                }
            }
            r.setReservationStatus(ReservationStatus.ALLOCATED);
        }
    }
    
    public void allocateRoom(Reservation reservation) throws InvalidRoomException {
        List<ReservationItem> items = reservation.getReservationItems();
        for (ReservationItem i : items) {
            RoomType roomType = i.getReservedRoomType();
            try {
                Room room = roomSessionBean.retrieveFirstAvailableRoomByRoomType(roomType);
                room.allocateRoom();
                roomType.decreaseCurrentAvailableRooms();
                i.setAllocatedRoom(room);
            } catch (InvalidRoomException e) {
                roomType = roomType.getNextHigherRoomType();
                if (roomType != null) {
                    try {
                        Room room = roomSessionBean.retrieveFirstAvailableRoomByRoomType(roomType);
                        room.setRoomStatus(RoomStatus.NOT_AVAILABLE);
                        roomType.decreaseCurrentAvailableRooms();
                        i.setAllocatedRoom(room);
                        i.setAllocationExceptionType(AllocationExceptionType.TYPE_ONE);
                    } catch (InvalidRoomException ex) {
                        i.setAllocationExceptionType(AllocationExceptionType.TYPE_TWO);
                    }
                } else {
                    i.setAllocationExceptionType(AllocationExceptionType.TYPE_TWO);
                }
            }
        }
        reservation.setReservationStatus(ReservationStatus.ALLOCATED);
    }
    
}
