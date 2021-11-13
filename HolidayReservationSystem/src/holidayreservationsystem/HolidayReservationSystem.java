/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package holidayreservationsystem;

import ws.client.PartnerWebService;
import ws.client.PartnerWebService_Service;


/**
 *
 * @author kwpwn
 */
public class HolidayReservationSystem {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        PartnerWebService partnerService = (new PartnerWebService_Service()).getPartnerWebServicePort();
        MainApp mainApp = new MainApp(partnerService);
        mainApp.runApp();

    }
    
}
