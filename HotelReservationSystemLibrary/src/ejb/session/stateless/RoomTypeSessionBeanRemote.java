/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.InvalidRoomTypeException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author kwpwn
 */
@Remote
public interface RoomTypeSessionBeanRemote {

    public RoomType retrieveRoomTypeByName(String name);
    
    public RoomType retrieveRoomTypeById(Long productId) throws InvalidRoomTypeException;

    public RoomType createRoomType(String name, String description, Integer size, Integer bedCapacity, List<String> amenities, RoomType nextHigherRoomType, RoomType nextLowerRoomType) throws InvalidRoomTypeException, UnknownPersistenceException, InputDataValidationException;

    public List<RoomType> retrieveAllRoomTypes();

    public RoomType updateRoomType(RoomType roomType) throws InvalidRoomTypeException, UpdateRoomTypeException, InputDataValidationException;

    public void deleteRoomType(Long productId) throws InvalidRoomTypeException;
    
}
