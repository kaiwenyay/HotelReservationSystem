/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AllocationExceptionReport;
import java.time.LocalDate;
import javax.ejb.Local;
import util.exception.InputDataValidationException;
import util.exception.InvalidReportException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author kwpwn
 */
@Local
public interface AllocationExceptionReportSessionBeanLocal {

    public AllocationExceptionReport createReport(LocalDate day) throws InvalidReportException, UnknownPersistenceException, InputDataValidationException;
    
}
