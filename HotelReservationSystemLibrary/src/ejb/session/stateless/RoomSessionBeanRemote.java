/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.util.List;
import javax.ejb.Remote;
import util.enumeration.RoomStatus;
import util.exception.InputDataValidationException;
import util.exception.InvalidRoomException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

/**
 *
 * @author kwpwn
 */
@Remote
public interface RoomSessionBeanRemote {

    public Room retrieveRoomByName(String roomNumber) throws InvalidRoomException;

    public Room createRoom(String roomNumber, RoomStatus roomStatus, RoomType roomType) throws InvalidRoomException, UnknownPersistenceException, InputDataValidationException;

    public Room updateRoom(Room room) throws InvalidRoomException, UpdateRoomException, InputDataValidationException;

    public void deleteRoom(Long roomId) throws InvalidRoomException;

    public List<Room> retrieveAllRooms(boolean fetchRoomType);
    
}
