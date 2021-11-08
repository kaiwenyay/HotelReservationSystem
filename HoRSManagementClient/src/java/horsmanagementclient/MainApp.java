/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import entity.Employee;
import java.util.Scanner;
import util.exception.InvalidCredentialsException;
import util.exception.InvalidEmployeeException;

/**
 *
 * @author kwpwn
 */
public class MainApp {
    
    private Employee currentEmployee;
    
    private EmployeeSessionBeanRemote employeeSessionBean;
    
    private SystemAdministrationModule systemAdministrationModule;
    
    private HotelOperationModule hotelOperationModule;
    
    private FrontOfficeModule frontOfficeModule;
    
    MainApp(EmployeeSessionBeanRemote employeeSessionBean) {
        this.employeeSessionBean = employeeSessionBean;
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
                    systemAdministrationModule = new SystemAdministrationModule();
                    hotelOperationModule = new HotelOperationModule();
                    frontOfficeModule = new FrontOfficeModule();
                    mainMenu();
                } catch (InvalidEmployeeException | InvalidCredentialsException e) {
                    System.out.println("Error: " + e.toString());
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
        System.out.println();
        System.out.println(String.format("Successfully logged in as %s!\n", currentEmployee.getUsername()));
        currentEmployee = employeeSessionBean.employeeLogin(username, password);
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
        }
    }
}
