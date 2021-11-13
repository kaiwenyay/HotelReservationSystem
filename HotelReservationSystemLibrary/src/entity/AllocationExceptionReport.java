/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

/**
 *
 * @author kwpwn
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "retrieveAllocationExceptionReportByDay",
            query = "SELECT r FROM AllocationExceptionReport r WHERE r.date = :inDate"
    )
})
public class AllocationExceptionReport implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allocationExceptionReportId;
    
    @Column(nullable = false, unique = true, columnDefinition = "DATE")
    @NotNull
    private LocalDate date;
    
    @OneToMany
    private List<Reservation> reservations;

    public AllocationExceptionReport() {
        this.reservations = new ArrayList<>();
    }

    public AllocationExceptionReport(LocalDate date) {
        this.date = date;
    }

    public Long getAllocationExceptionReportId() {
        return allocationExceptionReportId;
    }

    public void setAllocationExceptionReportId(Long allocationExceptionReportId) {
        this.allocationExceptionReportId = allocationExceptionReportId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (allocationExceptionReportId != null ? allocationExceptionReportId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof AllocationExceptionReport)) {
            return false;
        }
        AllocationExceptionReport other = (AllocationExceptionReport) object;
        if ((this.allocationExceptionReportId == null && other.allocationExceptionReportId != null) || (this.allocationExceptionReportId != null && !this.allocationExceptionReportId.equals(other.allocationExceptionReportId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.AllocationExceptionReport[ id=" + allocationExceptionReportId + " ]";
    }

    /**
     * @return the day
     */
    public LocalDate getDate() {
        return date;
    }

    /**
     * @param day the day to set
     */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /**
     * @return the reservations
     */
    public List<Reservation> getReservations() {
        return reservations;
    }

    /**
     * @param reservations the reservations to set
     */
    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }
    
    public void addReservation(Reservation reservation) {
        if (! reservations.contains(reservation)) {
            reservations.add(reservation);
        }
    }
    
}
