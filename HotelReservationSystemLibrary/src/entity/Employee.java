/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import util.enumeration.StaffRole;

/**
 *
 * @author kwpwn
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "retrieveEmployeeByUsername",
            query = "SELECT e FROM Employee e WHERE e.username LIKE :inUsername"
    ),
    @NamedQuery(
            name = "retrieveAllEmployees",
            query = "SELECT e FROM Employee e"
    )
})
public class Employee extends User implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StaffRole staffRole;

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    public Employee() {
        super();
    }

    public Employee(String username, String password, StaffRole staffRole) {
        super(username, password);
        
        this.staffRole = staffRole;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the userId fields are not set
        if (!(object instanceof Employee)) {
            return false;
        }
        Employee other = (Employee) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Employee[ userId=" + userId + " ]";
    }

    /**
     * @return the staffRole
     */
    public StaffRole getStaffRole() {
        return staffRole;
    }

    /**
     * @param staffRole the staffRole to set
     */
    public void setStaffRole(StaffRole staffRole) {
        this.staffRole = staffRole;
    }
    
}
