/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateful.ReservationManagerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import entity.Employee;
import entity.Reservation;
import entity.ReservationItem;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import util.enumeration.StaffRole;
import util.exception.InputDataValidationException;
import util.exception.InvalidReportException;
import util.exception.InvalidReservationException;
import util.exception.InvalidRoomException;
import util.exception.InvalidRoomTypeException;
import util.exception.InvalidUserException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
public class FrontOfficeModule {
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private Employee currentEmployee; 
    
    private ReservationSessionBeanRemote reservationSessionBean;
    
    private ReservationManagerSessionBeanRemote reservationManagerSessionBean;
    
    public FrontOfficeModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public FrontOfficeModule(Employee currentEmployee, ReservationSessionBeanRemote reservationSessionBean, ReservationManagerSessionBeanRemote reservationManagerSessionBean) {
        this();
        
        this.currentEmployee = currentEmployee;
        this.reservationSessionBean = reservationSessionBean;
        this.reservationManagerSessionBean = reservationManagerSessionBean;
    }

    
    public FrontOfficeModule(Employee currentEmployee) {
        this();
        
        this.currentEmployee = currentEmployee;
    }
    
    public void menu() throws InvalidStaffRoleException {
        
        if (currentEmployee.getStaffRole() != StaffRole.ADMIN && currentEmployee.getStaffRole() != StaffRole.GUEST_RELATIONS) { 
            throw new InvalidStaffRoleException("You don't have ADMIN or GUEST_RELATIONS rights to access the system administration module.");
        }
        
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while (true) {
            System.out.println("*** HoRS Management Client: Front Office ***\n");
            System.out.println("1: Walk-In Search Room");
            System.out.println("-----------------------");
            System.out.println("2: Check In Guest");
            System.out.println("3: Check Out Guest");
            System.out.println("-----------------------");
            System.out.println("4: Manual Allocation");
            System.out.println("-----------------------");
            System.out.println("5: Back\n");
            System.out.print(">"); 
            response = sc.nextInt();
            
            if (response == 1) {
                doWalkInSearchRoom();
            } else if (response == 2) {
                doCheckInGuest();
            } else if (response == 3) {
                doCheckOutGuest();
            } else if (response == 4) {
                doManualAllocate();
            } else if (response == 5) {
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }

    private void doWalkInSearchRoom() {
        
        Scanner sc = new Scanner(System.in);
        String input;
        LocalDate checkInDate;
        LocalDate checkOutDate;
        Integer response = 0;
        
        System.out.print("Enter check-in date (YYYY-MM-DD): ");
        input = sc.nextLine();
        checkInDate = LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
        
        System.out.print("Enter check-out date (YYYY-MM-DD): ");
        input = sc.nextLine();
        checkOutDate = LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
        
        System.out.print("Enter number of rooms: ");
        Integer noOfRooms = sc.nextInt();
        
        List<RoomType> availableRoomTypes = reservationManagerSessionBean.searchRooms(checkInDate, checkOutDate, noOfRooms);
        
        System.out.println("Please select your desired room type by entering the respective number.\n");
        System.out.printf("%8s%20s%20s%20s\n", "No.", "Room Type", "Vacancies", "Rate Per Night");
        
        if (availableRoomTypes.isEmpty()) {
            System.out.println("\nNo vacant rooms for this period\n");
            return;
        }
        
        RoomType roomType;
        RoomRate roomRate = null;
        for (int i = 0; i < availableRoomTypes.size(); i++) {
            
            roomType = availableRoomTypes.get(i);
            List<RoomRate> roomRates = roomType.getRoomRates();
            
            for (RoomRate r: roomRates) {
                if (r.getRateType() == RateType.PUBLISHED && ! r.isDisabled()) {
                    roomRate = r;
                    break;
                }
            }
            
            System.out.printf("%8s%20s%20s%20s\n", i + 1, roomType.getName(), roomType.getTotalRooms(), roomRate.getRatePerNight());
            
        }
        System.out.print(">");
        
        response = sc.nextInt();
        
        if (response < 1 || response > availableRoomTypes.size()) {
            System.out.println("Invalid option.");
            return;
        }
        
        roomType = availableRoomTypes.get(response - 1);
        
        Long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        
        sc.nextLine(); 
        
        System.out.println(String.format("For one %s from %s to %s, you will be charged a %s rate of $%s per night for %s nights", 
                roomType.getName(),
                checkInDate,
                checkOutDate,
                roomRate.getRateType().toString(), 
                roomRate.getRatePerNight().toString(), 
                nights)
        );
        
        BigDecimal subTotal = roomRate.getRatePerNight().multiply(new BigDecimal(nights));
        BigDecimal totalAmount = subTotal.multiply(new BigDecimal(noOfRooms));
        System.out.print(String.format("Proceed to book %s rooms for a total of $%s? Type 'Y' to proceed: ", noOfRooms, totalAmount));
        
        input = sc.nextLine();
        if (input.toLowerCase().equals("y")) {
            doWalkInReserveRoom(noOfRooms, roomType,subTotal, checkInDate, checkOutDate);
        } 
    }
    
    private void doWalkInReserveRoom(Integer noOfRooms, RoomType roomType, BigDecimal subTotal, LocalDate checkInDate, LocalDate checkOutDate) {
        
        System.out.println();
        
        for (int i = 0; i < noOfRooms; i++) {
            
            ReservationItem reservationItem = new ReservationItem(subTotal);
            Set<ConstraintViolation<ReservationItem>> constraintViolations = validator.validate(reservationItem);
                
            if (constraintViolations.isEmpty()) {
                try {
                    reservationManagerSessionBean.addReservationItem(subTotal, roomType.getName());
                } catch (InvalidRoomTypeException | InputDataValidationException e) {
                    System.out.println("An error has occured while reserving the rooms: " + e.getMessage() + "\n");
                    return;
                }
            } else {
                showInputDataValidationErrorsForReservationItem(constraintViolations);
            }
        }
        
        Reservation reservation;
        
        try {
            reservation = reservationManagerSessionBean.reserveRooms(currentEmployee.getUsername());
            System.out.println(String.format("Reservation successful! Your reservation ID is %s.\n", reservation.getReservationId()));
        } catch (InvalidRoomException | InvalidUserException | InvalidReservationException e) {
            System.out.println("An error has occured while reserving the rooms: " + e.getMessage() + "\n");
        } catch (UnknownPersistenceException e) {
            System.out.println("An unknown error has occured while reserving the rooms: " + e.getMessage() + "\n");
        } catch (InputDataValidationException e) {
            System.out.println(e.getMessage() + "\n");
        }
    }
    
//    private void doSearchRoom() {
//        Scanner sc = new Scanner(System.in);
//        String input;
//        LocalDate checkOutDate;
//        LocalDate checkInDate;
//        Integer response = 0;
//        
//        System.out.print("Enter check-in date (YYYY-MM-DD): ");
//        input = sc.nextLine();
//        checkInDate = LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
//       
//        System.out.print("Enter check-out date (YYYY-MM-DD): ");
//        input = sc.nextLine();
//        checkOutDate = LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
//        
//        List<RoomType> availableRoomTypes = reservationManagerSessionBean.searchRooms(checkInDate, checkOutDate);
//        
//        System.out.println("Please select your desired room type.");
//        for (int i = 0; i < availableRoomTypes.size(); i++) {
//            RoomType roomType = availableRoomTypes.get(i);
//            System.out.println(String.format("%s. %s : %s vacancies", i + 1, roomType.getName(), roomType.getTotalRooms()));
//        }
//        System.out.print(">");
//        response = sc.nextInt();
//        
//        if (response < 1 || response > availableRoomTypes.size()) {
//            System.out.println("Invalid option.");
//            return;
//        }
//        
//        RoomType selected = availableRoomTypes.get(response - 1);
//        System.out.print("Enter the number of rooms you would like to reserve: ");
//        response = sc.nextInt();
//        if (selected.getTotalRooms() < response) {
//            System.out.println(String.format("%s has insufficient vacancies.", selected.getName()));
//            return;
//        }
//        sc.nextLine();
//        
//        System.out.print(String.format("You have chosen to reserve %s rooms of type %s. Type 'Y' to continue: ", response, selected.getName()));
//        input = sc.nextLine();
//        if (input.toLowerCase().equals("y")) {
//            Long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
//            doReserveRoom(response, nights, selected, checkInDate, checkOutDate);
//        } 
//    }
    
//    private void doReserveRoom(Integer quantity, Long nights, RoomType roomType, LocalDate checkInDate, LocalDate checkOutDate) {
//        Scanner sc = new Scanner(System.in);
//        
//        List<RoomRate> roomRates = roomType.getRoomRates();
//        RoomRate roomRate = null;
//        for (RoomRate r: roomRates) {
//            if (r.getRateType() == RateType.PROMOTION && ! r.isDisabled()) {
//                LocalDate validFrom = r.getValidFrom().minusDays(1l);
//                LocalDate validTo = r.getValidTo().plusDays(1l);
//                LocalDate now = LocalDate.now();
//                if (validFrom.isBefore(now) && validTo.isAfter(now)) {
//                    roomRate = r;
//                }
//            } else if (r.getRateType() == RateType.PEAK && ! r.isDisabled()) {
//                if (roomRate == null) {
//                    LocalDate validFrom = r.getValidFrom().minusDays(1l);
//                    LocalDate validTo = r.getValidTo().plusDays(1l);
//                    LocalDate now = LocalDate.now();
//                    if (validFrom.isBefore(now) && validTo.isAfter(now)) {
//                        roomRate = r;
//                    }
//                }
//            } else if (r.getRateType() == RateType.NORMAL && ! r.isDisabled()) {
//                if (roomRate == null) {
//                    roomRate = r;
//                }
//            }
//        }
//        System.out.println(String.format("For one room, you will be charged a %s rate of %s per night for %s nights", 
//                roomRate.getRateType().toString(), 
//                roomRate.getRatePerNight().toString(), 
//                nights)
//        );
//        BigDecimal subTotal = roomRate.getRatePerNight().multiply(new BigDecimal(nights));
//        BigDecimal totalAmount = subTotal.multiply(new BigDecimal(quantity));
//        System.out.print(String.format("Proceed to book %s rooms for a total of %s? Type 'Y' to proceed: ", quantity, totalAmount));
//        
//        String response = sc.nextLine();
//        if (response.toLowerCase().equals("y")) {
//            for (Integer i = 0; i < quantity; i++) {
//                try {
//                    reservationManagerSessionBean.addReservationItem(subTotal, roomType.getName());
//                } catch (InvalidRoomTypeException | InputDataValidationException e) {
//                    System.out.println("Error: " + e.toString());
//                    return;
//                }
//            }
//            Reservation reservation;
//            try {
//                reservation = reservationManagerSessionBean.reserveRooms(currentEmployee.getUsername(), checkInDate, checkOutDate);
//            } catch (InvalidRoomException | InvalidUserException | InvalidReservationException | UnknownPersistenceException | InputDataValidationException e) {
//                System.out.println("Error: " + e.toString());
//                return;
//            }
//            
//        }
//        System.out.println("Reservation successful!\n");
//    }
    
    private void doManualAllocate() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Allocation Date: ");
        LocalDate checkinDate = LocalDate.parse(sc.nextLine(), DateTimeFormatter.ISO_DATE);
        
        System.out.println();
        
        try {
            reservationSessionBean.manualAllocateRooms(checkinDate);
            System.out.println("Successfully allocated!\n");
        } catch (InvalidRoomException | InvalidReportException | InputDataValidationException e) {
            System.out.println("An error has occured during manual allocation: " + e.getMessage() + "\n");
        } catch (UnknownPersistenceException e) {
            System.out.println("An unknown error has occured during manual allocation: " + e.getMessage() + "\n");
        }
    }
    private void doCheckInGuest() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Reservation ID: ");
        Long reservationId = sc.nextLong();
        
        Reservation reservation;
        try {
            reservation = reservationSessionBean.retrieveReservationById(reservationId, false, true, true, true);
        } catch (InvalidReservationException e) {
            System.out.println("An error occured while retrieving the reservation: " + e.getMessage());
            return;
        }
        
        List<ReservationItem> items = reservation.getReservationItems();
        for (ReservationItem i : items) {
            
            if (i.getAllocationExceptionType() == AllocationExceptionType.NO_EXCEPTION) {
                
                System.out.println(String.format("Item %s: Booked 1 %s, successfully allocated room %s.",
                        i.getReservationItemId(), 
                        i.getReservedRoomType().getName(), 
                        i.getAllocatedRoom().getRoomNumber()
                ));
                
            } else if (i.getAllocationExceptionType() == AllocationExceptionType.TYPE_ONE) {
                
                System.out.println(String.format("Item %s: Originally booked 1 %s, due to a lack of vacant %ss, "
                        + "we have upgraded your room type at no additional cost and allocated you room %s.",
                        i.getReservationItemId(),
                        i.getReservedRoomType().getName(), 
                        i.getReservedRoomType().getName(),
                        i.getAllocatedRoom().getRoomNumber()
                ));
                
            } else {
                System.out.println(String.format("Item %s: Unfortunately, we are overbooked and we could not allocated you any rooms.", i.getReservationItemId()));
            }
        }
        System.out.println();
        System.out.println("Enjoy your stay!\n");
    }

    private void doCheckOutGuest() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Reservation ID: ");
        Long reservationId = sc.nextLong();
        
        Reservation reservation;
        try {
            reservation = reservationSessionBean.retrieveReservationById(reservationId, false, true, true, true);
        } catch (InvalidReservationException e) {
            System.out.println("An error occured while retrieving the reservation: " + e.getMessage());
            return;
        }
        
        List<ReservationItem> items = reservation.getReservationItems();
        for (ReservationItem i : items) {
            System.out.println(String.format("Successfully checked out of room %s", i.getAllocatedRoom().getRoomNumber()));
        }
        System.out.println();
        System.out.println("Thank you for staying with us. Have a nice day!\n");
    }
    
    private void showInputDataValidationErrorsForReservationItem(Set<ConstraintViolation<ReservationItem>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}
