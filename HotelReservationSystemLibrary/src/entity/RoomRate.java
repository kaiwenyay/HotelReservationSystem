/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
public class RoomRate implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomRateId;
    
    @Column(nullable = false, unique = true, length = 24)
    @NotNull
    @Size(min = 1, max = 24)
    private String name;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private RateType rateType;
    
    @Column(nullable = false, precision = 6, scale = 2)
    @NotNull
    @Positive
    @Digits(integer = 6, fraction = 2)
    private BigDecimal ratePerNight;
    
    @Column(columnDefinition = "TIMESTAMP")
    @Future
    private LocalDateTime validFrom;
    
    @Column(columnDefinition = "TIMESTAMP")
    @Future
    private LocalDateTime validTo;

    public RoomRate(String name, RateType rateType, BigDecimal ratePerNight) {
        this.name = name;
        this.rateType = rateType;
        this.ratePerNight = ratePerNight;
    }

    public RoomRate(String name, RateType rateType, BigDecimal ratePerNight, LocalDateTime validFrom, LocalDateTime validTo) {
        this(name, rateType, ratePerNight);
                
        this.validFrom = validFrom;
        this.validTo = validTo;
    }

    public RoomRate() {
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

    @Override
    public String toString() {
        return "entity.RoomRate[ id=" + roomRateId + " ]";
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
    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    /**
     * @param validFrom the validFrom to set
     */
    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    /**
     * @return the validTo
     */
    public LocalDateTime getValidTo() {
        return validTo;
    }

    /**
     * @param validTo the validTo to set
     */
    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }
    
}
