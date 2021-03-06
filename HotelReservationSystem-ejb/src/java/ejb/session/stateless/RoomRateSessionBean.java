/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDate;
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
import util.enumeration.RateType;
import util.exception.InputDataValidationException;
import util.exception.InvalidRoomRateException;
import util.exception.InvalidRoomTypeException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomRateException;

/**
 *
 * @author kwpwn
 */
@Stateless
public class RoomRateSessionBean implements RoomRateSessionBeanRemote, RoomRateSessionBeanLocal {

    @EJB
    private RoomTypeSessionBeanLocal roomTypeSessionBean;

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public RoomRateSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    @Override
    public RoomRate retrieveRoomRateByName(String name) throws InvalidRoomRateException {
        try {
            RoomRate roomRate = em.createNamedQuery("retrieveRoomRateByName", RoomRate.class)
                    .setParameter("inName", name)
                    .getSingleResult();
            return roomRate;
        } catch (NoResultException e) {
            throw new InvalidRoomRateException("Room Rate " + name + " does not exist!");
        }
    }
    
    @Override
    public RoomRate retrieveRoomRateByName(String name, boolean fetchRoomType) throws InvalidRoomRateException {
        RoomRate roomRate = retrieveRoomRateByName(name);     
        if(roomRate != null) {
            if (fetchRoomType) {
                roomRate.getRoomType();
            }
            return roomRate;
        } else {
            throw new InvalidRoomRateException("Room Rate " + name + " does not exist!");
        }               
    }
    
    @Override
    public RoomRate retrieveRoomRateById(Long roomRateId) throws InvalidRoomRateException {
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);     
        if(roomRate != null) {
            return roomRate;
        } else {
            throw new InvalidRoomRateException("Room Rate " + roomRateId + " does not exist!");
        }               
    }
    
    @Override
    public RoomRate retrieveRoomRateById(Long roomRateId, boolean fetchRoomType) throws InvalidRoomRateException {
        RoomRate roomRate = em.find(RoomRate.class, roomRateId);     
        if(roomRate != null) {
            if (fetchRoomType) {
                roomRate.getRoomType();
            }
            return roomRate;
        } else {
            throw new InvalidRoomRateException("Room Rate " + roomRateId + " does not exist!");
        }               
    }
    
    @Override
    public RoomRate createRoomRate(
            String name, 
            Long roomTypeId, 
            RateType rateType, 
            BigDecimal ratePerNight, 
            LocalDate validityFrom, 
            LocalDate validityTo
    ) throws InvalidRoomTypeException, InvalidRoomRateException, UnknownPersistenceException, InputDataValidationException {
        
        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeById(roomTypeId);
        
        RoomRate roomRate = new RoomRate(name, rateType, ratePerNight, validityFrom, validityTo);
        Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(roomRate);
        
        if (constraintViolations.isEmpty()) {
            try {
                
                em.persist(roomRate);
                roomRate.setRoomType(roomType);
                em.flush();
                
            } catch (PersistenceException e) {
                if(e.getCause() != null && e.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    
                    if(e.getCause().getCause() != null && e.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new InvalidRoomRateException(String.format("Room Rate with name %s already exists.", name));
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
        
        return roomRate;
    }
    
    @Override
    public RoomRate createRoomRate(
            String name, 
            String roomTypeName, 
            RateType rateType, 
            BigDecimal ratePerNight, 
            LocalDate validityFrom, 
            LocalDate validityTo
    ) throws InvalidRoomTypeException, InvalidRoomRateException, UnknownPersistenceException, InputDataValidationException {
        
        RoomType roomType = roomTypeSessionBean.retrieveRoomTypeByName(roomTypeName);
        
        RoomRate roomRate = new RoomRate(name, rateType, ratePerNight, validityFrom, validityTo);
        Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(roomRate);
        
        if (constraintViolations.isEmpty()) {
            try {
                
                em.persist(roomRate);
                roomRate.setRoomType(roomType);
                em.flush();
                
            } catch (PersistenceException e) {
                if(e.getCause() != null && e.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    
                    if(e.getCause().getCause() != null && e.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new InvalidRoomRateException(String.format("Room Rate with name %s already exists.", name));
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
        
        return roomRate;
    }
    
    @Override
    public List<RoomRate> retrieveAllRoomRates() {
        List<RoomRate> roomRates = em.createNamedQuery("retrieveAllRoomRates", RoomRate.class)
                .getResultList();
        return roomRates;
    }
    
    @Override
    public List<RoomRate> retrieveAllRoomRates(boolean fetchRoomType) {
        List<RoomRate> roomRates = retrieveAllRoomRates();
        if (fetchRoomType) {
            roomRates.forEach(x -> x.getRoomType());
        }
        return roomRates;
    }
    
    @Override
    public RoomRate updateRoomRate(
            Long roomRateId,
            String name,
            Long roomTypeId,
            RateType rateType,
            BigDecimal ratePerNight,
            LocalDate validFrom,
            LocalDate validTo
    ) throws InvalidRoomTypeException, InvalidRoomRateException, UpdateRoomRateException, InputDataValidationException {
        
        RoomRate roomRateToUpdate;
        
        if(roomRateId != null) {
            RoomRate roomRate = new RoomRate(name, rateType, ratePerNight, validFrom, validTo);
            
            Set<ConstraintViolation<RoomRate>>constraintViolations = validator.validate(roomRate);
        
            if(constraintViolations.isEmpty()) {
                roomRateToUpdate = retrieveRoomRateById(roomRateId);
                RoomType newRoomType = roomTypeSessionBean.retrieveRoomTypeById(roomTypeId);

                if (roomRateToUpdate.getName().equals(roomRate.getName())) {
                    roomRateToUpdate.setName(name);
                    roomRateToUpdate.setRoomType(newRoomType);
                    roomRateToUpdate.setRateType(rateType);
                    roomRateToUpdate.setRatePerNight(ratePerNight);
                    roomRateToUpdate.setValidFrom(validFrom);
                    roomRateToUpdate.setValidTo(validTo);
                } else {
                    throw new UpdateRoomRateException("Room Rate name to be updated does not match the existing record");
                }
            } else {
                throw new InputDataValidationException(prepareInputDataValidationErrorsMessage(constraintViolations));
            }
        } else {
            throw new InvalidRoomRateException("Room Rate ID not provided for product to be updated");
        }
        
        return roomRateToUpdate;
    }
    
    @Override
    public boolean deleteRoomRate(Long roomRateId) throws InvalidRoomRateException {
        RoomRate roomRateToRemove = retrieveRoomRateById(roomRateId);
        RoomType roomType = roomRateToRemove.getRoomType();
        LocalDate validFrom = roomRateToRemove.getValidFrom();
        LocalDate validTo = roomRateToRemove.getValidTo();
        LocalDate dateNow = LocalDate.now();
        if ((validTo != null && dateNow.isAfter(validTo)) ||  (validFrom != null && validFrom.isAfter(dateNow) && validTo.isAfter(dateNow))) {
            roomType.removeRoomRate(roomRateToRemove);
            em.remove(roomRateToRemove);
            return true;
        } else {
            roomRateToRemove.setDisabled(true); 
            return false;
        }
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<RoomRate>> constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
