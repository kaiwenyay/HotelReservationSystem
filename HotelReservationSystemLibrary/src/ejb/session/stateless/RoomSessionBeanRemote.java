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
import util.exception.InvalidRoomTypeException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

/**
 *
 * @author kwpwn
 */
@Remote
public interface RoomSessionBeanRemote {

    public Room retrieveRoomByName(String roomNumber) throws InvalidRoomException;

    public Room createRoom(String roomNumber, RoomStatus roomStatus, Long roomTypeId) throws InvalidRoomTypeException, InvalidRoomException, UnknownPersistenceException, InputDataValidationException;

    public Room updateRoom(Room room) throws InvalidRoomTypeException, InvalidRoomException, UpdateRoomException, InputDataValidationException;

    public void deleteRoom(Long roomId) throws InvalidRoomException;

    public List<Room> retrieveAllRooms(boolean fetchRoomType);

    public Room retrieveFirstAvailableRoomByRoomType(RoomType roomType) throws InvalidRoomException;

    public Room retrieveRoomById(Long roomId, boolean fetchRoomType, boolean fetchRoomTypeRooms) throws InvalidRoomException;
    
}
