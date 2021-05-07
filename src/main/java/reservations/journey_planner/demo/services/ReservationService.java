package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import reservations.journey_planner.demo.entities.*;
import reservations.journey_planner.demo.exceptions.ReservationAlreadyExists;
import reservations.journey_planner.demo.exceptions.SeatsAlreadyBookedException;
import reservations.journey_planner.demo.repositories.PassengerRepository;
import reservations.journey_planner.demo.repositories.ReservationRepository;

import org.springframework.transaction.annotation.Transactional;
import reservations.journey_planner.demo.repositories.RouteRepository;
import reservations.journey_planner.demo.repositories.SeatRepository;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Query;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationService {
    @Autowired
    PassengerService passengerService;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    EntityManager entityManager;

    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = false)
    public List<Reservation> getReservationsByPassenger(Passenger p) {
        Passenger managed = passengerService.savePassengerIfNotExists(p);
        return reservationRepository.findAllByPassenger(managed);
    }

    @Transactional(readOnly = true)
    public Reservation getByPassengerIdAndRoute(String passId, Integer routeid) {
        return reservationRepository.findReservationByPassenger_IdAndBookedRoute_Id(passId, routeid);
    }

    @Transactional(readOnly = false)
    public Reservation addNewReservationIfPossible(Passenger passenger, Route route, List<Seat> seats) {
        Passenger freshP = passengerService.savePassengerIfNotExists(passenger);
        Route test = routeRepository.findRouteById(route.getId());
        if (reservationRepository.existsReservationsByPassenger_IdAndBookedRoute(passenger.getId(), test))
            throw new ReservationAlreadyExists();
        List<Seat> fromDB = seatRepository.findByIdIn(seats.stream().map(Seat::getId).collect(Collectors.toList()));
        if (!seatRepository.findSeatsNative(route.getId()).containsAll(fromDB))
            throw new SeatsAlreadyBookedException();
        Query q = entityManager.createNativeQuery("SELECT * FROM SEATS_PER_RESERVATION AS S WHERE S.SEAT_ID IN (:ids) AND S.ROUTE_ID=(:route) FOR UPDATE", SeatsInReservation.class);
        q.setParameter("ids", seats.stream().map(Seat::getId).collect(Collectors.toList())).setParameter("route", route.getId());
        List<SeatsInReservation> inReservations = q.getResultList();
        Reservation r = new Reservation();
        freshP.setDistance_travelled(freshP.getDistance_travelled() + test.getRouteLength());
        test.setAvailableSeats(test.getAvailableSeats() - fromDB.size());
        r.setBookedRoute(test);
        r.setPassenger(freshP);
        r.setSeatsBooked(inReservations.stream().map(SeatsInReservation::getSeat).collect(Collectors.toList()));
        r = reservationRepository.save(r);
        Iterator<Seat> seatIt = fromDB.iterator();
        for (SeatsInReservation x : inReservations) {
            x.setReservation(r);
            x.setSeat(seatIt.next());
        }
        inReservations.forEach(entityManager::persist);
        return r;

    }

}
