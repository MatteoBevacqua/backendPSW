package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import reservations.journey_planner.demo.entities.*;
import reservations.journey_planner.demo.exceptions.ReservationAlreadyExists;
import reservations.journey_planner.demo.exceptions.SeatsAlreadyBookedException;
import reservations.journey_planner.demo.repositories.*;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
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
    private SeatInReservationRepository seatInReservationRepository;

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
        System.out.println(passenger.getId());
        System.out.println(route.getId());
        if (reservationRepository.existsReservationsByPassenger_IdAndBookedRoute(passenger.getId(), test))
            throw new ReservationAlreadyExists();
        List<SeatsInReservation> inReservations = seatInReservationRepository.findAllBySeat_IdInAndRoute_IdAndReservationIsNull(seats.stream().map(Seat::getId).collect(Collectors.toList()), route.getId());
        if (inReservations.size() != seats.size()) { //qualcuno potrebbe essere stato prenotato
            //nessun problema perchè dopo l'eccezione i seats diventano detached
            List<Seat> availableLeft = inReservations.stream().map(SeatsInReservation::getSeat).collect(Collectors.toList());
            throw new SeatsAlreadyBookedException(availableLeft);
        }
        Reservation r = new Reservation();
        List<Seat> fromDB = seatRepository.findByIdIn(seats.stream().map(Seat::getId).collect(Collectors.toList()));
        freshP.setDistance_travelled(freshP.getDistance_travelled() + test.getRouteLength());
        test.setAvailableSeats(test.getAvailableSeats() - fromDB.size());
        r.setBookedRoute(test);
        r.setPassenger(freshP);
        r.setReserved_seats(inReservations);
        r.setSeatsBooked(inReservations.stream().map(SeatsInReservation::getSeat).collect(Collectors.toList()));
        Iterator<Seat> seatIt = fromDB.iterator();
        for (SeatsInReservation x : inReservations) {
            x.setReservation(r);
            x.setSeat(seatIt.next());
            seatInReservationRepository.save(x);
        }
        passengerRepository.save(freshP);
        return reservationRepository.save(r);

    }

}
