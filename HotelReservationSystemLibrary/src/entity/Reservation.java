/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import util.enumeration.ReservationStatus;

/**
 *
 * @author kwpwn
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "retrieveReservationsByUser",
            query = "SELECT r FROM Reservation r WHERE r.user.username LIKE :inUsername"
    ),
    @NamedQuery(
            name = "retrieveReservationsByCheckInDate",
            query = "SELECT r FROM Reservation r WHERE r.checkInDate LIKE :inCheckInDate"
    ),
    @NamedQuery(
            name = "retrieveReservationsByPeriod",
            query = "SELECT r FROM Reservation r WHERE :inCheckInDate BETWEEN r.checkInDate AND r.checkOutDate OR :inCheckOutDate BETWEEN r.checkInDate AND r.checkOutDate"
    )
})
public class Reservation implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;
    
    @Column(nullable = false, precision = 8, scale = 2)
    @NotNull
    @Positive
    @Digits(integer = 6, fraction = 2)
    private BigDecimal totalAmount;
    
    @Column(nullable = false, columnDefinition = "DATE")
    @NotNull
    private LocalDate checkInDate;
    
    @Column(nullable = false, columnDefinition = "DATE")
    @NotNull
    @Future
    private LocalDate checkOutDate;
    
    @Column(nullable = false, columnDefinition = "TIMESTAMP")
    @NotNull
    private LocalDateTime reservationDateTime;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private ReservationStatus reservationStatus;
    
    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(nullable = false)
    private List<ReservationItem> reservationItems;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    public Reservation() {
        this.reservationItems = new ArrayList<>();
    }

    public Reservation(BigDecimal totalAmount, LocalDate checkInDate, LocalDate checkOutDate, LocalDateTime reservationDateTime, ReservationStatus reservationStatus, List<ReservationItem> reservationItems, User user) {
        this();
        
        this.totalAmount = totalAmount;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;      
        this.reservationDateTime = reservationDateTime;
        this.reservationStatus = reservationStatus;
        this.reservationItems = reservationItems;
        this.user = user;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationId != null ? reservationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Reservation)) {
            return false;
        }
        Reservation other = (Reservation) object;
        if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Reservation[ id=" + reservationId + " ]";
    }

    /**
     * @return the totalAmount
     */
    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    /**
     * @param totalAmount the totalAmount to set
     */
    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * @return the checkInDate
     */
    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    /**
     * @param checkInDate the checkInDate to set
     */
    public void setCheckInDate(LocalDate checkInDate) {
        this.checkInDate = checkInDate;
    }

    /**
     * @return the checkOutDate
     */
    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }

    /**
     * @param checkOutDate the checkOutDate to set
     */
    public void setCheckOutDate(LocalDate checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    /**
     * @return the user
     */
    public User getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * @return the reservationDateTime
     */
    public LocalDateTime getReservationDateTime() {
        return reservationDateTime;
    }

    /**
     * @param reservationDateTime the reservationDateTime to set
     */
    public void setReservationDateTime(LocalDateTime reservationDateTime) {
        this.reservationDateTime = reservationDateTime;
    }

    /**
     * @return the reservationItems
     */
    public List<ReservationItem> getReservationItems() {
        return reservationItems;
    }

    /**
     * @param reservationItems the reservationItems to set
     */
    public void setReservationItems(List<ReservationItem> reservationItems) {
        this.reservationItems = reservationItems;
    }

    /**
     * @return the reservationStatus
     */
    public ReservationStatus getReservationStatus() {
        return reservationStatus;
    }

    /**
     * @param reservationStatus the reservationStatus to set
     */
    public void setReservationStatus(ReservationStatus reservationStatus) {
        this.reservationStatus = reservationStatus;
    }
    
}
