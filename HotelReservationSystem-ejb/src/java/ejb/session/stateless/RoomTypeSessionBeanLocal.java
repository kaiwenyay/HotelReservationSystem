/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.util.List;
import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.InvalidRoomException;
import util.exception.InvalidRoomTypeException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author kwpwn
 */
@Local
public interface RoomTypeSessionBeanLocal {
    
    public RoomType createRoomType(
            String name, 
            String description, 
            Integer size, 
            Integer bedCapacity, 
            String amenities
    ) throws InvalidRoomTypeException, UnknownPersistenceException, InputDataValidationException;
    
    public RoomType updateRoomType(RoomType roomType) throws InvalidRoomTypeException, UpdateRoomTypeException, InputDataValidationException;

    public RoomType retrieveRoomTypeById(Long productId) throws InvalidRoomTypeException;
    
    public RoomType retrieveRoomTypeById(Long roomTypeId, boolean fetchNextHigherRoomType, boolean fetchNextLowerRoomType, boolean fetchRooms, boolean fetchRoomRates) throws InvalidRoomTypeException;
    
    public RoomType retrieveRoomTypeByName(String name) throws InvalidRoomTypeException;
    
    public List<RoomType> retrieveAllRoomTypes();
            
    public List<RoomType> retrieveAllRoomTypes(boolean fetchNextHigherRoomType, boolean fetchNextLowerRoomType, boolean fetchRooms, boolean fetchRoomRates);
}
