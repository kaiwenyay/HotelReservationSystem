/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
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
import util.exception.InvalidCredentialsException;
import util.exception.InvalidPartnerException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    public PartnerSessionBean() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @Override
    public Partner retrievePartnerByUsername(String username) throws InvalidPartnerException {
        Partner partner;
        try {
            partner = em.createNamedQuery("retrievePartnerByUsername", Partner.class)
                    .setParameter("inUsername", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new InvalidPartnerException(String.format("Partner with username %s does not exist.", username));
        }
        return partner;
    }
    
    @Override
    public Partner createPartner(String username, String password, String partnerName) throws InvalidPartnerException, UnknownPersistenceException, InputDataValidationException {
        Partner partner = new Partner(username, password, partnerName);
        Set<ConstraintViolation<Partner>>constraintViolations = validator.validate(partner);
        
        if (constraintViolations.isEmpty()) {
            try {
                
                em.persist(partner);
                em.flush();
                
            } catch (PersistenceException e) {
                if(e.getCause() != null && e.getCause().getClass().getName().equals("org.eclipse.persistence.exceptions.DatabaseException")) {
                    
                    if(e.getCause().getCause() != null && e.getCause().getCause().getClass().getName().equals("java.sql.SQLIntegrityConstraintViolationException")) {
                        throw new InvalidPartnerException(String.format("Partner with username %s already exists.", username));
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
        
        return partner;
    }

    @Override
    public List<Partner> retrieveAllPartners() {
        List<Partner> partners = em.createNamedQuery("retrieveAllPartners", Partner.class)
                .getResultList();
        return partners;
    }
    
    @Override
    public Partner partnerLogin(String username, String password) throws InvalidPartnerException, InvalidCredentialsException {
        Partner partner = retrievePartnerByUsername(username); 
        if (! partner.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid password.");
        }
        return partner;
    }
    
    private String prepareInputDataValidationErrorsMessage(Set<ConstraintViolation<Partner>> constraintViolations) {
        String msg = "Input data validation error!:";
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            msg += "\n\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage();
        }
        
        return msg;
    }
}
