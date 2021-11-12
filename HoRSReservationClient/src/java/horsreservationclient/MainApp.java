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
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
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

        Set<ConstraintViolation<Guest>> constraintViolations = validator.validate(new Guest(email, password));

        if (constraintViolations.isEmpty()) {
            try {
                Guest guest = guestSessionBeanRemote.createGuest(email, password);
                System.out.println(String.format("Successfully created guest %s!\n", guest.getUsername()));
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

        List<RoomType> availableRoomTypes = reservationManagerSessionBeanRemote.searchRoom(LocalDate.now(), checkOutDate);

        System.out.println("Please select your desired room type.");
        for (int i = 0; i < availableRoomTypes.size(); i++) {
            RoomType roomType = availableRoomTypes.get(i);
            System.out.println(String.format("%s. %s : %s vacancies", i + 1, roomType.getName(), roomType.getCurrentAvailableRooms()));
        }
        System.out.print(">");
        response = sc.nextInt();

        if (response < 1 || response > availableRoomTypes.size()) {
            System.out.println("Invalid option.");
            return;
        }

        RoomType selected = availableRoomTypes.get(response - 1);
        System.out.print("Enter the number of rooms you would like to reserve: ");
        response = sc.nextInt();
        if (selected.getCurrentAvailableRooms() < response) {
            System.out.println(String.format("%s has insufficient vacancies.", selected.getName()));
            return;
        }
        sc.nextLine();
        Long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);

        System.out.print(String.format("You have chosen to reserve %s rooms of type %s. Type 'Y' to continue: ", response, selected.getName()));
        input = sc.nextLine();

        if (input.toLowerCase().equals("y")) {
            while (currentGuest == null) {
                System.out.println("You have not login in\n");
                System.out.println("1: Login");
                System.out.println("2: Register As Guest");
                System.out.println("3: Terminate Reserve");

                Integer responce = sc.nextInt();
                if (response == 1) {
                    try {
                        doLogin();
                        reserveHotelRoom(response, nights, selected, checkInDate, checkOutDate);
                    } catch (InvalidGuestException | InvalidCredentialsException e) {
                        System.out.println("Error: " + e.toString());
                    }
                } else if (response == 2) {
                    registerAsGuest();
                    reserveHotelRoom(response, nights, selected, checkInDate, checkOutDate);
                } else if (response == 3) {
                    break;
                } else {
                    System.out.println("Invalid option.");
                    System.out.println("Please try again.");
                }
            }

        }
    }

    public void reserveHotelRoom(Integer quantity, Long nights, RoomType roomType, LocalDate checkInDate, LocalDate checkOutDate) {
        Scanner sc = new Scanner(System.in);

        List<RoomRate> roomRates = roomType.getRoomRates();
        RoomRate roomRate = null;
        for (RoomRate r : roomRates) {
            if (r.getRateType() == RateType.PUBLISHED) {
                roomRate = r;
                break;
            }
        }
        System.out.println(String.format("For one room, you will be charged %s per night for %s nights", roomRate.getRatePerNight().toString(), nights));
        BigDecimal subTotal = roomRate.getRatePerNight().multiply(new BigDecimal(nights));
        BigDecimal totalAmount = subTotal.multiply(new BigDecimal(quantity));
        System.out.print(String.format("Proceed to book %s rooms for a total of %s? Type 'Y' to proceed: ", quantity, totalAmount));

        String response = sc.nextLine();
        if (response.toLowerCase().equals("y")) {
            for (Integer i = 0; i < quantity; i++) {
                try {
                    reservationManagerSessionBeanRemote.addReservationItem(subTotal, roomType.getName());
                } catch (InvalidRoomTypeException | InputDataValidationException e) {
                    System.out.println("Error: " + e.toString());
                    return;
                }
            }
            try {
                reservationManagerSessionBeanRemote.reserveRooms(currentGuest.getUsername(), LocalDate.now(), checkOutDate);
            } catch (InvalidRoomException | InvalidUserException | InvalidReservationException | UnknownPersistenceException | InputDataValidationException e) {
                System.out.println("Error: " + e.toString());
                return;
            }
        }
        System.out.println("Reservation successful!\n");
    }

    public void viewMyReservationDetails() {
        Scanner sc = new Scanner(System.in);
        
        System.out.println("Enter the reservation ID to view details");
        
        Long response = sc.nextLong();
        
        
        // need to validate reservation with this ID belongs to the the guest
        // need this method in remote
        Reservation reservation = reservationSessionBeanRemote.retrieveReservationById(response);
        List<Reservation> retrieveReservationsByUser = reservationSessionBeanRemote.retrieveReservationsByUser(currentGuest.getUsername());
        Boolean isValid = false;
        for(Reservation r:retrieveReservationsByUser) 
        {
            if (reservation.equals(r))
            {
                isValid = true;
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
