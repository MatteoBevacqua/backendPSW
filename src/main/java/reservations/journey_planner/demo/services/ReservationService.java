package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reservations.journey_planner.demo.entities.*;
import reservations.journey_planner.demo.exceptions.NoSuchReservationException;
import reservations.journey_planner.demo.exceptions.ReservationAlreadyExists;
import reservations.journey_planner.demo.exceptions.SeatsAlreadyBookedException;
import reservations.journey_planner.demo.repositories.*;

import org.springframework.transaction.annotation.Transactional;
import reservations.journey_planner.demo.requestPOJOs.ModifiedBookingDTO;

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
        Passenger freshP = passengerRepository.findPassengerById(passenger.getId());
        Route test = routeRepository.findRouteById(route.getId());
        if (reservationRepository.existsReservationsByPassenger_IdAndBookedRoute(passenger.getId(), test))
            throw new ReservationAlreadyExists();
        List<SeatsAndReservation> inReservations = seatInReservationRepository.findAllBySeat_IdInAndRoute_IdAndReservationIsNull(seats.stream().map(Seat::getId).collect(Collectors.toList()), route.getId());
        inReservations.forEach(res -> System.out.println(res.getSeat() + " " + res.getReservation()));
        if (inReservations.size() != seats.size()) { //qualcuno potrebbe essere stato prenotato
            List<Seat> availableLeft = inReservations.stream().map(SeatsAndReservation::getSeat).collect(Collectors.toList());
            throw new SeatsAlreadyBookedException(availableLeft);
        }
        Reservation r = new Reservation();
        List<Seat> fromDB = seatRepository.findByIdIn(seats.stream().map(Seat::getId).collect(Collectors.toList()));
        freshP.setDistance_travelled(freshP.getDistance_travelled() + test.getRouteLength());
        r.setBookedRoute(test);
        r.setPassenger(freshP);
        r.setReserved_seats(inReservations);
        inReservations.forEach(seatAndRes -> seatAndRes.setReservation(r));
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
