/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.Size;

/**
 *
 * @author kwpwn
 */
@Entity
public class Guest extends User implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Column(length = 254) 
    @Size(min = 1, max = 254)
    private String email;
    
    @Column(length = 8)
    @Size(min = 8, max = 8)
    private String mobilePhoneNumber;
    
    @Column(length = 9)
    @Size(min = 9, max = 9)
    private String passportNumber;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the userId fields are not set
        if (!(object instanceof Guest)) {
            return false;
        }
        Guest other = (Guest) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Guest[ userId=" + userId + " ]";
    }
    
}
