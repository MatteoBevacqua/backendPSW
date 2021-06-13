package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reservations.journey_planner.demo.configuration.EmailManagerBean;
import reservations.journey_planner.demo.configuration.Utils;
import reservations.journey_planner.demo.entities.*;
import reservations.journey_planner.demo.exceptions.NoSuchPassengerException;
import reservations.journey_planner.demo.exceptions.NoSuchReservationException;
import reservations.journey_planner.demo.exceptions.ReservationAlreadyExists;
import reservations.journey_planner.demo.exceptions.SeatsAlreadyBookedException;
import reservations.journey_planner.demo.repositories.PassengerRepository;
import reservations.journey_planner.demo.repositories.ReservationRepository;
import reservations.journey_planner.demo.repositories.SeatInReservationRepository;
import reservations.journey_planner.demo.repositories.SeatRepository;
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
    private SeatRepository seatRepository;
    @Autowired
    private SeatInReservationRepository seatInReservationRepository;
    @Autowired
    EntityManager entityManager;
    static Map<String, Object> timeout = Map.of("javax.persistence.lock.timeout", 20);
    @Autowired
    private EmailManagerBean emailManager;

    @Transactional(readOnly = true)
    public List<Reservation> getReservationsByPassenger(Passenger p) {
        Jwt jwt = Utils.getPrincipal();
        String pId = Utils.getPassengerFromToken(jwt).getId();
        Passenger managed = passengerRepository.findPassengerById(pId);
        if (managed == null) throw new NoSuchPassengerException();
        return reservationRepository.findAllByPassenger(managed);
    }

    @Transactional(readOnly = true)
    public Reservation getByPassengerIdAndRoute(String passId, Integer routeid) {
        return reservationRepository.findReservationByPassenger_IdAndBookedRoute_Id(passId, routeid);
    }

    @Deprecated
    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
    public Reservation addNewReservationLockTable(Passenger passenger, Route route, List<Seat> seats) {
        Passenger passengerData = passengerRepository.findPassengerById(passenger.getId());
        if (passengerData == null)
            throw new NoSuchPassengerException();
        if (reservationRepository.existsReservationsByPassenger_IdAndBookedRoute_Id(passenger.getId(), route.getId()))
            throw new ReservationAlreadyExists();
        Route routetoBook = entityManager.find(Route.class, route.getId());
        entityManager.createNativeQuery("LOCK TABLES SEATS_PER_RESERVATION WRITE,SEAT READ,ACTIVE_ROUTES AS R READ,RESERVATION WRITE;").executeUpdate();
        List<Seat> availableForRoute = seatRepository.findSeatsNative(route.getId());
        if (!availableForRoute.containsAll(seats)) {
            entityManager.createNativeQuery("UNLOCK TABLES").executeUpdate();
            throw new SeatsAlreadyBookedException();
        }
        Reservation r = new Reservation();
        r.setBookedRoute(routetoBook);
        r.setPassenger(passengerData);
        r = reservationRepository.save(r);
        Reservation finalR = r;
        seats.forEach(
                seat -> {
                    SeatsAndReservation seatToReserve = new SeatsAndReservation();
                    Seat seatFromDB = availableForRoute.stream().filter(seat1 -> seat1.getId() == seat.getId()).findFirst().get();
                    seatToReserve.setSeat(seatFromDB);
                    seatToReserve.setReservation(finalR);
                    seatToReserve.setRoute(routetoBook);
                    seatToReserve.setPricePaid(seat.getPricePaid());
                    seatInReservationRepository.save(seatToReserve); //salvato automaticamente
                }
        );
        entityManager.createNativeQuery("UNLOCK TABLES;").executeUpdate();
        Route newOne = entityManager.find(Route.class, route.getId(), LockModeType.PESSIMISTIC_WRITE);
        newOne.setSeatsLeft(newOne.getSeatsLeft() - seats.size());
        entityManager.persist(newOne);
        passengerData.setDistanceTravelled(passengerData.getDistanceTravelled() + routetoBook.getRouteLength());
        emailManager.sendTextEmail(r.toString(), "Reservation #" + r.getId() + " added", passenger);
        return r;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Reservation addNewReservation(Passenger passenger, Route route, List<Seat> seats) {
        Passenger passengerData = passengerRepository.findPassengerById(passenger.getId());
        if (passengerData == null)
            throw new NoSuchPassengerException();
        if (reservationRepository.existsReservationsByPassenger_IdAndBookedRoute_Id(passenger.getId(), route.getId()))
            throw new ReservationAlreadyExists();
        List<Seat> availableForRoute = seatRepository.findSeatsNative(route.getId());
        if (!availableForRoute.containsAll(seats))
            throw new SeatsAlreadyBookedException();
        final Reservation r = new Reservation();
        Route routetoBook = entityManager.find(Route.class, route.getId(), LockModeType.PESSIMISTIC_WRITE);
        seats.forEach(
                seat -> {
                    SeatsAndReservation seatToReserve = new SeatsAndReservation();
                    seatToReserve.setRoute(routetoBook);
                    Optional<Seat> seatFromDB = seatRepository.findById(seat.getId());
                    if (seatFromDB.isEmpty()) throw new RuntimeException("No such seat");
                    Seat temp = seatFromDB.get();
                    seatToReserve.setSeat(seatFromDB.get());
                    seatToReserve.setReservation(r);
                    if (seat.getPricePaid() != temp.getChildrenPrice() && seat.getPricePaid() != temp.getAdultPrice())
                        throw new RuntimeException("Illegal price range");
                    seatToReserve.setPricePaid(seat.getPricePaid());
                    r.getReservedSeats().add(seatToReserve);
                }
        );
        r.setBookedRoute(routetoBook);
        r.setPassenger(passengerData);
        Reservation reservation = reservationRepository.save(r);
        routetoBook.setSeatsLeft(routetoBook.getSeatsLeft() - seats.size());
        entityManager.lock(routetoBook, LockModeType.NONE);
        entityManager.persist(routetoBook);
        passengerData.setDistanceTravelled(passengerData.getDistanceTravelled() + routetoBook.getRouteLength());
        emailManager.sendTextEmail(r.toString(), "Reservation #" + r.getId() + " added", passenger);
        return reservation;
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public void deleteReservation(Integer id, Passenger p) {
        Reservation res;
        if ((res = reservationRepository.findByIdAndPassenger_Id(id, p.getId())) == null)
            throw new NoSuchReservationException();
        Route route;
        entityManager.lock(route = res.getBookedRoute(), LockModeType.PESSIMISTIC_WRITE);
        entityManager.refresh(route);
        route.setSeatsLeft(route.getSeatsLeft() + res.getReservedSeats().size());
        reservationRepository.delete(res);
        emailManager.sendTextEmail("Your reservation #" + res.getId() + "has been deleted", "Booking #" + res.getId() + " deleted", p);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED)
    public Reservation modifyReservation(Passenger p, ModifiedBookingDTO mod) {
        Reservation r;
        if ((r = reservationRepository.findByIdAndPassenger_Id(mod.getToModify().getId(), p.getId())) == null)
            throw new NoSuchReservationException();
        List<SeatsAndReservation> alreadyBookedByRoute = seatInReservationRepository.findAllByRoute_IdAndSeatIdIn(r.getBookedRoute().getId(), mod.getToAdd().stream().map(Seat::getId).collect(Collectors.toList()));
        if (alreadyBookedByRoute.size() > 0) throw new SeatsAlreadyBookedException();
        List<SeatsAndReservation> inReservation = r.getReservedSeats();
        ListIterator<SeatsAndReservation> iterator = inReservation.listIterator();
        SeatsAndReservation next;
        while (iterator.hasNext()) {
            next = iterator.next();
            if (mod.getToRemove().contains(next.getSeat())) iterator.remove();
        }
        mod.getChangePrice().forEach(seat -> {
            Optional<SeatsAndReservation> s = inReservation.stream().filter(res -> res.getSeat().getId() == seat.getId()).findFirst();
            if (s.isPresent()) {
                Optional<Seat> allowedPrices = seatRepository.findById(s.get().getSeat().getId());
                if (allowedPrices.isEmpty()) throw new RuntimeException("No such seat");
                Seat price = allowedPrices.get();
                if (seat.getPricePaid() == price.getAdultPrice() || seat.getPricePaid() == price.getChildrenPrice())
                    seatInReservationRepository.findBySeat_IdAndReservation_Id(seat.getId(), r.getId()).setPricePaid(seat.getPricePaid());
                else throw new RuntimeException("Illegal price range!");
            } else
                throw new RuntimeException("Bad seat specified");
        });
        mod.getToAdd().forEach(seatToAdd -> {
            if (alreadyBookedByRoute.stream().anyMatch(seat -> seat.getSeat().getId() == seatToAdd.getId()))
                throw new SeatsAlreadyBookedException();
            SeatsAndReservation seatsAndReservation = new SeatsAndReservation();
            Optional<Seat> toAdd;
            if ((toAdd = seatRepository.findById(seatToAdd.getId())).isEmpty())
                throw new RuntimeException("No such seat");
            seatsAndReservation.setSeat(toAdd.get());
            seatsAndReservation.setRoute(r.getBookedRoute());
            seatsAndReservation.setReservation(r);
            seatsAndReservation.setPricePaid(seatToAdd.getPricePaid());
            r.getReservedSeats().add(seatsAndReservation);
        });
        Route booked = r.getBookedRoute();
        entityManager.lock(booked, LockModeType.PESSIMISTIC_WRITE);
        entityManager.refresh(booked);
        booked.setSeatsLeft(booked.getSeatsLeft() + mod.getToRemove().size() - mod.getToAdd().size());
        emailManager.sendTextEmail(r.toString(), "Modified booking #" + r.getId(), p);
        return r;
    }

}
