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
import javax.ejb.EJB;
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
import util.exception.InvalidRoomTypeException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;

/**
 *
 * @author kwpwn
 */
@Stateless
public class RoomSessionBean implements RoomSessionBeanRemote, RoomSessionBeanLocal {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public RoomSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public Room retrieveRoomByName(String roomNumber) throws InvalidRoomException {
        Room room;
        try {
            room = em.createNamedQuery("retrieveRoomByRoomNumber", Room.class)
                    .setParameter("inRoomNumber", roomNumber)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new InvalidRoomException("Room with room number %s does not exist.");
        }
        return room;
    }
    
    @Override
    public Room retrieveRoomById(Long roomId) throws InvalidRoomException {
        Room room = em.find(Room.class, roomId);     
        if(room != null) {
            return room;
        } else {
            throw new InvalidRoomException("Room Type " + roomId + " does not exist!");
        }               
    }
    
    public Room retrieveRoomById(Long roomId, boolean fetchRoomType, boolean fetchRoomTypeRooms) throws InvalidRoomException {
        Room room = em.find(Room.class, roomId);     
        if(room != null) {
            if (fetchRoomType) {
                room.getRoomType();
            }
            if (fetchRoomTypeRooms) {
                room.getRoomType().getRooms().size();
            }
            return room;
        } else {
            throw new InvalidRoomException("Room Type " + roomId + " does not exist!");
        }               
    }
    
    @Override
    public Room retrieveFirstAvailableRoomByRoomType(RoomType roomType) throws InvalidRoomException {
        Room room;
        try {
            room = em.createNamedQuery("retrieveRoomsByRoomTypeAndStatus", Room.class)
                    .setParameter("inRoomTypeId", roomType.getRoomTypeId())
                    .setParameter("inRoomStatus", RoomStatus.AVAILABLE)
                    .setMaxResults(1)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new InvalidRoomException(String.format("No rooms for Room Type %s available at this moment.", roomType.getName()));
        }
        return room;
    }
    
    @Override
    public Room createRoom(String roomNumber, RoomStatus roomStatus, Long roomTypeId) throws InvalidRoomTypeException, InvalidRoomException, UnknownPersistenceException, InputDataValidationException {
        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeById(roomTypeId, false, false, true, false);
        Room room = new Room(roomNumber, roomStatus, null);
        Set<ConstraintViolation<Room>>constraintViolations = validator.validate(room);
        
        if (constraintViolations.isEmpty()) {
            try {
                
                room.setRoomType(roomType);
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
    public Room updateRoom(Room room) throws InvalidRoomTypeException, InvalidRoomException, UpdateRoomException, InputDataValidationException {
        
        if(room != null && room.getRoomId()!= null) {
            Set<ConstraintViolation<Room>>constraintViolations = validator.validate(room);
        
            if(constraintViolations.isEmpty()) {
                Room roomToUpdate = retrieveRoomById(room.getRoomId());
                RoomType roomTypeToUpdate = roomTypeSessionBean.retrieveRoomTypeById(room.getRoomType().getRoomTypeId(), false, false, true, false);

                if (roomToUpdate.getRoomNumber().equals(room.getRoomNumber())) {
                    roomToUpdate.setRoomNumber(room.getRoomNumber());
                    if (roomToUpdate.getRoomStatus() != room.getRoomStatus() && roomToUpdate.getRoomType().getRoomTypeId().longValue() != room.getRoomType().getRoomTypeId().longValue()) {
                        throw new InputDataValidationException("Cannot update status and room type simultaneously.");
                    }
                    if (roomToUpdate.getRoomStatus() != room.getRoomStatus()) {
                        roomToUpdate.setRoomStatus(room.getRoomStatus());
                    }
                    if (roomToUpdate.getRoomType().getRoomTypeId().longValue() != room.getRoomType().getRoomTypeId().longValue()) {
                        roomToUpdate.setRoomType(roomTypeToUpdate);
                    }
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
    public boolean deleteRoom(Long roomId) throws InvalidRoomException {
        Room roomToRemove = retrieveRoomById(roomId);
        RoomStatus roomStatus = roomToRemove.getRoomStatus();
        roomToRemove.disassociate();
        if (roomStatus == RoomStatus.AVAILABLE) {
            em.remove(roomToRemove);
            return true;
        } else {
           roomToRemove.setDisabled(true); 
           return false;
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Room>> constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
