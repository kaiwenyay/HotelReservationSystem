/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Reservation;
import javax.ejb.Local;
import util.exception.InvalidReservationException;

/**
 *
 * @author kwpwn
 */
@Local
public interface ReservationSessionBeanLocal {

    public Reservation retrieveReservationById(Long reservationId) throws InvalidReservationException;
    
}
