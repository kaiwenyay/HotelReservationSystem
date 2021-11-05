/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.Partner;
import java.util.List;
import javax.ejb.Remote;
import util.exception.InvalidCredentialsException;
import util.exception.InvalidPartnerException;

/**
 *
 * @author kwpwn
 */
@Remote
public interface PartnerSessionBeanRemote {

    public Partner retrievePartnerByUsername(String username);

    public Long createPartner(String username, String password, String partnerName) throws InvalidPartnerException;

    public List<Partner> retrieveAllPartners();

    public Partner partnerLogin(String username, String password) throws InvalidPartnerException, InvalidCredentialsException;
    
}
