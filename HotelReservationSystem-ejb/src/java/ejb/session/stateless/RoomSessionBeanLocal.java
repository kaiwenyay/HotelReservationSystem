/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.util.List;
import javax.ejb.Local;
import util.enumeration.RoomStatus;
import util.exception.InputDataValidationException;
import util.exception.InvalidRoomException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
@Local
public interface RoomSessionBeanLocal {

    public Room retrieveRoomById(Long roomId) throws InvalidRoomException;
    
    public Room retrieveFirstAvailableRoomByRoomType(RoomType roomType) throws InvalidRoomException;

    public List<Room> retrieveAllRooms();
    
    public Room createRoom(String roomNumber, RoomStatus roomStatus, RoomType roomType) throws InvalidRoomException, UnknownPersistenceException, InputDataValidationException;
    
}
