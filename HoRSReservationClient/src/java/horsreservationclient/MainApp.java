/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsreservationclient;

import ejb.session.stateful.ReservationManagerSessionBeanRemote;
import ejb.session.stateless.AllocationExceptionReportSessionBeanRemote;
import ejb.session.stateless.GuestSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Guest;
import entity.Reservation;
import entity.RoomRate;
import entity.RoomType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.RateType;
import util.exception.InputDataValidationException;
import util.exception.InvalidCredentialsException;
import util.exception.InvalidGuestException;
import util.exception.InvalidReservationException;
import util.exception.InvalidRoomException;
import util.exception.InvalidRoomTypeException;
import util.exception.InvalidUserException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author 81236
 */
public class MainApp {

    private final Validator validator;
    private final ValidatorFactory validatorFactory;

    private ReservationManagerSessionBeanRemote reservationManagerSessionBeanRemote;

    private GuestSessionBeanRemote guestSessionBeanRemote;

    private ReservationSessionBeanRemote reservationSessionBeanRemote;

    private RoomSessionBeanRemote roomSessionBeanRemote;

    private RoomRateSessionBeanRemote roomRateSessionBeanRemote;

    private RoomTypeSessionBeanRemote roomTypeSessionBeanRemote;

    private AllocationExceptionReportSessionBeanRemote allocationExceptionReportSessionBeanRemote;

    private Guest currentGuest;

    MainApp() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    public MainApp(ReservationManagerSessionBeanRemote reservationManagerSessionBeanRemote, GuestSessionBeanRemote guestSessionBeanRemote, ReservationSessionBeanRemote reservationSessionBeanRemote, RoomSessionBeanRemote roomSessionBeanRemote, RoomRateSessionBeanRemote roomRateSessionBeanRemote, RoomTypeSessionBeanRemote roomTypeSessionBeanRemote, AllocationExceptionReportSessionBeanRemote allocationExceptionReportSessionBeanRemote) {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
        this.reservationManagerSessionBeanRemote = reservationManagerSessionBeanRemote;
        this.guestSessionBeanRemote = guestSessionBeanRemote;
        this.reservationSessionBeanRemote = reservationSessionBeanRemote;
        this.roomSessionBeanRemote = roomSessionBeanRemote;
        this.roomRateSessionBeanRemote = roomRateSessionBeanRemote;
        this.roomTypeSessionBeanRemote = roomTypeSessionBeanRemote;
        this.allocationExceptionReportSessionBeanRemote = allocationExceptionReportSessionBeanRemote;
    }

    public void runApp() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;

        while (true) {
            System.out.println("*** Welcome to the HoRS Reservation Client! ***\n");
            System.out.println("1. Login");
            System.out.println("2. Register As Guest");
            System.out.println("3. Search Hotel Room");
            System.out.println("4. Exit"); // KW: added option to exit function
            System.out.print(">");
            response = sc.nextInt();

            if (response == 1) {
                try {
                    doLogin();
                    mainMenu();
                } catch (InvalidGuestException | InvalidCredentialsException e) {
                    System.out.println("Error: " + e.toString());
                }
            } else if (response == 2) {
                registerAsGuest();
            } else if (response == 3) {
                searchHotelRoom();
            } else if (response == 4) {
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
            System.out.println("You are logged in as " + currentGuest.getUsername() + "\n");
            System.out.println("1: Search Hotel Room");
            System.out.println("2: View My Reservation Details");
            System.out.println("3: View All My Reservations");
            System.out.println("4: Logout");
            System.out.print(">");

            response = sc.nextInt();
            if (response == 1) {
                searchHotelRoom();
            } else if (response == 2) {
                viewMyReservationDetails();
            } else if (response == 3) {
                viewAllMyReservations();
            } else if (response == 4) {
                break;
            } else {
                System.out.println("Invalid option.");
                System.out.println("Please try again.");
            }
        }
    }

