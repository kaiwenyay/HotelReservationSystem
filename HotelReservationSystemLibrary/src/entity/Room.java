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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import util.enumeration.RoomStatus;

/**
 *
 * @author kwpwn
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "retrieveRoomByRoomNumber",
            query = "SELECT r FROM Room r WHERE r.roomNumber LIKE :inRoomNumber"
    ),
    @NamedQuery(
            name = "retrieveAllRooms",
            query = "SELECT r FROM Room r"
    ),
    @NamedQuery(
            name = "retrieveRoomsByRoomTypeAndStatus",
            query = "SELECT r FROM Room r WHERE r.roomType.roomTypeId = :inRoomTypeId AND r.roomStatus = :inRoomStatus"
        )
})
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;
    
    @Column(nullable = false, unique = true, length = 4)
    @NotNull
    @Size(min = 4, max = 4)
    private String roomNumber;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull
    private RoomStatus roomStatus;
    
    @Column(nullable = false)
    @NotNull
    private String enumQueryString;
    
    @Column(nullable = false)
    @NotNull
    private boolean disabled;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private RoomType roomType;

    public Room() {
        this.disabled = false;
    }

    public Room(String roomNumber, RoomStatus roomStatus, RoomType roomType) {
        this();
        
        this.roomNumber = roomNumber;
        this.roomStatus = roomStatus;
        this.roomType = roomType;
        this.enumQueryString = roomStatus.toString();
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomId != null ? roomId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Room)) {
            return false;
        }
        Room other = (Room) object;
        if ((this.roomId == null && other.roomId != null) || (this.roomId != null && !this.roomId.equals(other.roomId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.Room[ id=" + roomId + " ]";
    }

    /**
     * @return the roomNumber
     */
    public String getRoomNumber() {
        return roomNumber;
    }

    /**
     * @param roomNumber the roomNumber to set
     */
    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    /**
     * @return the roomStatus
     */
    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    /**
     * @param roomStatus the roomStatus to set
     */
    public void setRoomStatus(RoomStatus roomStatus) {
        if (roomStatus != this.roomStatus) {
            if (this.roomStatus == RoomStatus.AVAILABLE && roomStatus == RoomStatus.NOT_AVAILABLE) {
                this.roomType.decreaseCurrentAvailableRooms();
            } else if (this.roomStatus == RoomStatus.NOT_AVAILABLE && roomStatus == RoomStatus.AVAILABLE) {
                this.roomType.increaseCurrentAvailableRooms();
            }
            this.roomStatus = roomStatus;
        }
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
        if (this.roomType != null && roomType.getRoomTypeId().longValue() != this.roomType.getRoomTypeId().longValue()) {
           this.roomType.removeRoom(this);
        }
        this.roomType = roomType;
        if (this.roomStatus == RoomStatus.AVAILABLE) {
            roomType.addRoom(this);
        } else {
            roomType.removeRoom(this);
        }
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
    
    public void associate(RoomType roomType) {
        setRoomType(roomType);
        this.roomType.addRoom(this);
    }
    
    public void disassociate() {
        this.roomType.removeRoom(this); 
    }
    
    
    public void allocateRoom() {
        setRoomStatus(RoomStatus.NOT_AVAILABLE);
//        roomType.decreaseCurrentAvailableRooms();
    }
}
