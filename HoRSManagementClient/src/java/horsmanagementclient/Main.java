/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package horsmanagementclient;

import ejb.session.stateless.EmployeeSessionBeanRemote;
import javax.ejb.EJB;

/**
 *
 * @author kwpwn
 */
public class Main {

    @EJB
    private static EmployeeSessionBeanRemote employeeSessionBean;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        MainApp mainApp = new MainApp(employeeSessionBean);
        mainApp.runApp();
    }
    
}
