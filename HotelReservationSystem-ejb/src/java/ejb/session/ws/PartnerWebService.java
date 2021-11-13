/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejb.session.ws;

import ejb.session.stateful.ReservationManagerSessionBeanLocal;
import ejb.session.stateless.PartnerSessionBeanLocal;
import ejb.session.stateless.ReservationSessionBeanLocal;
import entity.Partner;
import entity.Reservation;
import entity.ReservationItem;
import entity.Room;
import entity.RoomRate;
import entity.RoomType;
import entity.User;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javafx.util.Pair;
import javax.ejb.EJB;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import util.exception.InputDataValidationException;
import util.exception.InvalidCredentialsException;
import util.exception.InvalidPartnerException;
import util.exception.InvalidReservationException;
import util.exception.InvalidRoomException;
import util.exception.InvalidRoomTypeException;
import util.exception.InvalidUserException;
import util.exception.UnknownPersistenceException;

/**
 *
 * @author 81236
 */
@WebService(serviceName = "PartnerWebService")
@Stateless
public class PartnerWebService {

    @PersistenceContext(unitName = "HotelReservationSystem-ejbPU")
    private EntityManager em;

    @EJB
    private PartnerSessionBeanLocal partnerSessionBean;
    
    @EJB
    private ReservationManagerSessionBeanLocal reservationManagerSessionBean;
    
    
    @EJB
    private ReservationSessionBeanLocal reservationSessionBean;

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "partnerLogin")
    public Partner partnerLogin(String username, String password) throws InvalidPartnerException, InvalidCredentialsException {
        Partner partner = partnerSessionBean.partnerLogin(username, password);
        em.detach(partner);
        List<Reservation> reservations = partner.getReservations();
        for (Reservation r : reservations) {
            em.detach(r);
            User user = r.getUser();
            em.detach(user);
            user.setReservations(null);
            List<ReservationItem> items = r.getReservationItems();
            for (ReservationItem i : items) {
                em.detach(i);
                RoomType rt = i.getReservedRoomType();
                em.detach(rt);
                rt.setNextHigherRoomType(null);
                rt.setNextLowerRoomType(null);
                rt.setRoomRates(null);
                rt.setRooms(null);
            }
        }
        return partner;
    }
    
    @WebMethod(operationName = "retrievePartnerByUsername")
    public Partner retrievePartnerByUsername(String username) throws InvalidPartnerException {
        Partner partner = partnerSessionBean.retrievePartnerByUsername(username);
        em.detach(partner);
        List<Reservation> reservations = partner.getReservations();
        for (Reservation r : reservations) {
            em.detach(r);
            r.setUser(null);
        }
        return partner;
    }

    @WebMethod(operationName = "retrieveAllPartners")
    public List<Partner> retrieveAllPartners() {
        List<Partner> partners = partnerSessionBean.retrieveAllPartners();
        for (Partner p : partners) {
            em.detach(p);
            List<Reservation> reservations = p.getReservations();
            for (Reservation r : reservations) {
                em.detach(r);
                r.setUser(null);
            }
        }
        return partners;
    }

    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "searchRooms")
    public List<RoomType> searchRooms(String checkInDateString, String checkOutDateString, Integer noOfRooms) {
        LocalDate checkInDate = LocalDate.parse(checkInDateString, DateTimeFormatter.ISO_DATE);
        LocalDate checkOutDate = LocalDate.parse(checkOutDateString, DateTimeFormatter.ISO_DATE);
        List<RoomType> roomTypes = reservationManagerSessionBean.searchRooms(checkInDate, checkOutDate, noOfRooms);
        
        for (RoomType rt : roomTypes) {
            em.detach(rt);
            rt.setNextHigherRoomType(null);
            rt.setNextLowerRoomType(null);
            List<Room> rooms = rt.getRooms();
            List<RoomRate> roomRates = rt.getRoomRates();
            for (Room r : rooms) {
                em.detach(r);
                r.setRoomTypeNull();
            }
            for (RoomRate rr : roomRates) {
                em.detach(rr);
                rr.setRoomTypeNull();
            }
        }
        return roomTypes;
    }
    @WebMethod(operationName = "calculateSubTotals")
    public List<BigDecimal> calculateSubTotals() {
        List<BigDecimal> subTotals = reservationManagerSessionBean.calculateSubTotals();
        return subTotals;
    }
    
    @WebMethod(operationName = "addReservationItem")
    public void addReservationItem(BigDecimal subTotal, String roomTypeName) throws InvalidRoomTypeException, InputDataValidationException {
        reservationManagerSessionBean.addReservationItem(subTotal, roomTypeName);
    }
    
    @WebMethod(operationName = "reserveRooms")
    public Reservation reserveRooms(String username) throws InvalidRoomException, InvalidUserException, InvalidReservationException, UnknownPersistenceException, InputDataValidationException {
        Reservation reservation = reservationManagerSessionBean.reserveRooms(username);
        
        em.detach(reservation);
        User user = reservation.getUser();
        em.detach(user);
        user.setReservations(null);
        List<ReservationItem> items = reservation.getReservationItems();
        for (ReservationItem i : items) {
            em.detach(i);
            RoomType rt = i.getReservedRoomType();
            em.detach(rt);
            rt.setNextHigherRoomType(null);
            rt.setNextLowerRoomType(null);
            rt.setRoomRates(null);
            rt.setRooms(null);
        }
        
        return reservation;
    }
    
    @WebMethod(operationName = "retrieveReservationsByUser")
    public List<Reservation> retrieveReservationsByUser(String username) throws InvalidReservationException {
        List<Reservation> reservations = reservationSessionBean.retrieveReservationsByUser(username);
        
        for (Reservation r : reservations) {
            em.detach(r);
            User user = r.getUser();
            em.detach(user);
            user.setReservations(null);
            List<ReservationItem> items = r.getReservationItems();
            for (ReservationItem i : items) {
                em.detach(i);
                RoomType rt = i.getReservedRoomType();
                em.detach(rt);
                rt.setNextHigherRoomType(null);
                rt.setNextLowerRoomType(null);
                rt.setRoomRates(null);
                rt.setRooms(null);
            }

        }
        
        return reservations;
        
    }
    
    @WebMethod(operationName = "retrieveCheckInDatesByUser")
    public List<String> retrieveCheckInDatesByUser(String username) throws InvalidReservationException {
        List<Reservation> reservations = reservationSessionBean.retrieveReservationsByUser(username);
        List<String> dates = new ArrayList<>();
        for (Reservation r : reservations) {
            String checkInDate = r.getCheckInDate().format(DateTimeFormatter.ISO_DATE);
            dates.add(checkInDate);
        }
        
        return dates;
        
    }
    
    @WebMethod(operationName = "retrieveCheckOutDatesByUser")
    public List<String> retrieveCheckOutDatesByUser(String username) throws InvalidReservationException {
        List<Reservation> reservations = reservationSessionBean.retrieveReservationsByUser(username);
        List<String> dates = new ArrayList<>();
        for (Reservation r : reservations) {
            String checkOutDate = r.getCheckOutDate().format(DateTimeFormatter.ISO_DATE);
            dates.add(checkOutDate);
        }
        
        return dates;
        
    }
    
    @WebMethod(operationName = "retrieveReservationDateTimesByUser")
    public List<String> retrieveReservationDateTimesByUser(String username) throws InvalidReservationException {
        List<Reservation> reservations = reservationSessionBean.retrieveReservationsByUser(username);
        List<String> dates = new ArrayList<>();
        for (Reservation r : reservations) {
            String reservationDateTime = r.getReservationDateTime().format(DateTimeFormatter.ISO_DATE_TIME);
            dates.add(reservationDateTime);
        }
        
        return dates;
        
    }
    @WebMethod(operationName = "retrieveReservationById")
    public Reservation retrieveReservationById(Long reservationId) throws InvalidReservationException {
        Reservation reservation = reservationSessionBean.retrieveReservationById(reservationId);
         
        em.detach(reservation);
        User user = reservation.getUser();
        em.detach(user);
        user.setReservations(null);
        List<ReservationItem> items = reservation.getReservationItems();
        for (ReservationItem i : items) {
            em.detach(i);
            RoomType rt = i.getReservedRoomType();
            em.detach(rt);
            rt.setNextHigherRoomType(null);
            rt.setNextLowerRoomType(null);
            rt.setRoomRates(null);
            rt.setRooms(null);
        }
         return reservation;
    }
}


