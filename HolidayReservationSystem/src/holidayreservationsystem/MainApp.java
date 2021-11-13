/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import ws.client.InputDataValidationException_Exception;
import ws.client.InvalidCredentialsException_Exception;
import ws.client.InvalidPartnerException_Exception;
import ws.client.InvalidReservationException_Exception;
import ws.client.InvalidRoomException_Exception;
import ws.client.InvalidRoomTypeException_Exception;
import ws.client.InvalidUserException_Exception;
import ws.client.Partner;
import ws.client.PartnerWebService;
import ws.client.Reservation;
import ws.client.RoomType;
import ws.client.UnknownPersistenceException_Exception;

/**
 *
 * @author 81236
 */
public class MainApp {

    private PartnerWebService partnerWebService;
    
    private Partner currentPartner;

    MainApp() {
    }

    public MainApp(PartnerWebService partnerWebService) {       
        this.partnerWebService = partnerWebService;
    }

    public void runApp() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to the Holiday Reservation System Client! ***\n");
            System.out.println("1. Login");
            System.out.println("2. Search Hotel Room");
            System.out.println("3. Exit"); // KW: added option to exit function
            System.out.print(">");
            response = sc.nextInt();

            if (response == 1) {
                try {
                    doLogin();
                    mainMenu();
                } catch (InvalidPartnerException_Exception | InvalidCredentialsException_Exception e) {
                    System.out.println("An error has occured while logging in: " + e.getMessage() + "\n");
                }
            } else if (response == 2) {
                searchHotelRoom();
            } else if (response == 3) {
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }

    }

    public void mainMenu() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** HoRS Reservation Client Main Menu ***\n");
            System.out.println("You are logged in as " + currentPartner.getUsername() + "\n");
            System.out.println("1: Search Hotel Room");
            System.out.println("2: View Partner Reservation Details");
            System.out.println("3: View All Partner Reservations");
            System.out.println("4: Logout");
            System.out.print(">");

            response = sc.nextInt();
            if (response == 1) {
                searchHotelRoom();
            } else if (response == 2) {
                viewPartnerReservationDetails();
            } else if (response == 3) {
                viewAllPartnerReservations();
            } else if (response == 4) {
                break;
            } else {
                System.out.println("Invalid option.");
                System.out.println("Please try again.");
            }
        }
    }

    public void doLogin() throws InvalidPartnerException_Exception, InvalidCredentialsException_Exception {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();
        currentPartner = partnerWebService.partnerLogin(username, password);
        System.out.println();
        System.out.println(String.format("Successfully logged in as %s!\n", currentPartner.getUsername()));
    }

    public void searchHotelRoom() {
        Scanner sc = new Scanner(System.in);
        String checkInDateString, checkOutDateString;
        String input;
        java.time.LocalDate checkOutDate, checkInDate;
        Integer response = 0;

        System.out.print("Enter check-in date (YYYY-MM-DD): ");
        checkInDateString = sc.nextLine();
        checkInDate = java.time.LocalDate.parse(checkInDateString, DateTimeFormatter.ISO_DATE);

        System.out.print("Enter check-out date (YYYY-MM-DD): ");
        checkOutDateString = sc.nextLine();
        checkOutDate = java.time.LocalDate.parse(checkOutDateString, DateTimeFormatter.ISO_DATE);
        
        Long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        
        System.out.print("Enter number of rooms: ");
        Integer noOfRooms = sc.nextInt();

        List<RoomType> availableRoomTypes = partnerWebService.searchRooms(checkInDateString, checkOutDateString, noOfRooms);
        
        System.out.println("Please select your desired room type by entering the respective number. Type '0' to exit.\n");
        System.out.printf("%8s%25s%20s%20s\n", "No.", "Room Type", "Vacancies", "Sub Total");
        
        if (availableRoomTypes.isEmpty()) {
            System.out.println("\nNo vacant rooms for this period\n");
            return;
        }
       
        List<BigDecimal> subTotals = partnerWebService.calculateSubTotals();
        
        for (int i = 0; i < availableRoomTypes.size(); i++) {
            System.out.printf("%8s%25s%20s%20s\n", i + 1, availableRoomTypes.get(i).getName(), availableRoomTypes.get(i).getTotalRooms(), subTotals.get(i));
        }
        
        System.out.print(">");
        response = sc.nextInt();

        if (response < 1 || response > availableRoomTypes.size()) {
            return;
        }

        RoomType roomType = availableRoomTypes.get(response - 1);
        
        BigDecimal subTotal = subTotals.get(response - 1);
        
        sc.nextLine();

        System.out.println(String.format("For one %s from %s to %s, you will be charged $%s for %s nights", roomType.getName(), checkInDate, checkOutDate, subTotal, nights));
        BigDecimal totalAmount = subTotal.multiply(new BigDecimal(noOfRooms));
        System.out.print(String.format("Proceed to book %s rooms for a total of $%s? Type 'Y' to proceed: ", noOfRooms, totalAmount));
        input = sc.nextLine();

        if (input.toLowerCase().equals("y")) {
            if (currentPartner == null) {
                while (true) {
                    System.out.println("You have not logged in yet.\n");
                    System.out.println("1: Login");
                    System.out.println("2: Terminate Reservation");

                    response = sc.nextInt();
                    if (response == 1) {
                        try {
                            doLogin();
                            reserveHotelRoom(noOfRooms, roomType, subTotal, checkInDate, checkOutDate);
                            break;
                        } catch (InvalidPartnerException_Exception | InvalidCredentialsException_Exception e) {
                            System.out.println("Error: " + e.toString());
                        }
                    } else if (response == 2) {
                        break;
                    } else {
                        System.out.println("Invalid option.");
                        System.out.println("Please try again.");
                    }
                }
            } else {
               reserveHotelRoom(noOfRooms, roomType, subTotal, checkInDate, checkOutDate); 
            }
        }
    }

    public void reserveHotelRoom(Integer noOfRooms, RoomType roomType, BigDecimal subTotal, java.time.LocalDate checkInDate, java.time.LocalDate checkOutDate) {
      
        System.out.println();
        
        Reservation reservation = null;
        for (Integer i = 0; i < noOfRooms; i++) {
            try {
                partnerWebService.addReservationItem(subTotal, roomType.getName());
            } catch (InvalidRoomTypeException_Exception | InputDataValidationException_Exception e) {
                System.out.println("Error: " + e.toString());
                return;
            }
        }
        try {
            reservation = partnerWebService.reserveRooms(currentPartner.getUsername());
        } catch (InvalidRoomException_Exception | InvalidUserException_Exception | InvalidReservationException_Exception | UnknownPersistenceException_Exception | InputDataValidationException_Exception e) {
            System.out.println("Error: " + e.toString());
            return;
        }
        System.out.println(String.format("Reservation successful! Your reservation ID is %s\n", reservation.getReservationId())); // KW: added printing reservation ID
    }

    public void viewPartnerReservationDetails() {
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Enter the reservation ID to view details: ");
        
        Long response = sc.nextLong();
        
        Reservation reservation = null;
        try {
            reservation = partnerWebService.retrieveReservationById(response);
        } catch (InvalidReservationException_Exception e) {
            System.out.println("An error occured while retrieiving the reservation: " + e.getMessage());
        }
        
        List<Reservation> retrieveReservationsByUser = new ArrayList<>();
        List<String> checkInDates = new ArrayList<>();
        List<String> checkOutDates = new ArrayList<>();
        List<String> reservationDateTimes = new ArrayList<>();
        
        try {
            retrieveReservationsByUser = partnerWebService.retrieveReservationsByUser(currentPartner.getUsername());
            checkInDates = partnerWebService.retrieveCheckInDatesByUser(currentPartner.getUsername());
            checkOutDates = partnerWebService.retrieveCheckOutDatesByUser(currentPartner.getUsername());
            reservationDateTimes = partnerWebService.retrieveReservationDateTimesByUser(currentPartner.getUsername());
        } catch (InvalidReservationException_Exception e) {
            System.out.println("An error occured while retrieiving the reservation: " + e.getMessage());
        }
        
        Boolean isValid = false;
        int index = 0;
        for(int i = 0; i < retrieveReservationsByUser.size(); i++)  {
            if (reservation.getReservationId().longValue() == retrieveReservationsByUser.get(i).getReservationId().longValue()) {
                isValid = true;
                index = i;
                break;
            }
        }
        if(isValid) {
            System.out.printf("%8s%20s%35s%20s%20s%20s%30s\n", 
                        "ID",  
                        "Reservation Status",
                        "Reserved Room Type",
                        "Total Amount", 
                        "Check-In Date", 
                        "Check-Out Date",
                        "Reservation Date Time"
            );
            System.out.printf("%8s%20s%35s%20s%20s%20s%30s\n", 
                    retrieveReservationsByUser.get(index).getReservationId(),
                    retrieveReservationsByUser.get(index).getReservationStatus().toString(),
                    retrieveReservationsByUser.get(index).getReservationItems().get(0).getReservedRoomType().getName(),
                    retrieveReservationsByUser.get(index).getTotalAmount(),
                    checkInDates.get(index),
                    checkOutDates.get(index),
                    reservationDateTimes.get(index)
            );
        } else {
            System.out.println("Invalid ID");
        }
        
        sc.nextLine();
        
        System.out.print("Press any key to continue...> ");
        sc.nextLine();
        
    }

    public void viewAllPartnerReservations() {
        Scanner sc = new Scanner(System.in);
        
        List<Reservation> reservations = new ArrayList<>();
        List<String> checkInDates = new ArrayList<>();
        List<String> checkOutDates = new ArrayList<>();
        List<String> reservationDateTimes = new ArrayList<>();
        
        try {
            reservations = partnerWebService.retrieveReservationsByUser(currentPartner.getUsername());
            checkInDates = partnerWebService.retrieveCheckInDatesByUser(currentPartner.getUsername());
            checkOutDates = partnerWebService.retrieveCheckOutDatesByUser(currentPartner.getUsername());
            reservationDateTimes = partnerWebService.retrieveReservationDateTimesByUser(currentPartner.getUsername());
        } catch (InvalidReservationException_Exception e) {
            System.out.println("An error occured while retrieving the reservations: " + e.toString());
        }
        System.out.printf("%8s%20s%30s%20s%20s%20s%30s\n", 
                        "ID",  
                        "Reservation Status",
                        "Reserved Room Type",
                        "Total Amount", 
                        "Check-In Date", 
                        "Check-Out Date",
                        "Reservation Date Time"
        );
        for (int i = 0; i < reservations.size(); i++) {
         
            System.out.printf("%8s%20s%30s%20s%20s%20s%30s\n", 
                    reservations.get(i).getReservationId(),
                    reservations.get(i).getReservationStatus().toString(),
                    reservations.get(i).getReservationItems().get(0).getReservedRoomType().getName(),
                    reservations.get(i).getTotalAmount(),
                    checkInDates.get(i),
                    checkOutDates.get(i),
                    reservationDateTimes.get(i)
            );
        }
        
        System.out.print("Press any key to continue...> ");
        sc.nextLine();
    }

}
