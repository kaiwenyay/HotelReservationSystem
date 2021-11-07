/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 *
 * @author kwpwn
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "retrievePartnerByUsername",
            query = "SELECT p FROM Partner p WHERE p.username LIKE :inUsername"
    ),
    @NamedQuery(
            name = "retrieveAllPartners",
            query = "SELECT p FROM Partner p"
    )
})
public class Partner extends User implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Column(nullable = false, length = 32)
    @NotNull
    @Size(min = 1, max = 32)
    private String partnerName;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    public Partner() {
        super();
    }

    public Partner(String username, String password, String partnerName) {
        super(username, password);
        
        this.partnerName = partnerName;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Partner)) {
            return false;
        }
        Partner other = (Partner) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Partner[ id=" + userId + " ]";
    }

    /**
     * @return the partnerName
     */
    public String getPartnerName() {
        return partnerName;
    }

    /**
     * @param partnerName the partnerName to set
     */
    public void setPartnerName(String partnerName) {
        this.partnerName = partnerName;
    }
    
}
