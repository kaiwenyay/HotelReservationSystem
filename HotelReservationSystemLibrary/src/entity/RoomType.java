/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

/**
 *
 * @author kwpwn
 */
@Entity
@NamedQueries({
    @NamedQuery(
            name = "retrieveRoomTypeByName",
            query = "SELECT rt FROM RoomType rt WHERE rt.name LIKE :inName"
    ),
    @NamedQuery(
            name = "retrieveAllRoomTypes",
            query = "SELECT rt FROM RoomType rt"
    )
})
public class RoomType implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;
    
    @Column(nullable = false, unique = true, length = 24)
    @NotNull
    @Size(min = 1, max = 24)
    private String name;
    
    @Column(nullable = false, length = 128)
    @NotNull
    @Size(min = 1, max = 128)
    private String description;
    
    @Column(nullable = false)
    @NotNull
    @Positive
    private Integer size;
    
    @Column(nullable = false)
    @NotNull
    @Min(1)
    private Integer bedCapacity;
    
    @Column(nullable = false)
    @NotNull
    @Size(min = 1, max = 20)
    private List<String> amenities;
    
    @Column(nullable = false)
    @NotNull
    private boolean disabled;
    
    @Column(nullable = false)
    @NotNull
    @Min(0)
    private Integer totalRooms;
    
    @Column(nullable = false)
    @NotNull
    @Min(0)
    private Integer currentAvailableRooms;
    
    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms;
    
    @OneToMany(mappedBy = "roomType")
    @JoinColumn(nullable = false)
    private List<RoomRate> roomRates;
    
    @OneToOne(fetch = FetchType.LAZY)
    private RoomType nextHigherRoomType;
    
    @OneToOne(fetch = FetchType.LAZY)
    private RoomType nextLowerRoomType;

    public RoomType() {
        this.totalRooms = 0;
        this.currentAvailableRooms = 0;
        this.rooms = new ArrayList<>();
        this.roomRates = new ArrayList<>();
        this.disabled = false;
    }

    public RoomType(String name, String description, Integer size, Integer bedCapacity, List<String> amenities, RoomType nextHigherRoomType, RoomType nextLowerRoomType, RoomRate roomRate) {
        this();
        
        this.name = name;
        this.description = description;
        this.size = size;
        this.bedCapacity = bedCapacity;
        this.amenities = amenities;
        this.nextHigherRoomType = nextHigherRoomType;
        this.nextLowerRoomType = nextLowerRoomType;
        this.roomRates.add(roomRate);
    }
    
    public Long getRoomTypeId() {
        return roomTypeId;
    }

    public void setRoomTypeId(Long roomTypeId) {
        this.roomTypeId = roomTypeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (roomTypeId != null ? roomTypeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof RoomType)) {
            return false;
        }
        RoomType other = (RoomType) object;
        if ((this.roomTypeId == null && other.roomTypeId != null) || (this.roomTypeId != null && !this.roomTypeId.equals(other.roomTypeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "entity.RoomType[ id=" + roomTypeId + " ]";
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
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the size
     */
    public Integer getSize() {
        return size;
    }

    /**
     * @param size the size to set
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * @return the bedCapacity
     */
    public Integer getBedCapacity() {
        return bedCapacity;
    }

    /**
     * @param bedCapacity the bedCapacity to set
     */
    public void setBedCapacity(Integer bedCapacity) {
        this.bedCapacity = bedCapacity;
    }

    /**
     * @return the amenities
     */
    public List<String> getAmenities() {
        return amenities;
    }

    /**
     * @param amenities the amenities to set
     */
    public void setAmenities(List<String> amenities) {
        this.amenities = amenities;
    }

    /**
     * @return the nextHigherRoomType
     */
    public RoomType getNextHigherRoomType() {
        return nextHigherRoomType;
    }

    /**
     * @param nextHigherRoomType the nextHigherRoomType to set
     */
    public void setNextHigherRoomType(RoomType nextHigherRoomType) {
        this.nextHigherRoomType = nextHigherRoomType;
    }

    /**
     * @return the rooms
     */
    public List<Room> getRooms() {
        return rooms;
    }

    /**
     * @param rooms the rooms to set
     */
    public void setRooms(List<Room> rooms) {
        this.rooms = rooms;
    }

    /**
     * @return the nextLowerRoomType
     */
    public RoomType getNextLowerRoomType() {
        return nextLowerRoomType;
    }

    /**
     * @param nextLowerRoomType the nextLowerRoomType to set
     */
    public void setNextLowerRoomType(RoomType nextLowerRoomType) {
        this.nextLowerRoomType = nextLowerRoomType;
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
    
    /**
     * @return the roomRates
     */
    public List<RoomRate> getRoomRates() {
        return roomRates;
    }

    /**
     * @param roomRates the roomRates to set
     */
    public void setRoomRates(List<RoomRate> roomRates) {
        this.roomRates = roomRates;
    }

    /**
     * @return the inventory
     */
    public Integer getCurrentAvailableRooms() {
        return currentAvailableRooms;
    }

    /**
     * @param currentAvailableRooms the inventory to set
     */
    public void setCurrentAvailableRooms(Integer currentAvailableRooms) {
        this.currentAvailableRooms = currentAvailableRooms;
    }

    /**
     * @return the totalRooms
     */
    public Integer getTotalRooms() {
        return totalRooms;
    }

    /**
     * @param totalRooms the totalRooms to set
     */
    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }
    
    public void decreaseTotalRooms() {
        totalRooms--;
    }
    
    public void decreaseCurrentAvailableRooms() {
        currentAvailableRooms--;
    }
    
    public void addRoom(Room room) {
        if (! rooms.contains(room)) {
            rooms.add(room);
            currentAvailableRooms++;
            totalRooms++;
        }
    }
    
    public void removeRoom(Room room) {
        if (rooms.contains(room)) {
            rooms.remove(room);
            currentAvailableRooms--;
            totalRooms--;
        }
    }
    
    
    public void addRoomRate(RoomRate roomRate) {
        if (! roomRates.contains(roomRate)) {
            roomRates.add(roomRate);
        }
    }
    
    public void removeRoomRate(RoomRate roomRate) {
        if (roomRates.contains(roomRate)) {
            roomRates.remove(roomRate);
        }   
    }
    
    public void disassociateHigherAndLower() {
        if (this.nextLowerRoomType != null && this.nextHigherRoomType != null) {
            
            this.nextLowerRoomType.setNextHigherRoomType(this.nextHigherRoomType);
            this.nextHigherRoomType.setNextLowerRoomType(this.nextLowerRoomType);
            
        } else if (this.nextLowerRoomType == null && this.nextHigherRoomType != null) {
            
            this.nextHigherRoomType.setNextLowerRoomType(null);
            
        } else if (this.nextLowerRoomType != null && this.nextHigherRoomType == null) {
            
            this.nextLowerRoomType.setNextHigherRoomType(null);
            
        }
    }


}
