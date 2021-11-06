/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import entity.Room;
import entity.User;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.InvalidReservationException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
@Remote
public interface ReservationSessionBeanRemote {

    public Reservation createReservation(BigDecimal totalAmount, LocalDateTime checkInDate, LocalDateTime checkOutDate, LocalDateTime reservationDateTime, List<Room> rooms, User user) throws InvalidReservationException, UnknownPersistenceException, InputDataValidationException;

    public List<Reservation> retrieveAllReservationsByUser(String username);
    
}
