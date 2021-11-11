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
import javax.ejb.Remote;
import util.enumeration.ReservationStatus;
import util.exception.InputDataValidationException;
import util.exception.InvalidReservationException;
import util.exception.InvalidRoomException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
@Remote
public interface ReservationSessionBeanRemote {

    public Reservation createReservation(
            BigDecimal totalAmount, 
            LocalDate checkInDate, 
            LocalDate checkOutDate, 
            LocalDateTime reservationDateTime, 
            ReservationStatus reservationStatus, 
            List<ReservationItem> reservationItems, 
            User user
    ) throws InvalidReservationException, UnknownPersistenceException, InputDataValidationException, InvalidRoomException;

    public List<Reservation> retrieveReservationsByCheckInDate(LocalDate checkInDate);

    public List<Reservation> retrieveReservationsByUser(String username);

    public List<Reservation> retrieveReservationsByCheckInDate(
            LocalDate checkInDate, 
            boolean fetchReservationItems, 
            boolean fetchUser, 
            boolean fetchItemRoomType
    );

    public void allocateRoom(Reservation reservation) throws InvalidRoomException;

    public void manualAllocateRooms(LocalDate date) throws InvalidRoomException;

    public void checkOutGuest(Long reservationId) throws InvalidRoomException, InvalidReservationException;
    
}
