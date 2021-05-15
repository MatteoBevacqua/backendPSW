package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import reservations.journey_planner.demo.configuration.Utils;
import reservations.journey_planner.demo.entities.*;
import reservations.journey_planner.demo.exceptions.NoSuchPassengerException;
import reservations.journey_planner.demo.exceptions.NoSuchReservationException;
import reservations.journey_planner.demo.exceptions.ReservationAlreadyExists;
import reservations.journey_planner.demo.exceptions.SeatsAlreadyBookedException;
import reservations.journey_planner.demo.repositories.*;

import org.springframework.transaction.annotation.Transactional;
import reservations.journey_planner.demo.requestPOJOs.ModifiedBookingDTO;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
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

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    public List<Reservation> getReservationsByPassenger(Passenger p) {
        Jwt jwt = Utils.getPrincipal();
        String pId = Utils.getPassengerFromToken(jwt).getId();
        Passenger managed = passengerRepository.findPassengerById(pId);
        if (managed == null) throw new NoSuchPassengerException();
        return reservationRepository.findAllByPassenger(managed);
    }

    public Reservation getByPassengerIdAndRoute(String passId, Integer routeid) {
        return reservationRepository.findReservationByPassenger_IdAndBookedRoute_Id(passId, routeid);
    }

    @Transactional(readOnly = false)
    public Reservation addNewReservationIfPossible(Passenger passenger, Route route, List<Seat> seats) {
        Passenger freshP = passengerRepository.findPassengerById(passenger.getId());
        Route test = routeRepository.findRouteById(route.getId());
        if (reservationRepository.existsReservationsByPassenger_IdAndBookedRoute(passenger.getId(), test))
            throw new ReservationAlreadyExists();
        List<Seat> availableForRoute = seatRepository.findSeatsNative(test.getId(),test.getTrain().getTrain_id());
        System.out.println(test.getId()+ " " + test.getTrain().getTrain_id());
        System.out.println(availableForRoute.size() + " av ");
        System.out.println(test.getSeatsLeft());
        if (test.getSeatsLeft() < seats.size())
            throw new SeatsAlreadyBookedException(availableForRoute);
        List<Seat> fromDB = seatRepository.findByIdIn(seats.stream().map(Seat::getId).collect(Collectors.toList()));
        if (!availableForRoute.containsAll(fromDB))  //qualcuno potrebbe essere stato prenotato
            throw new SeatsAlreadyBookedException(availableForRoute);
        System.out.println("heeeerrr");
        test.setSeatsLeft(test.getSeatsLeft() - seats.size()); //  "lock"
        routeRepository.save(test); //a mo' di barriera
        Reservation r = new Reservation();
   //     freshP.setDistance_travelled(freshP.getDistance_travelled() + test.getRouteLength());
        r.setBookedRoute(test);
        r.setPassenger(freshP);
        SeatsAndReservation[] res = new SeatsAndReservation[1];
        Iterator<Seat> it = seats.iterator();
        List<SeatsAndReservation> reserv = fromDB.stream().map(
                seat -> {
                    res[0] = new SeatsAndReservation();
                    res[0].setRoute(test);
                    res[0].setSeat(it.next());
                    res[0].setReservation(r);
                    return res[0];
                }
        ).collect(Collectors.toList());
        reserv.forEach(seat -> seatInReservationRepository.save(seat));
        r.setReserved_seats(reserv);
        return reservationRepository.save(r);

    }

    @Transactional(readOnly = false)
    public void deleteReservation(Reservation r, Passenger p) {
        Reservation res;
        if ((res = reservationRepository.findByIdAndPassenger_Id(r.getId(), p.getId())) == null)
            throw new NoSuchReservationException();
        List<SeatsAndReservation> seats = seatInReservationRepository.findAllByRoute_IdAndReservation_Id(res.getBookedRoute().getId(), res.getId());
        Route route = routeRepository.findRouteById(res.getId());
        seats.forEach(seat -> seat.setReservation(null));
        reservationRepository.delete(res);
    }

    @Transactional(readOnly = false)
    public Reservation modifyReservation(Passenger p, ModifiedBookingDTO mod) {
        Reservation r;
        if ((r = reservationRepository.findByIdAndPassenger_Id(mod.getToModify().getId(), p.getId())) == null)
            throw new NoSuchReservationException();
        List<Integer> toAddIds = mod.getToAdd().stream().map(Seat::getId).collect(Collectors.toList());
        List<SeatsAndReservation> toAddFromDb = seatInReservationRepository.findAllBySeat_IdInAndRoute_IdAndReservationIsNull(toAddIds, r.getBookedRoute().getId());
        if (toAddFromDb.size() != mod.getToAdd().size()) { //nel frattempo qualcuno è stato prenotato
            List<Seat> availableLeft = toAddFromDb.stream().map(SeatsAndReservation::getSeat).collect(Collectors.toList());
            throw new SeatsAlreadyBookedException(availableLeft);
        }
        List<SeatsAndReservation> toRemove = seatInReservationRepository.findAllByRoute_IdAndReservation_IdAndSeat_IdIn(r.getBookedRoute().getId(), r.getId(), mod.getToRemove().stream().map(Seat::getId).collect(Collectors.toList()));
        toAddFromDb.forEach(seat -> seat.setReservation(r));
        toRemove.forEach(seat -> seat.setReservation(null));
        return r;
    }

}
