/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.AllocationExceptionReportSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.AllocationExceptionReport;
import entity.Employee;
import entity.Reservation;
import entity.ReservationItem;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.AllocationExceptionType;
import util.enumeration.InvalidStaffRoleException;
import util.enumeration.RateType;
import util.enumeration.RoomStatus;
import util.enumeration.StaffRole;
import util.exception.InputDataValidationException;
import util.exception.InvalidReportException;
import util.exception.InvalidRoomException;
import util.exception.InvalidRoomRateException;
import util.exception.InvalidRoomTypeException;
import util.exception.UnknownPersistenceException;
import util.exception.UpdateRoomException;
import util.exception.UpdateRoomRateException;
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
    
    private AllocationExceptionReportSessionBeanRemote allocationExceptionReportSessionBean;
    
    public HotelOperationModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public HotelOperationModule(Employee currentEmployee, RoomSessionBeanRemote roomSessionBean, RoomTypeSessionBeanRemote roomTypeSessionBean, RoomRateSessionBeanRemote roomRateSessionBean, AllocationExceptionReportSessionBeanRemote allocationExceptionReportSessionBean) {
        this();
        
        this.currentEmployee = currentEmployee;
        this.roomSessionBean = roomSessionBean;
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.roomRateSessionBean = roomRateSessionBean;
        this.allocationExceptionReportSessionBean = allocationExceptionReportSessionBean;
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
        System.out.print("Enter Description: ");
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
            } catch (InputDataValidationException e) {
                System.out.println(e.getMessage() + "\n");
            } catch (InvalidRoomTypeException e) {
                System.out.println("An error has occured while creating the room type: " + e.getMessage());
            } catch (UnknownPersistenceException e) {
                System.out.println("An unknown error has occured while creating the room type: " + e.getMessage());
            }
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
        
        System.out.print("Enter Room Type ID: ");
        Long roomTypeId = sc.nextLong();
        
        try {
            RoomType roomType = roomTypeSessionBean.retrieveRoomTypeById(roomTypeId, true, true, false, true);
            System.out.printf("%8s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s\n", 
                    "ID", 
                    "Name", 
                    "Description", 
                    "Size", 
                    "Bed Capacity", 
                    "Amenities", 
                    "Disabled", 
                    "Total Rooms",  
                    "Higher Room Type", 
                    "Lower Room Type", 
                    "Room Rates"
            );
            System.out.printf("%8s%20s%20s%20s%20s%20s%20s%20s%20s%20s%20s\n", 
                    roomType.getRoomTypeId().toString(), 
                    roomType.getName(), 
                    roomType.getDescription(), 
                    roomType.getSize().toString(), 
                    roomType.getBedCapacity().toString(), 
                    roomType.getAmenities(),
                    roomType.isDisabled(),
                    roomType.getTotalRooms(),
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
            
        } catch(InvalidRoomTypeException e) {
            System.out.println("An error occured while retrieving the room type: " + e.toString());
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
        
        System.out.print("Enter Size (0 if no change): ");
        integerInput = sc.nextInt();
        if(integerInput > 0) {
            roomType.setSize(integerInput);
        }
        
        System.out.print("Enter Bed Capacity (0 if no change): ");
        integerInput = sc.nextInt();
        if(integerInput > 0) {
            roomType.setBedCapacity(integerInput);
        }
        
        sc.nextLine();
        
        System.out.print("Enter Amenities (blank if no change): ");
        input = sc.nextLine();
        if (input.length() > 0) {
            roomType.setAmenities(input);
        }
        
        sc.nextLine();
        
        while (true) {
            
            System.out.print("Enter Next Higher Room Type ID (0 if no change)> ");
            nextHigherRoomTypeId = sc.nextLong();
            
            if (nextHigherRoomTypeId > 0) {
                
                try {
                    
                    nextHigherRoomType = roomTypeSessionBean.retrieveRoomTypeById(nextHigherRoomTypeId);
                    roomType.setNextHigherRoomType(nextHigherRoomType);
                    
                    break;
                } catch (InvalidRoomTypeException e) {
                    System.out.println("An error occured whlie retrieving the room type: " + e.getMessage());
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
                    System.out.println("An error occured whlie retrieving the room type: " + e.getMessage());
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
            } catch (InputDataValidationException e) {
                System.out.println(e.getMessage() + "\n");
            } catch (InvalidRoomTypeException e) {
                System.out.println("An error has occured while creating the room type: " + e.getMessage());
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
                System.out.println("An error has occured while retrieveing the room type: " + e.toString());
            }
        } else {
            System.out.println("Room Type NOT deleted!\n");
        }
    }
    
    private void doViewAllRoomTypes() {
        Scanner sc= new Scanner(System.in);
        
        List<RoomType> roomTypes = roomTypeSessionBean.retrieveAllRoomTypes(true, true, false, true);
        System.out.printf("%8s%20s%20s%15s%20s%20s%15s%20s%30s%30s%40s\n", 
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
            
            System.out.printf("%8s%20s%20s%15s%20s%20s%15s%20s%30s%30s%40s\n", 
                    roomType.getRoomTypeId().toString(), 
                    roomType.getName(), 
                    roomType.getDescription(), 
                    roomType.getSize().toString(), 
                    roomType.getBedCapacity().toString(), 
                    roomType.getAmenities(),
                    roomType.isDisabled(),
                    roomType.getTotalRooms(),
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
        Scanner sc= new Scanner(System.in);
        
        List<Room> rooms = roomSessionBean.retrieveAllRooms(true);
        System.out.printf("%8s%20s%20s%30s\n", "Room ID", "Room Number", "Room Status", "Room Type");

        for(Room r : rooms) {
            System.out.printf("%8s%20s%20s%30s\n", r.getRoomId().toString(), r.getRoomNumber(), r.getRoomStatus().toString(), r.getRoomType().toString());
        }

        System.out.print("Press any key to continue...> ");
        sc.nextLine();
    }

    private void doViewRoomAllocationExceptionReport() {
        AllocationExceptionReport report;
        try {
            report = allocationExceptionReportSessionBean.retrieveReport(LocalDate.now(), true, true, true, true);
        } catch (InvalidReportException e) {
            System.out.println("Error: " + e.toString());
            return;
        }
        
        List<Reservation> reservations = report.getReservations();
        
        System.out.println(String.format("*** Allocation Exception Report for %s ***", LocalDate.now()));
        
        System.out.printf("%8s%15s%25s%25s%25s\n", 
                    "Reservation ID", 
                    "Item", 
                    "Exception Type", 
                    "Reserved Room Type", 
                    "Allocated Room"
            );
        
        for (Reservation r : reservations) {
            List<ReservationItem> items = r.getReservationItems();
            for (ReservationItem i : items) {
                if (i.getAllocationExceptionType() != AllocationExceptionType.NO_EXCEPTION) {
                    System.out.printf("%13s%15s%25s%25s%25s\n", 
                        r.getReservationId(),
                        i.getReservationItemId(),
                        i.getAllocationExceptionType(),
                        i.getReservedRoomType(), 
                        i.getAllocatedRoom()
                    );
                }
            }
        }
        
    }

    private void doCreateNewRoomRate() {
        StaffRole staffRole = currentEmployee.getStaffRole();
        if (staffRole == StaffRole.OPERATIONS || staffRole == StaffRole.GUEST_RELATIONS) {
            System.out.println("You don't have ADMIN or SALES rights to perform this operation.");
            return;
        }
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Room Type Id: ");
        Long roomTypeId = sc.nextLong();
        RateType rateType = null;
        while (rateType == null) {
            System.out.println("Select the rate type.");
            System.out.println("1. Published");
            System.out.println("2. Normal");
            System.out.println("3. Peak");
            System.out.println("4. Promotion");
            System.out.print(">");
            response = sc.nextInt();

            if (response == 1) {
                rateType = RateType.PUBLISHED;
            } else if (response == 2) {
                rateType = RateType.NORMAL;
            } else if (response == 3) {
                rateType = RateType.PEAK;
            } else if (response == 4) {
                rateType = RateType.PROMOTION;
            } else {
                System.out.println("Invalid option.");
            }
        }
        
        System.out.print("Enter Rate Per Night: ");
        BigDecimal ratePerNight = new BigDecimal(sc.nextDouble());
        sc.nextLine();
        
        LocalDate validFrom = null;
        LocalDate validTo = null;
        
        if (rateType == RateType.PEAK || rateType == RateType.PROMOTION) {
            System.out.print("Enter validity period start date (YYYY-MM-DD) : ");
            validFrom = LocalDate.parse(sc.nextLine(), DateTimeFormatter.ISO_DATE);
            System.out.print("Enter validity period end date (YYYY-MM-DD) : ");
            validTo = LocalDate.parse(sc.nextLine(), DateTimeFormatter.ISO_DATE);
        }
        
        Set<ConstraintViolation<RoomRate>> constraintViolations = validator.validate(new RoomRate(name, rateType, ratePerNight, validFrom, validTo));
        
        if (constraintViolations.isEmpty()) {
            try {
                RoomRate roomRate = roomRateSessionBean.createRoomRate(name, roomTypeId, rateType, ratePerNight, validFrom, validTo);
                System.out.println(String.format("Successfully created room type %s!\n", roomRate.getName()));
            } catch (InvalidRoomTypeException | InvalidRoomRateException | UnknownPersistenceException | InputDataValidationException e) {
                System.out.println("Error: " + e.toString());
            }
        } else {
            showInputDataValidationErrorsForRoomRate(constraintViolations);
        }
    }

    private void doViewRoomRateDetails() {
        StaffRole staffRole = currentEmployee.getStaffRole();
        if (staffRole == StaffRole.OPERATIONS || staffRole == StaffRole.GUEST_RELATIONS) {
            System.out.println("You don't have ADMIN or SALES rights to perform this operation.");
            return;
        }
        
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        System.out.print("Enter Room Type ID: ");
        Long roomRateId = sc.nextLong();
        
        try {
            RoomRate roomRate = roomRateSessionBean.retrieveRoomRateById(roomRateId, true);
            System.out.printf("%8s%25s%25s%15s%20s%15s%15s%15s\n", 
                    "ID", 
                    "Name", 
                    "Room Type",
                    "Rate Type", 
                    "Rate Per Night", 
                    "Disabled",
                    "Valid From", 
                    "Valid To"
            );
            System.out.printf("%8s%25s%25s%15s%20s%15s%15s%15s\n", 
                    roomRate.getRoomRateId().toString(), 
                    roomRate.getName(), 
                    roomRate.getRoomType(), 
                    roomRate.getRateType().toString(), 
                    roomRate.getRatePerNight().toString(), 
                    roomRate.isDisabled(),
                    roomRate.getValidFrom(),
                    roomRate.getValidTo()
            );         
            System.out.println("------------------------");
            System.out.println("1: Update Room Rate");
            System.out.println("2: Delete Room Rate");
            System.out.println("3: Back\n");
            System.out.print("> ");
            response = sc.nextInt();

            if(response == 1) {
                doUpdateRoomRate(roomRate);
            } else if(response == 2) {
                doDeleteRoomRate(roomRate);
            }
        }
        catch(InvalidRoomRateException e)
        {
            System.out.println("Error: " + e.toString());
        }
    }

    private void doUpdateRoomRate(RoomRate roomRate) {
        Scanner sc = new Scanner(System.in);        
        String input;
        Integer integerInput;
        Double doubleInput;
        
        String name = roomRate.getName();
        Long roomTypeId;
        RateType rateType = null;
        BigDecimal ratePerNight;
        LocalDate validFrom;
        LocalDate validTo;
        
        System.out.print("Enter New Room Type Id (0 if no change): ");
        roomTypeId = sc.nextLong();
        if (roomTypeId == 0) {
            roomTypeId = roomRate.getRoomType().getRoomTypeId();
        }
        
        while(true) {
            System.out.println("Select the rate type: ");
            System.out.println("0. No Change");
            System.out.println("1. Published");
            System.out.println("2. Normal");
            System.out.println("3. Peak");
            System.out.println("4. Promotion");
            System.out.print(">");
            integerInput = sc.nextInt();
            
            if(integerInput >= 1 && integerInput <= 4) {
                rateType = RateType.values()[integerInput - 1];
                break;
            } else if (integerInput == 0) {
                rateType = roomRate.getRateType();
                break;
            } else {
                System.out.println("Invalid option, please try again!\n");
            }
        }
        
        System.out.print("Enter Rate Per Night (0 if no change): ");
        doubleInput = sc.nextDouble();
        if(doubleInput > 0) {
            ratePerNight = new BigDecimal(doubleInput);
        } else {
            ratePerNight = roomRate.getRatePerNight();
        }
        
        sc.nextLine();
        
        System.out.print("Enter Validity Period Start Date (YYYY-MM-DD) (blank if no change): ");
        input = sc.nextLine();
        if(input.length() > 0) {
            validFrom = LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
        } else {
            validFrom = roomRate.getValidFrom();
        }
        
        System.out.print("Enter Validity Period End Date (YYYY-MM-DD) (blank if no change): ");
        input = sc.nextLine();
        if(input.length() > 0) {
            validTo = LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
        } else {
            validTo = roomRate.getValidTo();
        }
        
        Set<ConstraintViolation<RoomRate>> constraintViolations = validator.validate(new RoomRate(name, rateType, ratePerNight, validFrom, validTo));
        
        if(constraintViolations.isEmpty()) {
            try {
                roomRateSessionBean.updateRoomRate(roomRate.getRoomRateId(), name, roomTypeId, rateType, ratePerNight, validFrom, validTo);
                System.out.println("Room Rate updated successfully!\n");
            }
            catch (InvalidRoomTypeException | InvalidRoomRateException | UpdateRoomRateException | InputDataValidationException e) {
                System.out.println("Error: " + e.toString());
            }
        }
        else {
            showInputDataValidationErrorsForRoomRate(constraintViolations);
        }
    }

    private void doDeleteRoomRate(RoomRate roomRate) {
        
        Scanner sc = new Scanner(System.in);
        System.out.printf("Confirm Room %s (ID: %s) (Enter 'Y' to Delete)> ", roomRate.getName(), roomRate.getRoomRateId());
        String input = sc.nextLine().trim();
        
        if(input.toLowerCase().equals("y")) {
            try {
                boolean deleted = roomRateSessionBean.deleteRoomRate(roomRate.getRoomRateId());
                if (deleted) {
                    System.out.println(String.format("Room Rate %s deleted successfully!\n", roomRate.getName()));
                } else {
                    System.out.println(String.format("Room %s still in use. Room  NOT deleted, disabled instead.", roomRate.getName()));
                }
            } 
            catch (InvalidRoomRateException e) {
                System.out.println("Error: " + e.toString());
            }
        } else {
            System.out.println("Room Type NOT deleted!\n");
        }
    }

    private void doViewAllRoomRates() {
        Scanner sc = new Scanner(System.in);
        
        List<RoomRate> roomRates = roomRateSessionBean.retrieveAllRoomRates(true);
        System.out.printf("%8s%25s%25s%15s%20s%15s%15s%15s\n", 
                "ID", 
                "Name", 
                "Room Type",
                "Rate Type", 
                "Rate Per Night", 
                "Disabled",
                "Valid From", 
                "Valid To"
        ); 

        for(RoomRate roomRate : roomRates) {
            
            System.out.printf("%8s%25s%25s%15s%20s%15s%15s%15s\n", 
                roomRate.getRoomRateId().toString(), 
                roomRate.getName(), 
                roomRate.getRoomType(), 
                roomRate.getRateType().toString(), 
                roomRate.getRatePerNight().toString(), 
                roomRate.isDisabled(),
                roomRate.getValidFrom(),
                roomRate.getValidTo()
        );
        }

        System.out.print("Press any key to continue...> ");
        sc.nextLine();
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
    
    private void showInputDataValidationErrorsForRoomRate(Set<ConstraintViolation<RoomRate>> constraintViolations) {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}
