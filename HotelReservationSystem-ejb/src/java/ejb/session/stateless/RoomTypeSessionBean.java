/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

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
    public RoomType createRoomType(String name, String description, Integer size, Integer bedCapacity, List<String> amenities, RoomType nextHigherRoomType) throws InvalidRoomTypeException, UnknownPersistenceException, InputDataValidationException {
        RoomType roomType = new RoomType(name, description, size, bedCapacity, amenities, nextHigherRoomType);
        Set<ConstraintViolation<RoomType>>constraintViolations = validator.validate(roomType);
        
        if (constraintViolations.isEmpty()) {
            try {
                
                em.persist(roomType);
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
    
    public void updateRoomType(RoomType roomType) {
        
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomType>>constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
