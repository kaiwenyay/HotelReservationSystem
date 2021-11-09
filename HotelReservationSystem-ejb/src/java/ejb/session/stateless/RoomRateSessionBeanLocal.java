/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Local;
import util.enumeration.RateType;
import util.exception.InputDataValidationException;
import util.exception.InvalidRoomRateException;
import util.exception.InvalidRoomTypeException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
@Local
public interface RoomRateSessionBeanLocal {

    public List<RoomRate> retrieveAllRoomRates();
    
    public RoomRate createRoomRate(
            String name, 
            Long roomTypeId, 
            RateType rateType, 
            BigDecimal ratePerNight, 
            LocalDate validityFrom, 
            LocalDate validityTo
    ) throws InvalidRoomTypeException, InvalidRoomRateException, UnknownPersistenceException, InputDataValidationException;
    
}
