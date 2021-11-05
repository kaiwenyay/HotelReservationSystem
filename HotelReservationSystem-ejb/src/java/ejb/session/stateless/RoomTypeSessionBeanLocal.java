/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomType;
import java.util.List;
import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.InvalidRoomTypeException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
@Local
public interface RoomTypeSessionBeanLocal {
    
    public RoomType createRoomType(String name, String description, Integer size, Integer bedCapacity, List<String> amenities, RoomType nextHigherRoomType) throws InvalidRoomTypeException, UnknownPersistenceException, InputDataValidationException;
}
