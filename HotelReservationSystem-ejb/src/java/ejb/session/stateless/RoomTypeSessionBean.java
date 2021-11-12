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
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.exception.InputDataValidationException;
import util.exception.InvalidRoomTypeException;
import util.exception.UnknownPersistenceException;

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
    public RoomType retrieveRoomTypeByName(String name) throws InvalidRoomTypeException {
        RoomType roomType;
        try {
            roomType = em.createNamedQuery("retrieveRoomTypeByName", RoomType.class)
                    .setParameter("inName", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new InvalidRoomTypeException(String.format("Room Type with name %s does not exist.", name));
        }
        return roomType;
    }
    
    @Override
    public RoomType retrieveRoomTypeById(Long roomTypeId) throws InvalidRoomTypeException {
        RoomType roomType = em.find(RoomType.class, roomTypeId);     
        if(roomType != null) {
            return roomType;
        } else {
            throw new InvalidRoomTypeException("Room Type " + roomTypeId + " does not exist!");
        }               
    }
    
    @Override
    public RoomType retrieveRoomTypeById(
            Long roomTypeId, 
            boolean fetchNextHigherRoomType, 
            boolean fetchNextLowerRoomType, 
            boolean fetchRooms, 
            boolean fetchRoomRates
    ) throws InvalidRoomTypeException {
        
        RoomType roomType = em.find(RoomType.class, roomTypeId);     
        if(roomType != null) {
            if (fetchNextHigherRoomType) {
                roomType.getNextHigherRoomType();
            }
            if (fetchNextLowerRoomType) {
                roomType.getNextLowerRoomType();
            }
            if (fetchRooms) {
                roomType.getRooms().size();
            }
            if (fetchRoomRates) {
                roomType.getRoomRates().size();
            }
            return roomType;
        } else {
            throw new InvalidRoomTypeException("Room type " + roomTypeId + " does not exist!");
        }               
    }
    @Override
    public RoomType createRoomType(
            String name, 
            String description, 
            Integer size, 
            Integer bedCapacity, 
            String amenities
    ) throws InvalidRoomTypeException, UnknownPersistenceException, InputDataValidationException {
        
        RoomType roomType = new RoomType(name, description, size, bedCapacity, amenities);
         
        Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(roomType);
        
        if (constraintViolations.isEmpty()) {
            try {

                em.persist(roomType);
                em.flush();
                
            } catch (PersistenceException e) {
                if(e.getCause() != null && e.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    
                    if(e.getCause().getCause() != null && e.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new InvalidRoomTypeException(String.format("Room type with name %s already exists.", name));
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
    public RoomType createRoomType(
            String name, 
            String description, 
            Integer size, 
            Integer bedCapacity, 
            String amenities,
            String nextHigherRoomTypeName,
            String nextLowerRoomTypeName
    ) throws InvalidRoomTypeException, UnknownPersistenceException, InputDataValidationException {
  
        RoomType nextHigherRoomType = null;
        if (nextHigherRoomTypeName != null) {
            nextHigherRoomType = retrieveRoomTypeByName(nextHigherRoomTypeName);
        }
        
        RoomType nextLowerRoomType = null;
        if (nextLowerRoomTypeName != null) {
            
            nextLowerRoomType = retrieveRoomTypeByName(nextLowerRoomTypeName);
        }
        
        RoomType roomType = new RoomType(name, description, size, bedCapacity, amenities, nextHigherRoomType, nextLowerRoomType);
        
        Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(roomType);
        
        if (constraintViolations.isEmpty()) {
            try {
                em.persist(roomType);
                em.flush();
                
                if (nextHigherRoomType != null) {
                    nextHigherRoomType.setNextLowerRoomType(roomType);
                }
                if (nextLowerRoomType != null) {
                    nextLowerRoomType.setNextHigherRoomType(roomType);
                }
                
                
            } catch (PersistenceException e) {
                if(e.getCause() != null && e.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    
                    if(e.getCause().getCause() != null && e.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new InvalidRoomTypeException(String.format("Room type with name %s already exists.", name));
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
    public List<RoomType> retrieveAllRoomTypes(boolean fetchNextHigherRoomType, boolean fetchNextLowerRoomType, boolean fetchRooms, boolean fetchRoomRates) {
        List<RoomType> roomTypes = em.createNamedQuery("retrieveAllRoomTypes", RoomType.class)
                .getResultList();
        if (! fetchNextHigherRoomType && ! fetchNextLowerRoomType && ! fetchRooms && ! fetchRoomRates) {
            return roomTypes;
        }
        for (RoomType rt : roomTypes) {
            if (fetchNextHigherRoomType) {
                rt.getNextHigherRoomType();
            }
            if (fetchNextLowerRoomType) {
                rt.getNextLowerRoomType();
            }
            if (fetchRooms) {
                rt.getRooms().size();
            }
            if (fetchRoomRates) {
                rt.getRoomRates().size();
            }
        }
        return roomTypes;
    }
    
    @Override
    public RoomType updateRoomType(RoomType roomType) throws InvalidRoomTypeException, InputDataValidationException, UnknownPersistenceException {
        
        if(roomType != null && roomType.getRoomTypeId()!= null) {
            Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(roomType);
        
            if(constraintViolations.isEmpty()) {
                try {
                
                    if (roomType.getNextHigherRoomType() != null) {
                        RoomType nextHigherRoomType = retrieveRoomTypeById(roomType.getNextHigherRoomType().getRoomTypeId());
                        nextHigherRoomType.setNextLowerRoomType(roomType);
                    }
                
                    if (roomType.getNextLowerRoomType() != null) {
                        RoomType nextLowerRoomType = retrieveRoomTypeById(roomType.getNextLowerRoomType().getRoomTypeId());
                        nextLowerRoomType.setNextHigherRoomType(roomType);
                    }
                
                em.merge(roomType);
                em.flush();
                
                } catch (PersistenceException e) {
                    if(e.getCause() != null && e.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {

                        if(e.getCause().getCause() != null && e.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                            throw new InvalidRoomTypeException(String.format("Room type with name %s already exists.", roomType.getName()));
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
        } else {
            throw new InvalidRoomTypeException("Room type ID not provided for product to be updated");
        }
        
        return roomType;
    }
    
    @Override
    public boolean deleteRoomType(Long roomTypeId) throws InvalidRoomTypeException {
        RoomType roomTypeToRemove = retrieveRoomTypeById(roomTypeId);
        List<Room> rooms = roomTypeToRemove.getRooms();
        if (rooms.isEmpty()) {
            roomTypeToRemove.disassociateHigherAndLower();
            em.remove(roomTypeToRemove);
            return true;
        } else {
           roomTypeToRemove.setDisabled(true); 
           return false;
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomType>> constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
