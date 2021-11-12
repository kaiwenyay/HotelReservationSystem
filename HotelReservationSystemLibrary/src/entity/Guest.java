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
            name = "retrieveGuestByEmail",
            query = "SELECT g FROM Guest g WHERE g.username LIKE :inEmail"
    ),
})
public class Guest extends User implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Column(nullable = false, length = 64)
    @NotNull
    @Size(min = 2, max = 64)
    private String name;

    public Guest() {
        super();
    }

    public Guest(String email, String password, String name) {
        super(email, password);
        this.name = name;
    }

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

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
}