    public void doLogin() throws InvalidGuestException, InvalidCredentialsException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter Username: ");
        String username = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();
        currentGuest = guestSessionBeanRemote.guestLogin(username, password);
        System.out.println();
        System.out.println(String.format("Successfully logged in as %s!\n", currentGuest.getUsername()));
    }

    public void registerAsGuest() {

        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Email: ");
        String email = sc.nextLine();
        System.out.print("Enter Password: ");
        String password = sc.nextLine();
        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        Set<ConstraintViolation<Guest>> constraintViolations = validator.validate(new Guest(email, password, name));

        if (constraintViolations.isEmpty()) {
            try {
                Guest guest = guestSessionBeanRemote.createGuest(email, password, name);
                System.out.println(String.format("Successfully created guest %s (Username: %s)!\n", guest.getName(), guest.getUsername()));
            } catch (InvalidGuestException | UnknownPersistenceException | InputDataValidationException e) {
                System.out.println("Error: " + e.toString());
            }
        } else {
            showInputDataValidationErrorsForGuest(constraintViolations);
        }

    }

    public void searchHotelRoom() {
        Scanner sc = new Scanner(System.in);
        String input;
        LocalDate checkOutDate, checkInDate;
        Integer response = 0;

        System.out.print("Enter check-in date (YYYY-MM-DD): ");
        input = sc.nextLine();
        checkInDate = LocalDate.parse(input, DateTimeFormatter.ISO_DATE);

        System.out.print("Enter check-out date (YYYY-MM-DD): ");
        input = sc.nextLine();
        checkOutDate = LocalDate.parse(input, DateTimeFormatter.ISO_DATE);
        
        System.out.print("Enter number of rooms: ");
        Integer noOfRooms = sc.nextInt();

        List<RoomType> availableRoomTypes = reservationManagerSessionBeanRemote.searchRooms(checkInDate, checkOutDate, noOfRooms);
        
        System.out.println("Please select your desired room type by entering the respective number.\n");
        System.out.printf("%8s%25s%20s%20s%20s\n", "No.", "Room Type", "Vacancies", "Rate Type", "Rate Per Night");
        RoomType roomType;
        List<RoomRate> roomRateForEach = new ArrayList<>();
        for (int i = 0; i < availableRoomTypes.size(); i++) {
            roomType = availableRoomTypes.get(i);
            List<RoomRate> roomRates = roomType.getRoomRates();
            RoomRate roomRate = null;
            for (RoomRate r: roomRates) {
                if (r.getRateType() == RateType.PROMOTION && ! r.isDisabled()) {
                    LocalDate validFrom = r.getValidFrom().minusDays(1l);
                    LocalDate validTo = r.getValidTo().plusDays(1l);
                    System.out.println(validFrom.isBefore(checkInDate) && validTo.isAfter(checkInDate));
                    if (validFrom.isBefore(checkInDate) && validTo.isAfter(checkInDate)) {
                        roomRate = r;
                    }
                } else if (r.getRateType() == RateType.PEAK && ! r.isDisabled()) {
                    if (roomRate == null || roomRate.getRateType() != RateType.PROMOTION) {
                        LocalDate validFrom = r.getValidFrom().minusDays(1l);
                        LocalDate validTo = r.getValidTo().plusDays(1l);
                        LocalDate now = LocalDate.now();
                        System.out.println(validFrom.isBefore(now) && validTo.isAfter(now));
                        if (validFrom.isBefore(now) && validTo.isAfter(now)) {
                            roomRate = r;
                        }
                    }
                } else if (r.getRateType() == RateType.NORMAL && ! r.isDisabled()) {
                    if (roomRate == null || (roomRate.getRateType() != RateType.PROMOTION && roomRate.getRateType() != RateType.PEAK)) {
                        roomRate = r;
                    }
                }
            }
            roomRateForEach.add(roomRate);
            System.out.printf("%8s%25s%20s%20s%20s\n", i + 1, roomType.getName(), roomType.getTotalRooms(), roomRate.getRateType(), roomRate.getRatePerNight());
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

        System.out.println(String.format("For one room, you will be charged a %s rate of %s per night for %s nights", 
                roomRateForEach.get(response - 1).getRateType().toString(), 
                roomRateForEach.get(response - 1).getRatePerNight().toString(), 
                nights)
        );
        BigDecimal subTotal = roomRateForEach.get(response - 1).getRatePerNight().multiply(new BigDecimal(nights));
        BigDecimal totalAmount = subTotal.multiply(new BigDecimal(noOfRooms));
        System.out.print(String.format("Proceed to book %s rooms for a total of %s? Type 'Y' to proceed: ", noOfRooms, totalAmount));
        input = sc.nextLine();

        if (input.toLowerCase().equals("y")) {
            if (currentGuest == null) {
                while (true) {
                    System.out.println("You have not logged in yet.\n");
                    System.out.println("1: Login");
                    System.out.println("2: Register As Guest");
                    System.out.println("3: Terminate Reservation");

                    response = sc.nextInt();
                    if (response == 1) {
                        try {
                            doLogin();
                            reserveHotelRoom(noOfRooms, roomType, subTotal, checkInDate, checkOutDate);
                            break;
                        } catch (InvalidGuestException | InvalidCredentialsException e) {
                            System.out.println("Error: " + e.toString());
                        }
                    } else if (response == 2) {
                        registerAsGuest();
                        try {
                            doLogin();
                            reserveHotelRoom(noOfRooms, roomType, subTotal, checkInDate, checkOutDate);
                            break;
                        } catch (InvalidGuestException | InvalidCredentialsException e) {
                            System.out.println("Error: " + e.toString());
                        }
                    } else if (response == 3) {
                        break;
                    } else {
                        System.out.println("Invalid option.");
                        System.out.println("Please try again.");
                    }
                }
            }

        }
    }

    public void reserveHotelRoom(Integer noOfRooms, RoomType roomType, BigDecimal subTotal, LocalDate checkInDate, LocalDate checkOutDate) {
      
        Reservation reservation = null;
        for (Integer i = 0; i < noOfRooms; i++) {
            try {
                reservationManagerSessionBeanRemote.addReservationItem(subTotal, roomType.getName());
            } catch (InvalidRoomTypeException | InputDataValidationException e) {
                System.out.println("Error: " + e.toString());
                return;
            }
        }
        try {
            reservation = reservationManagerSessionBeanRemote.reserveRooms(currentGuest.getUsername(), LocalDate.now(), checkOutDate);
        } catch (InvalidRoomException | InvalidUserException | InvalidReservationException | UnknownPersistenceException | InputDataValidationException e) {
            System.out.println("Error: " + e.toString());
            return;
        }
        System.out.println(String.format("Reservation successful! Your reservation ID is %s\n", reservation.getReservationId())); // KW: added printing reservation ID
    }

    public void viewMyReservationDetails() {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Enter the reservation ID to view details: ");
        
        Long response = sc.nextLong();
        
        
        // need to validate reservation with this ID belongs to the the guest
        // need this method in remote
        Reservation reservation = null;
        try {
            reservation = reservationSessionBeanRemote.retrieveReservationById(response);
        } catch (InvalidReservationException e) {
            System.out.println("An error occured while retrieiving the reservation: " + e.getMessage());
        }
        List<Reservation> retrieveReservationsByUser = reservationSessionBeanRemote.retrieveReservationsByUser(currentGuest.getUsername());
        Boolean isValid = false;
        for(Reservation r:retrieveReservationsByUser)  {
            if (reservation.equals(r)) {
                isValid = true;
                break;
            }
        }
        if(isValid) {
            System.out.println(reservation);
        } else {
            System.out.println("Invalid ID");
        }
    }

    public void viewAllMyReservations() {
        List<Reservation> retrieveReservationsByUser = reservationSessionBeanRemote.retrieveReservationsByUser(currentGuest.getUsername());
        System.out.println(retrieveReservationsByUser);
        
        Scanner sc = new Scanner(System.in);
        
        while (true) {
            System.out.println("Do you want to view a reservation details?\n");
            System.out.println("1: Yes");
            System.out.println("2: No");
            Integer response = sc.nextInt();
            if (response == 1)
            {
                viewMyReservationDetails();
            }

        }
    }

    private void showInputDataValidationErrorsForGuest(Set<ConstraintViolation<Guest>> constraintViolations) {
        System.out.println("\nInput data validation error!:");

        for (ConstraintViolation constraintViolation : constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }

}
