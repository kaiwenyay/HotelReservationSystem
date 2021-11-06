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
import util.exception.InputDataValidationException;
import util.exception.InvalidRoomTypeException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author kwpwn
 */
@Stateless
public class RoomTypeSessionBean implements RoomTypeSessionBeanRemote, RoomTypeSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public RoomTypeSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public RoomType retrieveRoomTypeByName(String name) {
        try {
            RoomType roomType = em.createNamedQuery("retrieveRoomTypeByName", RoomType.class)
                    .setParameter("inName", name)
                    .getSingleResult();
            return roomType;
        } catch (NoResultException e) {
            return null;
        }
    }
    @Override
    public RoomType retrieveRoomTypeById(Long productId) throws InvalidRoomTypeException {
        RoomType roomType = em.find(RoomType.class, productId);     
        if(roomType != null) {
            return roomType;
        } else {
            throw new InvalidRoomTypeException("Room Type " + productId + " does not exist!");
        }               
    }
    
    // Doesn't yet account for creating a new room type whose ranking is inbetween two existing room types
    @Override
    public RoomType createRoomType(String name, String description, Integer size, Integer bedCapacity, List<String> amenities, RoomType nextHigherRoomType, RoomType nextLowerRoomType, RoomRate roomRate) throws InvalidRoomTypeException, UnknownPersistenceException, InputDataValidationException {
        RoomType roomType = new RoomType(name, description, size, bedCapacity, amenities, nextHigherRoomType, nextLowerRoomType, roomRate);
        Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(roomType);
        
        if (constraintViolations.isEmpty()) {
            try {
                
                em.persist(roomType);
                roomRate.setRoomType(roomType);
                em.flush();
                
            } catch (PersistenceException e) {
                if(e.getCause() != null && e.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    
                    if(e.getCause().getCause() != null && e.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new InvalidRoomTypeException(String.format("RoomType with name %s already exists.", name));
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
        
        return roomType;
    }
    
    @Override
    public List<RoomType> retrieveAllRoomTypes() {
        List<RoomType> roomTypes = em.createNamedQuery("retrieveAllRoomTypes", RoomType.class)
                .getResultList();
        return roomTypes;
    }
    
    @Override
    public RoomType updateRoomType(RoomType roomType) throws InvalidRoomTypeException, UpdateRoomTypeException, InputDataValidationException {
        
        if(roomType != null && roomType.getRoomTypeId()!= null) {
            Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(roomType);
        
            if(constraintViolations.isEmpty()) {
                RoomType roomTypeToUpdate = retrieveRoomTypeById(roomType.getRoomTypeId());

                if (roomTypeToUpdate.getName().equals(roomType.getName())) {
                    em.merge(roomType);
                    em.flush();
                } else {
                    throw new UpdateRoomTypeException("Name of Room Type to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new InvalidRoomTypeException("Room Type ID not provided for product to be updated");
        }
        
        return roomType;
    }
    
    @Override
    public void deleteRoomType(Long roomTypeId) throws InvalidRoomTypeException {
        RoomType roomTypeToRemove = retrieveRoomTypeById(roomTypeId);
        List<Room> rooms = roomTypeToRemove.getRooms();
        if (rooms.isEmpty()) {
            roomTypeToRemove.disassociateHigherAndLower();
            em.remove(roomTypeToRemove);
        } else {
           roomTypeToRemove.setDisabled(true); 
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomType>>constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
