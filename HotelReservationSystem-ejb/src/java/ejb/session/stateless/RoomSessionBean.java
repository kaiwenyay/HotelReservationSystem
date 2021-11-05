/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Room;
import entity.RoomType;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.RoomStatus;
import util.exception.InputDataValidationException;
import util.exception.InvalidRoomException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

/**
 *
 * @author kwpwn
 */
@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public RoomSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Room retrieveRoomByName(String roomNumber) {
        try {
            Room room = em.createNamedQuery("retrieveRoomByRoomNumber", Room.class)
                    .setParameter("inRoomNumber", roomNumber)
                    .getSingleResult();
            return room;
        } catch (NoResultException e) {
            return null;
        }
    }
    @Override
    public Room retrieveRoomById(Long productId) throws InvalidRoomException {
        Room room = em.find(Room.class, productId);     
        if(room != null) {
            return room;
        } else {
            throw new InvalidRoomException("Room Type " + productId + " does not exist!");
        }               
    }
    
    @Override
    public Room createRoom(String roomNumber, RoomStatus roomStatus, RoomType roomType) throws InvalidRoomException, UnknownPersistenceException, InputDataValidationException {
        Room room = new Room(roomNumber, roomStatus, roomType);
        Set<ConstraintViolation<Room>>constraintViolations = validator.validate(room);
        
        if (constraintViolations.isEmpty()) {
            try {
                
                room.associate(roomType);
                em.persist(room);
                em.flush();
                
            } catch (PersistenceException e) {
                if(e.getCause() != null && e.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    
                    if(e.getCause().getCause() != null && e.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new InvalidRoomException(String.format("Room with room number %s already exists.", roomNumber));
                    } else {
                        throw new UnknownPersistenceException(e.getMessage());
                    }
                    
                } else {
                    throw new UnknownPersistenceException(e.getMessage());
                }
            }
        } else {
            throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
        }
        
        return room;
    }
    
    @Override
    public List<Room> retrieveAllRooms() {
        List<Room> rooms = em.createNamedQuery("retrieveAllRooms", Room.class)
                .getResultList();
        return rooms;
    }
    
    @Override
    public List<Room> retrieveAllRooms(boolean fetchRoomType) {
        List<Room> rooms = retrieveAllRooms();
        if (fetchRoomType) {
            rooms.forEach(x -> x.getRoomType());
        }
        return rooms;
    }
    
    @Override
    public Room updateRoom(Room room) throws InvalidRoomException, UpdateRoomException, InputDataValidationException {
        
        if(room != null && room.getRoomId()!= null) {
            Set<ConstraintViolation<Room>>constraintViolations = validator.validate(room);
        
            if(constraintViolations.isEmpty()) {
                Room roomToUpdate = retrieveRoomById(room.getRoomId());

                if (roomToUpdate.getRoomNumber().equals(room.getRoomNumber())) {
                    em.merge(room);
                    em.flush();
                } else {
                    throw new UpdateRoomException("Room number to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new InvalidRoomException("Room ID not provided for product to be updated");
        }
        
        return room;
    }
    
    @Override
    public void deleteRoom(Long roomId) throws InvalidRoomException {
        Room roomToRemove = retrieveRoomById(roomId);
        RoomStatus roomStatus = roomToRemove.getRoomStatus();
        roomToRemove.disassociate();
        if (roomStatus == RoomStatus.AVAILABLE) {
            em.remove(roomToRemove);
        } else {
           roomToRemove.setDisabled(true); 
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Room>>constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
