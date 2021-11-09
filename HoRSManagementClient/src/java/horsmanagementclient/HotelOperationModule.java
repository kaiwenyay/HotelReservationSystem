/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import entity.Room;
import entity.RoomType;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.InvalidStaffRoleException;
import util.enumeration.RoomStatus;
import util.enumeration.StaffRole;
import util.exception.InputDataValidationException;
import util.exception.InvalidRoomException;
import util.exception.InvalidRoomTypeException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;
import util.exception.UpdateRoomTypeException;

/**
 *
 * @author kwpwn
 */
public class HotelOperationModule {
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private Employee currentEmployee; 
    
    private RoomSessionBeanRemote roomSessionBean;
    
    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    
    private RoomRateSessionBeanRemote roomRateSessionBean;
    
    public HotelOperationModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public HotelOperationModule(Employee currentEmployee, RoomSessionBeanRemote roomSessionBean, RoomTypeSessionBeanRemote roomTypeSessionBean, RoomRateSessionBeanRemote roomRateSessionBean) {
        this();
        
        this.currentEmployee = currentEmployee;
        this.roomSessionBean = roomSessionBean;
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.roomRateSessionBean = roomRateSessionBean;
    }

    public void menu() throws InvalidStaffRoleException {
        StaffRole staffRole = currentEmployee.getStaffRole();
        if (staffRole == StaffRole.GUEST_RELATIONS) {
            throw new InvalidStaffRoleException("You don't have ADMIN or OPERATIONS or SALES rights to access the hotel operation module.");
        }
        
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        OUTER:
        while (true) {
            System.out.println("*** HoRS Management Client: Hotel Operation ***\n");
            System.out.println("1: Create New Room Type");
            System.out.println("2: View Room Type Details");
            System.out.println("3. View All Room Types");
            System.out.println("-----------------------");
            System.out.println("4: Create New Room");
            System.out.println("5: Update Room");
            System.out.println("6: Delete Room");
            System.out.println("7. View All Rooms");
            System.out.println("8. View Room Allocation Exception Report");
            System.out.println("-----------------------");
            System.out.println("9: Create New Room Rate");
            System.out.println("10: View Room Rate Details");
            System.out.println("11. View All Room Rates");
            System.out.println("12: Back\n");
            System.out.print(">"); 
            response = sc.nextInt();
            
            switch (response) {
                case 1:
                    doCreateNewRoomType();
                    break;
                case 2:
                    doViewRoomTypeDetails();
                    break;
                case 3:
                    doViewAllRoomTypes();
                    break;
                case 4:
                    doCreateNewRoom();
                    break;
                case 5:
                    doUpdateRoom();
                    break;
                case 6:
                    doDeleteRoom();
                    break;
                case 7:
                    doViewAllRooms();
                    break;
                case 8:
                    doViewRoomAllocationExceptionReport();
                    break;
                case 9:
                    doCreateNewRoomRate();
                    break;
                case 10:
                    doViewRoomRateDetails();
                    break;
                case 11:
                    doViewAllRoomRates();
                    break;
                case 12:
                    break OUTER;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    private void doCreateNewRoomType() {
        StaffRole staffRole = currentEmployee.getStaffRole();
        if (staffRole == StaffRole.SALES || staffRole == StaffRole.GUEST_RELATIONS) {
            System.out.println("You don't have ADMIN or OPERATIONS rights to perform this operation.");
            return;
        }
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Enter Name> ");
        String name = sc.nextLine();
        System.out.print("Enter Description> ");
        String description = sc.nextLine();
        System.out.print("Enter Size: ");
        Integer size = sc.nextInt();
        System.out.print("Enter Bed Capacity: ");
        Integer bedCapacity = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Amenities (Input all in one line): ");
        String amenities = sc.nextLine();
        
        Set<ConstraintViolation<RoomType>> constraintViolations = validator.validate(new RoomType(name, description, size, bedCapacity, amenities));
        
        if (constraintViolations.isEmpty()) {
            try {
                RoomType roomType = roomTypeSessionBean.createRoomType(name, description, size, bedCapacity, amenities);
                System.out.println(String.format("Successfully created room type %s!\n", roomType.getName()));
            } catch (InvalidRoomTypeException | UnknownPersistenceException | InputDataValidationException e) {
                System.out.println("Error: " + e.toString());
            }
        } else {
            showInputDataValidationErrorsForRoomType(constraintViolations);
        }
    }
    
    private void doViewRoomTypeDetails() {
        
        StaffRole staffRole = currentEmployee.getStaffRole();
        if (staffRole == StaffRole.SALES || staffRole == StaffRole.GUEST_RELATIONS) {
            System.out.println("You don't have ADMIN or OPERATIONS rights to perform this operation.");
            return;
        }
        
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        System.out.print("Enter Room Type ID> ");
        Long roomTypeId = sc.nextLong();
        
        try {
            RoomType roomType = roomTypeSessionBean.retrieveRoomTypeById(roomTypeId);
            System.out.printf("%8s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s\n", 
                    "ID", 
                    "Name", 
                    "Description", 
                    "Size", 
                    "Bed Capacity", 
                    "Amenities", 
                    "Disabled", 
                    "Total Rooms", 
                    "Curr Avail Rooms", 
                    "Higher Room Type", 
                    "Lower Room Type", 
                    "Room Rates"
            );
            System.out.printf("%8s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s\n", 
                    roomType.getRoomTypeId().toString(), 
                    roomType.getName(), 
                    roomType.getDescription(), 
                    roomType.getSize().toString(), 
                    roomType.getBedCapacity().toString(), 
                    roomType.getAmenities(),
                    roomType.isDisabled(),
                    roomType.getTotalRooms(),
                    roomType.getCurrentAvailableRooms(),
                    roomType.getNextHigherRoomType(),
                    roomType.getNextLowerRoomType(),
                    roomType.getRoomRates().toString()
            );         
            System.out.println("------------------------");
            System.out.println("1: Update Room Type");
            System.out.println("2: Delete Room Type");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = sc.nextInt();

            if(response == 1) {
                doUpdateRoomType(roomType);
            } else if(response == 2) {
                doDeleteRoomType(roomType);
            }
        }
        catch(InvalidRoomTypeException e)
        {
            System.out.println("Error: " + e.toString());
        }
    }
    
    private void doUpdateRoomType(RoomType roomType) {
        Scanner sc = new Scanner(System.in);        
        String input;
        Integer integerInput;
        Long nextHigherRoomTypeId;
        Long nextLowerRoomTypeId;
        RoomType nextHigherRoomType;
        RoomType nextLowerRoomType;
        
        System.out.print("Enter Name (blank if no change): ");
        input = sc.nextLine().trim();
        if(input.length() > 0) {
            roomType.setName(input);
        }
        
        System.out.print("Enter Description (blank if no change): ");
        input = sc.nextLine().trim();
        if(input.length() > 0) {
            roomType.setDescription(input);
        }
        
        System.out.print("Enter Size (negative number if no change): ");
        integerInput = sc.nextInt();
        if(integerInput >= 0) {
            roomType.setSize(integerInput);
        }
        
        System.out.print("Enter Bed Capacity (negative number if no change): ");
        integerInput = sc.nextInt();
        if(integerInput >= 0) {
            roomType.setBedCapacity(integerInput);
        }
        
        sc.nextLine();
        
        System.out.print("Enter Amenities (blank if no change): ");
        input = sc.nextLine();
        if (input.length() > 0) {
            roomType.setAmenities(input);
        }
        
        sc.nextLine();
        
        if (roomType.isDisabled()) {
            System.out.print("Re-enable Room Type? Y/N: ");
            input = sc.nextLine();
            if (input.toLowerCase().equals("y")) {
                roomType.setDisabled(false);
            } 
        }
        
        while (true) {
            System.out.print("Enter Next Higher Room Type ID (0 if no change)> ");
            nextHigherRoomTypeId = sc.nextLong();
            if (nextHigherRoomTypeId > 0) {
                try {
                    nextHigherRoomType = roomTypeSessionBean.retrieveRoomTypeById(nextHigherRoomTypeId);
                    roomType.setNextHigherRoomType(nextHigherRoomType);
                    break;
                } catch (InvalidRoomTypeException e) {
                    System.out.println("Error: " + e.toString());
                }
            } else {
                break;
            }
        }
        
        while (true) {
            System.out.print("Enter Next Lower Room Type ID (0 if no change)> ");
            nextLowerRoomTypeId = sc.nextLong();
            if (nextLowerRoomTypeId > 0) {
                try {
                    nextLowerRoomType = roomTypeSessionBean.retrieveRoomTypeById(nextLowerRoomTypeId);
                    roomType.setNextLowerRoomType(nextLowerRoomType);
                    break;
                } catch (InvalidRoomTypeException e) {
                    System.out.println("Error: " + e.toString());
                }
            } else {
                break;
            }
        }
        
        Set<ConstraintViolation<RoomType>> constraintViolations = validator.validate(roomType);
        
        if(constraintViolations.isEmpty()) {
            try {
                roomTypeSessionBean.updateRoomType(roomType);
                System.out.println("Room Type updated successfully!\n");
            }
            catch (InvalidRoomTypeException | UpdateRoomTypeException | InputDataValidationException e) {
                System.out.println("Error: " + e.toString());
            }
        }
        else {
            showInputDataValidationErrorsForRoomType(constraintViolations);
        }
    }
    
    private void doDeleteRoomType(RoomType roomType) {
        Scanner scanner = new Scanner(System.in);     
        String input;
        
        System.out.printf("Confirm Room Type %s (ID: %s) (Enter 'Y' to Delete)> ", roomType.getName(), roomType.getRoomTypeId());
        input = scanner.nextLine().trim();
        
        if(input.toLowerCase().equals("y")) {
            try {
                boolean deleted = roomTypeSessionBean.deleteRoomType(roomType.getRoomTypeId());
                if (deleted) {
                    System.out.println("Room Type deleted successfully!\n");
                } else {
                    System.out.println("Room Type still in use. Room Type NOT deleted, disabled instead.");
                }
            } 
            catch (InvalidRoomTypeException e) {
                System.out.println("Error: " + e.toString());
            }
        } else {
            System.out.println("Room Type NOT deleted!\n");
        }
    }
    
    private void doViewAllRoomTypes() {
        Scanner sc= new Scanner(System.in);
        
        List<RoomType> roomTypes = roomTypeSessionBean.retrieveAllRoomTypes(true, true, false, true);
        System.out.printf("%8s%20s%20s%15s%20s%20s%15s%20s%20s%30s%30s%40s\n", 
                    "ID", 
                    "Name", 
                    "Description", 
                    "Size", 
                    "Bed Capacity", 
                    "Amenities", 
                    "Disabled", 
                    "Total Rooms", 
                    "Curr Avail Rooms", 
                    "Higher Room Type", 
                    "Lower Room Type", 
                    "Room Rates"
            );

        for(RoomType roomType : roomTypes) {
            
            System.out.printf("%8s%20s%20s%15s%20s%20s%15s%20s%20s%30s%30s%40s\n", 
                    roomType.getRoomTypeId().toString(), 
                    roomType.getName(), 
                    roomType.getDescription(), 
                    roomType.getSize().toString(), 
                    roomType.getBedCapacity().toString(), 
                    roomType.getAmenities(),
                    roomType.isDisabled(),
                    roomType.getNextHigherRoomType(),
                    roomType.getNextLowerRoomType(),
                    roomType.getRoomRates().toString()
            );
        }

        System.out.print("Press any key to continue...> ");
        sc.nextLine();
    }
   
    private void doCreateNewRoom() {
        StaffRole staffRole = currentEmployee.getStaffRole();
        if (staffRole == StaffRole.SALES || staffRole == StaffRole.GUEST_RELATIONS) {
            System.out.println("You don't have ADMIN or OPERATIONS rights to perform this operation.");
            return;
        }
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        System.out.print("Enter Room Number: ");
        String roomNumber = sc.nextLine();
        RoomStatus roomStatus = null;
        
        while (roomStatus == null) {
            System.out.println("Select the room's status.");
            System.out.println("1. Available");
            System.out.println("2. Not Available");
            System.out.print(">");
            response = sc.nextInt();

            if (response == 1) {
                roomStatus = RoomStatus.AVAILABLE;
            } else if (response == 2) {
                roomStatus = RoomStatus.NOT_AVAILABLE;
            } else {
                System.out.println("Invalid option.");
            }
        }
        
        System.out.print("Enter Room Type ID: ");
        Long roomTypeId = sc.nextLong();
        RoomType roomType = null;
        try {
            roomType = roomTypeSessionBean.retrieveRoomTypeById(roomTypeId);
        } catch (InvalidRoomTypeException e) {
            System.out.println("Error: " + e.toString());
            return;
        }
        Set<ConstraintViolation<Room>> constraintViolations = validator.validate(new Room(roomNumber, roomStatus, roomType));
        
        if (constraintViolations.isEmpty()) {
            try {
                Room room = roomSessionBean.createRoom(roomNumber, roomStatus, roomTypeId);
                System.out.println(String.format("Successfully created room %s!\n", room.getRoomNumber()));
            } catch (InvalidRoomTypeException | InvalidRoomException | UnknownPersistenceException | InputDataValidationException e) {
                System.out.println("Error: " + e.toString());
            }
        } else {
            showInputDataValidationErrorsForRoom(constraintViolations);
        }
    }
    
    // Right now, updating status and room type at the same time causes huge headaches
    // because we have to update the totalRooms variable as well. So, until I gitgud, 
    // I'm just gonna stop users from updating both at the same time.
    private void doUpdateRoom() {
        
        System.out.println("*** IMPORTANT: DO NOT UPDATE BOTH ROOM STATUS AND ROOM TYPE SIMULTANEOUSLY. ONLY CHOOSE ONE TO UPDATE PER OPERATION *** ");
        
        Scanner sc = new Scanner(System.in);   
        System.out.print("Enter Room ID: ");
        Long roomId = sc.nextLong();
        Room room;
        try {
            room = roomSessionBean.retrieveRoomById(roomId, true, true);
        } catch (InvalidRoomException e) {
            System.out.println("Error: " + e.toString());
            return;
        }
        
        String input;
        Integer integerInput;
        Long longInput;
        
        sc.nextLine();
        
        System.out.print("Enter Room Number (blank if no change): ");
        input = sc.nextLine().trim();
        if(input.length() > 0) {
            room.setRoomNumber(input);
        }
        
        System.out.println("*** IMPORTANT: DO NOT UPDATE BOTH ROOM STATUS AND ROOM TYPE SIMULTANEOUSLY. ONLY CHOOSE ONE TO UPDATE PER OPERATION *** ");
        boolean changed = false;
        
        while(true) {
            System.out.println("Select the room's status");
            System.out.println("0. No Change");
            System.out.println("1. Available");
            System.out.println("2. Not Available");
            System.out.print(">");
            integerInput = sc.nextInt();
            
            if(integerInput >= 1 && integerInput <= 2) {
                room.setRoomStatus(RoomStatus.values()[integerInput - 1]);
                changed = true;
                break;
            } else if (integerInput == 0) {
                break;
            } else {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        System.out.print("Enter Room Type ID (0 if no change): ");
        longInput = sc.nextLong();
        
        if(longInput > 0) {
            if (changed) {
                System.out.println("Please do not update both status and room type together.");
                return;
            }
            try {
                RoomType roomType = roomTypeSessionBean.retrieveRoomTypeById(longInput, false, false, true, false);
                room.setRoomType(roomType);
            } catch (InvalidRoomTypeException e) {
                System.out.println("Error: " + e.toString());
            }
            
        }
        
        Set<ConstraintViolation<Room>> constraintViolations = validator.validate(room);
        
        if(constraintViolations.isEmpty()) {
            try {
                roomSessionBean.updateRoom(room);
                System.out.println(String.format("Room %s updated successfully!\n", room.getRoomNumber()));
            } catch (InvalidRoomTypeException | InvalidRoomException | UpdateRoomException | InputDataValidationException e) {
                System.out.println("Error: " + e.toString());
            }
        }
        else {
            showInputDataValidationErrorsForRoom(constraintViolations);
        }
    }

    private void doDeleteRoom() {
        Scanner sc = new Scanner(System.in); 
        System.out.print("Enter Room Id: ");
        Long roomId = sc.nextLong();
        Room room;
        try {
            room = roomSessionBean.retrieveRoomById(roomId, false, false);
        } catch (InvalidRoomException e) {
            System.out.println("Error: " + e.toString());
            return;
        }
        sc.nextLine();
        
        System.out.printf("Confirm Room %s (ID: %s) (Enter 'Y' to Delete)> ", room.getRoomNumber(), room.getRoomId());
        String input = sc.nextLine().trim();
        
        if(input.toLowerCase().equals("y")) {
            try {
                boolean deleted = roomSessionBean.deleteRoom(room.getRoomId());
                if (deleted) {
                    System.out.println(String.format("Room %s deleted successfully!\n", room.getRoomNumber()));
                } else {
                    System.out.println(String.format("Room %s still in use. Room  NOT deleted, disabled instead.", room.getRoomNumber()));
                }
            } 
            catch (InvalidRoomException e) {
                System.out.println("Error: " + e.toString());
            }
        } else {
            System.out.println("Room Type NOT deleted!\n");
        }
    }

    private void doViewAllRooms() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewRoomAllocationExceptionReport() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doCreateNewRoomRate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewRoomRateDetails() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doUpdateRoomRate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doDeleteRoomRate() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doViewAllRoomRates() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    private void showInputDataValidationErrorsForRoomType(Set<ConstraintViolation<RoomType>> constraintViolations) {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    private void showInputDataValidationErrorsForRoom(Set<ConstraintViolation<Room>> constraintViolations) {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}
