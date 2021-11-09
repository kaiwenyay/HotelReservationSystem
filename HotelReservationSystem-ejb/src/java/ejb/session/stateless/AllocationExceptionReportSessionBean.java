/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.stateless;

import entity.AllocationExceptionReport;
import entity.Reservation;
import entity.ReservationItem;
import java.time.LocalDate;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import util.exception.InvalidReportException;

/**
 *
 * @author kwpwn
 */
@Stateless
public class AllocationExceptionReportSessionBean implements AllocationExceptionReportSessionBeanRemote, AllocationExceptionReportSessionBeanLocal {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @Override
    public AllocationExceptionReport retrieveReport(LocalDate day, boolean fetchReservation, boolean fetchReservationItems, boolean fetchRoomType, boolean fetchRoom) throws InvalidReportException {
        AllocationExceptionReport report;
        try {
            report = em.createNamedQuery("retrieveAllocationExceptionReportByDay", AllocationExceptionReport.class)
                    .setParameter("inDay", day)
                    .getSingleResult();
            if (fetchReservation) {
                List<Reservation> reservations = report.getReservations();
                reservations.size();
                if (fetchReservationItems) {
                    for (Reservation r : reservations) {
                        r.getReservationItems().size();
                        if (fetchRoomType || fetchRoom) {
                            List<ReservationItem> items = r.getReservationItems();
                            for (ReservationItem i : items) {
                                if (fetchRoomType) {
                                    i.getReservedRoomType();
                                }
                                if (fetchRoom) {
                                    i.getAllocatedRoom();
                                }
                            }
                        }
                    }
                }
            }
        } catch (NoResultException e) {
            throw new InvalidReportException(String.format("Report on day %s does not exist.", day));
        }
        return report;
    }
}
