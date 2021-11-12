/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateful.ReservationManagerSessionBeanRemote;
import ejb.session.stateless.AllocationExceptionReportSessionBeanRemote;
import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import ejb.session.stateless.ReservationSessionBeanRemote;
import ejb.session.stateless.RoomRateSessionBeanRemote;
import ejb.session.stateless.RoomSessionBeanRemote;
import ejb.session.stateless.RoomTypeSessionBeanRemote;
import entity.Employee;
import java.util.Scanner;
import util.enumeration.InvalidStaffRoleException;
import util.exception.InvalidCredentialsException;
import util.exception.InvalidEmployeeException;

/**
 *
 * @author kwpwn
 */
public class MainApp {
 
    private EmployeeSessionBeanRemote employeeSessionBean;
    
    private PartnerSessionBeanRemote partnerSessionBean;
    
    private RoomSessionBeanRemote roomSessionBean;
    
    private RoomTypeSessionBeanRemote roomTypeSessionBean;
    
    private RoomRateSessionBeanRemote roomRateSessionBean;
    
    private ReservationSessionBeanRemote reservationSessionBean;

    private ReservationManagerSessionBeanRemote reservationManagerSessionBean;
    
    private AllocationExceptionReportSessionBeanRemote allocationExceptionReportSessionBean;
    
    private SystemAdministrationModule systemAdministrationModule;
    
    private HotelOperationModule hotelOperationModule;
    
    private FrontOfficeModule frontOfficeModule;
        
    private Employee currentEmployee;
    
    MainApp() {
    }

    public MainApp(EmployeeSessionBeanRemote employeeSessionBean, PartnerSessionBeanRemote partnerSessionBean, RoomSessionBeanRemote roomSessionBean, RoomTypeSessionBeanRemote roomTypeSessionBean, RoomRateSessionBeanRemote roomRateSessionBean, ReservationSessionBeanRemote reservationSessionBean, ReservationManagerSessionBeanRemote reservationManagerSessionBean, AllocationExceptionReportSessionBeanRemote allocationExceptionReportSessionBean) {
        this.employeeSessionBean = employeeSessionBean;
        this.partnerSessionBean = partnerSessionBean;
        this.roomSessionBean = roomSessionBean;
        this.roomTypeSessionBean = roomTypeSessionBean;
        this.roomRateSessionBean = roomRateSessionBean;
        this.reservationSessionBean = reservationSessionBean;
        this.reservationManagerSessionBean = reservationManagerSessionBean;
        this.allocationExceptionReportSessionBean = allocationExceptionReportSessionBean;
    }

    

    public void runApp() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while (true) {
            System.out.println("*** Welcome to the HoRS Management Client! ***\n");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.print(">");
            response = sc.nextInt();
                
            if (response == 1) {
                try {
                    doLogin();
                    systemAdministrationModule = new SystemAdministrationModule(currentEmployee, employeeSessionBean, partnerSessionBean);
                    hotelOperationModule = new HotelOperationModule(currentEmployee, roomSessionBean, roomTypeSessionBean, roomRateSessionBean, allocationExceptionReportSessionBean);
                    frontOfficeModule = new FrontOfficeModule(currentEmployee, reservationSessionBean, reservationManagerSessionBean);
                    mainMenu();
                } catch (InvalidEmployeeException | InvalidCredentialsException e) {
                    System.out.println("An error occured while logging in: " + e.getMessage() + "\n");
                } 
            } else if (response == 2) {
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }
    
    public void doLogin() throws InvalidEmployeeException, InvalidCredentialsException {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter username: ");
        String username = sc.nextLine();
        System.out.print("Enter password: ");
        String password = sc.nextLine();
        currentEmployee = employeeSessionBean.employeeLogin(username, password);
        System.out.println();
        System.out.println(String.format("Successfully logged in as %s!\n", currentEmployee.getUsername()));
    }
    
    public void mainMenu() {
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while (true) {
            System.out.println("*** HoRS Management Client Main Menu ***\n");
            System.out.println("You are logged in as " + currentEmployee.getUsername() + " with " + currentEmployee.getStaffRole().toString() + " rights\n");
            System.out.println("1: System Administration");
            System.out.println("2: Hotel Operation");
            System.out.println("3: Front Office");
            System.out.println("4: Logout");
            System.out.print(">");
            
            response = sc.nextInt();
            try {
                if (response == 1) {
                    systemAdministrationModule.menu();
                } else if (response == 2) {
                    hotelOperationModule.menu();
                } else if (response == 3) {
                    frontOfficeModule.menu();
                } else if (response == 4) {
                    break;
                } else {
                    System.out.println("Invalid option.");
                }
            } catch (InvalidStaffRoleException e) {
                System.out.println("Error: " + e.toString());
                System.out.println("Please try again.");
            }
        }
    }
}
