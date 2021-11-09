/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.User;
import javax.ejb.Local;
import util.exception.InvalidUserException;

/**
 *
 * @author kwpwn
 */
@Local
public interface UserSessionBeanLocal {

    public User retrieveUserByUsername(String username) throws InvalidUserException;
    
}
