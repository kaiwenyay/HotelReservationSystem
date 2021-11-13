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
import javax.ejb.EJB;

/**
 *
 * @author kwpwn
 */
public class Main {

    @EJB(name = "ReservationSessionBeanRemote")
    private static ReservationSessionBeanRemote reservationSessionBeanRemote;

    @EJB(name = "ReservationManagerSessionBeanRemote")
    private static ReservationManagerSessionBeanRemote reservationManagerSessionBeanRemote;

    @EJB(name = "GuestSessionBeanRemote")
    private static GuestSessionBeanRemote guestSessionBeanRemote;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        MainApp mainApp = new MainApp(
            reservationManagerSessionBeanRemote,
            guestSessionBeanRemote,
            reservationSessionBeanRemote
            );
        mainApp.runApp();

    }

}
