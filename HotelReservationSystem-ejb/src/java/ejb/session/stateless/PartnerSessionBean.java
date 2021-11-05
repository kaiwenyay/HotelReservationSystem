/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.InvalidCredentialsException;
import util.exception.InvalidPartnerException;

/**
 *
 * @author kwpwn
 */
@Stateless
public class PartnerSessionBean implements PartnerSessionBeanRemote, PartnerSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public Partner retrievePartnerByUsername(String username) {
        try {
            Partner partner = em.createNamedQuery("retrievePartnerByUsername", Partner.class)
                    .setParameter("inUsername", username)
                    .getSingleResult();
            return partner;
        } catch (NoResultException e) {
            return null;
        }
    }
    
    @Override
    public Long createPartner(String username, String password, String partnerName) throws InvalidPartnerException {
        Partner partner = retrievePartnerByUsername(username);
        if (partner != null) {
            throw new InvalidPartnerException(String.format("Partner with username %s already exists.", username));
        }
        partner = new Partner(username, password, partnerName);
        em.persist(partner);
        em.flush();
        return partner.getUserId();
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
        if (partner == null) {
            throw new InvalidPartnerException(String.format("Partner with username %s does not exist.", username));
        }
        if (! partner.getPassword().equals(password)) {
            throw new InvalidCredentialsException("Invalid password.");
        }
        return partner;
    }
}
