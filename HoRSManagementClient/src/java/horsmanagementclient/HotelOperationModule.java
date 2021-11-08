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
import entity.RoomType;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.InvalidStaffRoleException;
import util.enumeration.StaffRole;
import util.exception.InputDataValidationException;
import util.exception.InvalidRoomTypeException;
import util.exception.UnknownPersistenceException;
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
                    roomType.getAmenities().toString(),
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
   
    private void doCreateNewRoom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doUpdateRoom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void doDeleteRoom() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
}
