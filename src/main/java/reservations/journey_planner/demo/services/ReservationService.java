package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import reservations.journey_planner.demo.configuration.Utils;
import reservations.journey_planner.demo.entities.*;
import reservations.journey_planner.demo.exceptions.NoSuchPassengerException;
import reservations.journey_planner.demo.exceptions.NoSuchReservationException;
import reservations.journey_planner.demo.exceptions.ReservationAlreadyExists;
import reservations.journey_planner.demo.exceptions.SeatsAlreadyBookedException;
import reservations.journey_planner.demo.repositories.*;

import org.springframework.transaction.annotation.Transactional;
import reservations.journey_planner.demo.requestPOJOs.ModifiedBookingDTO;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import java.util.*;
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
    @Autowired
    EntityManager entityManager;
    static Map<String, Object> timeout = Map.of("javax.persistence.lock.timeout", 2);


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

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Reservation addNewReservationIfPossible(Passenger passenger, Route route, List<Seat> seats) {

        Passenger freshP = passengerRepository.findPassengerById(passenger.getId());
        if (freshP == null)
            throw new NoSuchPassengerException();
        if (reservationRepository.existsReservationsByPassenger_IdAndBookedRoute_Id(passenger.getId(), route.getId()))
            throw new ReservationAlreadyExists();
        List<Seat> availableForRoute = seatRepository.findSeatsNative(route.getId());
        List<Seat> fromDB = seatRepository.findByIdIn(seats.stream().map(Seat::getId).collect(Collectors.toList()));
        if (!availableForRoute.containsAll(fromDB))
            throw new SeatsAlreadyBookedException(availableForRoute);
        System.out.println("asd");
        Reservation r = new Reservation();
        System.out.println(route);
        Route DB = entityManager.find(Route.class, route.getId(), LockModeType.PESSIMISTIC_WRITE);
        DB.setSeatsLeft(DB.getSeatsLeft() - fromDB.size());
        entityManager.persist(DB);
        entityManager.lock(DB,LockModeType.NONE);
        r.setPassenger(freshP);
        r.setBookedRoute(DB);
        SeatsAndReservation[] res = new SeatsAndReservation[1];
        Iterator<Seat> it = fromDB.iterator();
        List<SeatsAndReservation> reserv = fromDB.stream().map(
                seat -> {
                    res[0] = new SeatsAndReservation();
                    res[0].setRoute(DB);
                    res[0].setSeat(it.next());
                    res[0].setReservation(r);
                    return res[0];
                }
        ).collect(Collectors.toList());
        List<SeatsAndReservation> managed =
                reserv.stream().map(seat -> seatInReservationRepository.save(seat)).collect(Collectors.toList());
        freshP.setDistance_travelled(freshP.getDistance_travelled() + DB.getRouteLength());
        r.setReserved_seats(managed);
        r.setBookedRoute(DB);
        r.setPassenger(freshP);
        r.setReserved_seats(managed);
        Reservation ret = reservationRepository.save(r);
        return ret;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteReservation(Reservation r, Passenger p) {
        Reservation res;
        if ((res = reservationRepository.findByIdAndPassenger_Id(r.getId(), p.getId())) == null)
            throw new NoSuchReservationException();
        List<SeatsAndReservation> seats = seatInReservationRepository.findAllByRoute_IdAndReservation_Id(res.getBookedRoute().getId(), res.getId());
        seats.forEach(seatInReservationRepository::delete);
        Route route;
        entityManager.lock(route = res.getBookedRoute(), LockModeType.PESSIMISTIC_WRITE, timeout);
        route.setSeatsLeft(route.getSeatsLeft() + seats.size());
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
