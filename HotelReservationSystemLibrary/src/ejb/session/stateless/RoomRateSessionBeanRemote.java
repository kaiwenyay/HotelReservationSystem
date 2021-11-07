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
import javax.ejb.Remote;
import util.enumeration.RateType;
import util.exception.InputDataValidationException;
import util.exception.InvalidRoomRateException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomRateException;

/**
 *
 * @author kwpwn
 */
@Remote
public interface RoomRateSessionBeanRemote {

    public void deleteRoomRate(Long roomRateId) throws InvalidRoomRateException;

    public RoomRate updateRoomRate(RoomRate roomRate) throws InvalidRoomRateException, UpdateRoomRateException, InputDataValidationException;

    public List<RoomRate> retrieveAllRoomRates(boolean fetchRoomType);

    public RoomRate createRoomRate(String name, RoomType roomType, RateType rateType, BigDecimal ratePerNight, LocalDate validityFrom, LocalDate validityTo) throws InvalidRoomRateException, UnknownPersistenceException, InputDataValidationException;

    public RoomRate retrieveRoomRateById(Long roomRateId) throws InvalidRoomRateException;

    public RoomRate retrieveRoomRateByName(String name) throws InvalidRoomRateException;
    
}
