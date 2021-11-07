/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import util.enumeration.AllocationExceptionType;

/**
 *
 * @author kwpwn
 */
@Entity
public class ReservationItem implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long reservationItemId;
    
    @Column(nullable = false, precision = 8, scale = 2)
    @NotNull
    @Positive
    @Digits(integer = 6, fraction = 2)
    private BigDecimal subTotal;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private AllocationExceptionType allocationExceptionType;
    
    @ManyToOne(fetch = FetchType.LAZY)
    private Room allocatedRoom;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private RoomType reservedRoomType;

    public ReservationItem() {
        allocationExceptionType = AllocationExceptionType.NO_EXCEPTION;
    }

    public ReservationItem(BigDecimal subTotal, RoomType reservedRoomType) {
        this.subTotal = subTotal;
        this.reservedRoomType = reservedRoomType;
    }

    public Long getReservationItemId() {
        return reservationItemId;
    }

    public void setReservationItemId(Long reservationItemId) {
        this.reservationItemId = reservationItemId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (reservationItemId != null ? reservationItemId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ReservationItem)) {
            return false;
        }
        ReservationItem other = (ReservationItem) object;
        if ((this.reservationItemId == null && other.reservationItemId != null) || (this.reservationItemId != null && !this.reservationItemId.equals(other.reservationItemId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.ReservationItemEntity[ id=" + reservationItemId + " ]";
    }

    /**
     * @return the subTotal
     */
    public BigDecimal getSubTotal() {
        return subTotal;
    }

    /**
     * @param subTotal the subTotal to set
     */
    public void setSubTotal(BigDecimal subTotal) {
        this.subTotal = subTotal;
    }

    /**
     * @return the allocatedRoom
     */
    public Room getAllocatedRoom() {
        return allocatedRoom;
    }

    /**
     * @param allocatedRoom the allocatedRoom to set
     */
    public void setAllocatedRoom(Room allocatedRoom) {
        this.allocatedRoom = allocatedRoom;
    }

    /**
     * @return the reservedRoomType
     */
    public RoomType getReservedRoomType() {
        return reservedRoomType;
    }

    /**
     * @param reservedRoomType the reservedRoomType to set
     */
    public void setReservedRoomType(RoomType reservedRoomType) {
        this.reservedRoomType = reservedRoomType;
    }

    /**
     * @return the allocationExceptionType
     */
    public AllocationExceptionType getAllocationExceptionType() {
        return allocationExceptionType;
    }

    /**
     * @param allocationExceptionType the allocationExceptionType to set
     */
    public void setAllocationExceptionType(AllocationExceptionType allocationExceptionType) {
        this.allocationExceptionType = allocationExceptionType;
    }
    
}
