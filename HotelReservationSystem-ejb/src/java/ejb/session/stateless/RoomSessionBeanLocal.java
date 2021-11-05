/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import java.util.List;
import javax.ejb.Local;
import util.exception.InvalidRoomException;

/**
 *
 * @author kwpwn
 */
@Local
public interface RoomSessionBeanLocal {

    public Room retrieveRoomById(Long productId) throws InvalidRoomException;

    public List<Room> retrieveAllRooms();
    
}
