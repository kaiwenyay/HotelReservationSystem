/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import ejb.session.stateless.PartnerSessionBeanRemote;
import entity.Employee;
import entity.Partner;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import util.enumeration.InvalidStaffRoleException;
import util.enumeration.StaffRole;
import util.exception.InputDataValidationException;
import util.exception.InvalidEmployeeException;
import util.exception.InvalidPartnerException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
public class SystemAdministrationModule {
    
    private final ValidatorFactory validatorFactory;
    private final Validator validator;
    
    private Employee currentEmployee;   
    
    private EmployeeSessionBeanRemote employeeSessionBean;
    
    private PartnerSessionBeanRemote partnerSessionBean;

    public SystemAdministrationModule() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }
    
    public SystemAdministrationModule(Employee currentEmployee, EmployeeSessionBeanRemote employeeSessionBean, PartnerSessionBeanRemote partnerSessionBean) {
        this();
        
        this.currentEmployee = currentEmployee;
        this.employeeSessionBean = employeeSessionBean;
        this.partnerSessionBean = partnerSessionBean;
    }

    public void menu() throws InvalidStaffRoleException {
        if (currentEmployee.getStaffRole() != StaffRole.ADMIN) {
            throw new InvalidStaffRoleException("You don't have MANAGER rights to access the system administration module.");
        }
        
        Scanner sc = new Scanner(System.in);
        Integer response = 0;
        
        while (true) {
            System.out.println("*** HoRS Management Client: System Administration ***\n");
            System.out.println("1: Create New Employee");
            System.out.println("2: View All Employees");
            System.out.println("-----------------------");
            System.out.println("3: Create New Partner");
            System.out.println("4: View All Partners");
            System.out.println("-----------------------");
            System.out.println("5: Back\n");
            System.out.print(">"); 
            response = sc.nextInt();
            
            if (response == 1) {
                doCreateNewEmployee();
            } else if (response == 2) {
                doViewAllEmployees();
            } else if (response == 3) {
                doCreateNewPartner();
            } else if (response == 4) {
                doViewAllPartners();
            } else if (response == 5) {
                break;
            } else {
                System.out.println("Invalid option.");
            }
        }
    }
    
    public void doCreateNewEmployee() {
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Enter Username> ");
        String username = sc.nextLine();
        System.out.print("Enter Password> ");
        String password = sc.nextLine();
        
        Integer response = 0;
        StaffRole staffRole = null;
        
        while (staffRole == null) {
            System.out.println("Please select employee's staff role.");
            System.out.println("1. System Administrator");
            System.out.println("2. Operations Manager");
            System.out.println("3. Sales Manager");
            System.out.println("4. Guest Relations Officer");
            System.out.print(">");
            response = sc.nextInt();

            if (response == 1) {
                staffRole = StaffRole.ADMIN;
            } else if (response == 2) {
                staffRole = StaffRole.OPERATIONS;
            } else if (response == 3) {
                staffRole = StaffRole.SALES;
            } else if (response == 4) {
                staffRole = StaffRole.GUEST_RELATIONS;
            } else {
                System.out.println("Invalid option.");
            }
        }
        
        Set<ConstraintViolation<Employee>> constraintViolations = validator.validate(new Employee(username, password, staffRole));
        
        if (constraintViolations.isEmpty()) {
            try {
                Employee employee = employeeSessionBean.createEmployee(username, password, staffRole);
                System.out.println(String.format("Successfully created employee %s!\n", employee.getUsername()));
            } catch (InvalidEmployeeException | UnknownPersistenceException | InputDataValidationException e) {
                System.out.println("Error: " + e.toString());
            }
        } else {
            showInputDataValidationErrorsForEmployee(constraintViolations);
        }
    }
    
    public void doViewAllEmployees() {
        Scanner scanner = new Scanner(System.in);
        
        List<Employee> employees = employeeSessionBean.retrieveAllEmployees();
        System.out.printf("%8s%20s%20s%20s\n", "User ID", "Username", "Password", "Staff Role");

        for(Employee e : employees) {
            System.out.printf("%8s%20s%20s%20s\n", e.getUserId().toString(), e.getUsername(), e.getPassword(), e.getStaffRole().toString());
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    public void doCreateNewPartner() {
        Scanner sc = new Scanner(System.in);
        
        System.out.print("Enter Username> ");
        String username = sc.nextLine();
        System.out.print("Enter Password> ");
        String password = sc.nextLine();
        System.out.print("Enter Partner Name> ");
        String partnerName = sc.nextLine();
        
        Set<ConstraintViolation<Partner>> constraintViolations = validator.validate(new Partner(username, password, partnerName));
        
        if (constraintViolations.isEmpty()) {
            try {
                Partner partner = partnerSessionBean.createPartner(username, password, partnerName);
                System.out.println(String.format("Successfully created partner %s! (Userame: %s)\n", partner.getPartnerName(), partner.getUsername()));
            } catch (InvalidPartnerException | UnknownPersistenceException | InputDataValidationException e) {
                System.out.println("Error: " + e.toString());
            }
        } else {
            showInputDataValidationErrorsForPartner(constraintViolations);
        }
    }
    
    public void doViewAllPartners() {
        Scanner scanner = new Scanner(System.in);
        
        List<Partner> partners = partnerSessionBean.retrieveAllPartners();
        System.out.printf("%8s%20s%20s%20s\n", "User ID", "Username", "Password", "Partner Name");

        for(Partner e : partners) {
            System.out.printf("%8s%20s%20s%20s\n", e.getUserId().toString(), e.getUsername(), e.getPassword(), e.getPartnerName());
        }

        System.out.print("Press any key to continue...> ");
        scanner.nextLine();
    }
    
    private void showInputDataValidationErrorsForEmployee(Set<ConstraintViolation<Employee>> constraintViolations) {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
    
    private void showInputDataValidationErrorsForPartner(Set<ConstraintViolation<Partner>> constraintViolations) {
        System.out.println("\nInput data validation error!:");
            
        for(ConstraintViolation constraintViolation:constraintViolations) {
            System.out.println("\t" + constraintViolation.getPropertyPath() + " - " + constraintViolation.getInvalidValue() + "; " + constraintViolation.getMessage());
        }

        System.out.println("\nPlease try again......\n");
    }
}
