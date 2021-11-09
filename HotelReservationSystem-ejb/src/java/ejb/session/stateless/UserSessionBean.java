/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.User;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.InvalidUserException;

/**
 *
 * @author kwpwn
 */
@Stateless
public class UserSessionBean implements UserSessionBeanRemote, UserSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public User retrieveUserByUsername(String username) throws InvalidUserException {
        User user;
        try {
            user = em.createNamedQuery("retrieveUserByUsername", User.class)
                    .setParameter("inUsername", username)
                    .getSingleResult();
        } catch (NoResultException e) {
            throw new InvalidUserException(String.format("User with username %s does not exist.", username));
        }
        return user;
    }
}
