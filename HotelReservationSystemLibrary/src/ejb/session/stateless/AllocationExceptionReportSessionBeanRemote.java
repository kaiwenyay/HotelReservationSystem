/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AllocationExceptionReport;
import java.time.LocalDate;
import javax.ejb.Remote;
import util.exception.InvalidReportException;

/**
 *
 * @author kwpwn
 */
@Remote
public interface AllocationExceptionReportSessionBeanRemote {

    public AllocationExceptionReport retrieveReport(LocalDate day, boolean fetchReservation, boolean fetchReservationItems, boolean fetchRoomType, boolean fetchRoom) throws InvalidReportException;
    
}
