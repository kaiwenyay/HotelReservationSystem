/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Guest;
import javax.ejb.Remote;
import util.exception.InputDataValidationException;
import util.exception.InvalidCredentialsException;
import util.exception.InvalidGuestException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
@Remote
public interface GuestSessionBeanRemote {

    public Guest guestLogin(String email, String password) throws InvalidGuestException, InvalidCredentialsException;

    public Guest createGuest(String email, String password) throws InvalidGuestException, UnknownPersistenceException, InputDataValidationException;

    public Guest retrieveGuestByEmail(String email) throws InvalidGuestException;
    
}
