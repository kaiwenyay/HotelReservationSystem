/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import util.enumeration.RateType;

/**
 *
 * @author kwpwn
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "retrieveRoomRateByName",
            query = "SELECT rr FROM RoomRate rr WHERE rr.name LIKE :inName"
    ),
    @NamedQuery(
            name = "retrieveAllRoomRates",
            query = "SELECT rr FROM RoomRate rr"
    )
})
public class RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRateId;
    
    @Column(nullable = false, unique = true, length = 48)
    @NotNull
    @Size(min = 2, max = 48)
    private String name;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private RateType rateType;
    
    @Column(nullable = false, precision = 7, scale = 2)
    @NotNull
    @Positive
    @Digits(integer = 5, fraction = 2)
    private BigDecimal ratePerNight;
    
    @Column(nullable = false)
    @NotNull
    private boolean disabled;
    
    @Column(columnDefinition = "DATE")
    @Future
    private LocalDate validFrom;
    
    @Column(columnDefinition = "DATE")
    @Future
    private LocalDate validTo;
    
    @ManyToOne(optional = false)
    @JoinColumn(nullable = false)
    private RoomType roomType;

    public RoomRate(String name, RateType rateType, BigDecimal ratePerNight) {
        this();
        
        this.name = name;
        this.rateType = rateType;
        this.ratePerNight = ratePerNight;
    }

    public RoomRate(String name, RateType rateType, BigDecimal ratePerNight, LocalDate validFrom, LocalDate validTo) {
        this(name, rateType, ratePerNight);
                
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public RoomRate() {
        this.disabled = false;
    }

    public Long getRoomRateId() {
        return roomRateId;
    }

    public void setRoomRateId(Long roomRateId) {
        this.roomRateId = roomRateId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomRateId != null ? roomRateId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoomRate)) {
            return false;
        }
        RoomRate other = (RoomRate) object;
        if ((this.roomRateId == null && other.roomRateId != null) || (this.roomRateId != null && !this.roomRateId.equals(other.roomRateId))) {
            return false;
        }
        return true;
    }

//    @Override
//    public String toString() {
//        return "entity.RoomRate[ id=" + roomRateId + " ]";
//    }
    
    @Override
    public String toString() {
        return name + " (ID: " + roomRateId + ")";
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

    /**
     * @return the rateType
     */
    public RateType getRateType() {
        return rateType;
    }

    /**
     * @param rateType the rateType to set
     */
    public void setRateType(RateType rateType) {
        this.rateType = rateType;
    }

    /**
     * @return the ratePerNight
     */
    public BigDecimal getRatePerNight() {
        return ratePerNight;
    }

    /**
     * @param ratePerNight the ratePerNight to set
     */
    public void setRatePerNight(BigDecimal ratePerNight) {
        this.ratePerNight = ratePerNight;
    }

    /**
     * @return the validFrom
     */
    public LocalDate getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(LocalDate validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the validTo
     */
    public LocalDate getValidTo() {
        return validTo;
    }

    /**
     * @param validTo the validTo to set
     */
    public void setValidTo(LocalDate validTo) {
        this.validTo = validTo;
    }

    /**
     * @return the roomType
     */
    public RoomType getRoomType() {
        return roomType;
    }

    /**
     * @param roomType the roomType to set
     */
    public void setRoomType(RoomType roomType) {
        if (this.roomType != null) {
            this.roomType.removeRoomRate(this);
        }
        this.roomType = roomType;
        roomType.addRoomRate(this);
    }

    /**
     * @return the disabled
     */
    public boolean isDisabled() {
        return disabled;
    }

    /**
     * @param disabled the disabled to set
     */
    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
   
}
