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
        Reservation r = new Reservation();
        Route DB = entityManager.find(Route.class, route.getId(), LockModeType.PESSIMISTIC_WRITE);
        DB.setSeatsLeft(DB.getSeatsLeft() - fromDB.size());
        entityManager.persist(DB);
        entityManager.lock(DB, LockModeType.NONE);
        r.setPassenger(freshP);
        r.setBookedRoute(DB);
        Seat[] prev = new Seat[1];
        fromDB.forEach(seat -> {
            prev[0] = seats.get(seats.indexOf(seat));
            seat.setPricePaid(prev[0].getPricePaid());
        });
        SeatsAndReservation[] res = new SeatsAndReservation[1];
        Iterator<Seat> it = fromDB.iterator();
        List<SeatsAndReservation> reserv = fromDB.stream().map(
                seat -> {
                    res[0] = new SeatsAndReservation();
                    res[0].setRoute(DB);
                    res[0].setSeat(prev[0] = it.next());
                    res[0].setReservation(r);
                    res[0].setPricePaid(prev[0].getPricePaid());
                    return res[0];
                }
        ).collect(Collectors.toList());
        List<SeatsAndReservation> managed =
                reserv.stream().map(seat -> seatInReservationRepository.save(seat)).collect(Collectors.toList());
        freshP.setDistanceTravelled(freshP.getDistanceTravelled() + DB.getRouteLength());
        r.setReservedSeats(managed);
        r.setBookedRoute(DB);
        r.setPassenger(freshP);
        return reservationRepository.save(r);

    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteReservation(Integer id, Passenger p) {
        Reservation res;
        if ((res = reservationRepository.findByIdAndPassenger_Id(id, p.getId())) == null)
            throw new NoSuchReservationException();
        List<SeatsAndReservation> seats = seatInReservationRepository.findAllByRoute_IdAndReservation_Id(res.getBookedRoute().getId(), res.getId());
        Route route;
        entityManager.lock(route = res.getBookedRoute(), LockModeType.PESSIMISTIC_WRITE);
        System.out.println(res.getReservedSeats());
        System.out.println();
        System.out.println(seats);
        System.out.println(route.getSeatsLeft());
        route.setSeatsLeft(route.getSeatsLeft() + res.getReservedSeats().size());
        System.out.println(route.getSeatsLeft());
        reservationRepository.delete(res);
    }

    @Transactional(readOnly = false)
    public Reservation modifyReservation(Passenger p, ModifiedBookingDTO mod) {
        Reservation r;
        if ((r = reservationRepository.findByIdAndPassenger_Id(mod.getToModify().getId(), p.getId())) == null)
            throw new NoSuchReservationException();
        List<Integer> idsToAdd = mod.getToAdd().stream().map(Seat::getId).collect(Collectors.toList());
        List<SeatsAndReservation> alreadyBookedByRoute = seatInReservationRepository.findAllByRoute_IdAndSeatIdIn(r.getBookedRoute().getId(), idsToAdd);
        System.out.println(idsToAdd);
        System.out.println(alreadyBookedByRoute + "  b  ");
        if (alreadyBookedByRoute.size() > 0) throw new SeatsAlreadyBookedException();
        List<SeatsAndReservation> toRemove = seatInReservationRepository.findAllByRoute_IdAndReservation_IdAndSeat_IdIn(r.getBookedRoute().getId(), r.getId(), mod.getToRemove().stream().map(Seat::getId).collect(Collectors.toList()));
        List<SeatsAndReservation> inReservation = r.getReservedSeats();
        toRemove.forEach(inReservation::remove);
        mod.getChangePrice().forEach(seat -> {
            Optional<SeatsAndReservation> s = inReservation.stream().filter(res -> res.getSeat().getId() == seat.getId()).findFirst();
            if (s.isPresent()) {
                Optional<Seat> allowedPrices = seatRepository.findById(s.get().getSeat().getId());
                if (allowedPrices.isEmpty()) throw new RuntimeException("NO such seat");
                Seat price = allowedPrices.get();
                if (seat.getPricePaid() == price.getAdultPrice() || seat.getPricePaid() == price.getChildrenPrice())
                    seatInReservationRepository.findBySeat_IdAndReservation_Id(seat.getId(), r.getId()).setPricePaid(seat.getPricePaid());
                else throw new RuntimeException("Illegal price range!");
            } else
                throw new RuntimeException("Bad seat specified");
        });
        mod.getToAdd().forEach(seatToAdd -> {
            if (alreadyBookedByRoute.stream().anyMatch(seat -> seat.getSeat().getId() == seatToAdd.getId()))
                throw new RuntimeException("Unavailable seats");
            SeatsAndReservation seatsAndReservation = new SeatsAndReservation();
            Optional<Seat> toAdd;
            if ((toAdd = seatRepository.findById(seatToAdd.getId())).isEmpty())
                throw new RuntimeException("No such seat");
            seatsAndReservation.setSeat(toAdd.get());
            seatsAndReservation.setRoute(r.getBookedRoute());
            seatsAndReservation.setReservation(r);
            seatsAndReservation.setPricePaid(seatToAdd.getPricePaid());
            r.getReservedSeats().add(seatsAndReservation); //cascade persist automatico
        });
        Route booked = r.getBookedRoute();
        entityManager.lock(booked, LockModeType.PESSIMISTIC_WRITE);
        booked.setSeatsLeft(booked.getSeatsLeft() + toRemove.size() - mod.getToAdd().size());
        return r;
    }

}
