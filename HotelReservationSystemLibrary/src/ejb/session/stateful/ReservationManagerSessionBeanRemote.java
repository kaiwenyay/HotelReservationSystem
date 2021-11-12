/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateful;

import entity.Reservation;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Remote;
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
@Remote
public interface ReservationManagerSessionBeanRemote {

    public List<RoomType> searchRooms(LocalDate checkInDate, LocalDate checkOutDate);

    public void addReservationItem(BigDecimal subTotal, String roomTypeName) throws InvalidRoomTypeException, InputDataValidationException;

    public Reservation reserveRooms(String username, LocalDate checkInDate, LocalDate checkOutDate) throws InvalidRoomException, InvalidUserException, InvalidReservationException, UnknownPersistenceException, InputDataValidationException;

    
}
