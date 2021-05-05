package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reservations.journey_planner.demo.entities.Passenger;
import reservations.journey_planner.demo.entities.Reservation;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.exceptions.ReservationAlreadyExists;
import reservations.journey_planner.demo.repositories.PassengerRepository;
import reservations.journey_planner.demo.repositories.ReservationRepository;

import org.springframework.transaction.annotation.Transactional;
import reservations.journey_planner.demo.repositories.RouteRepository;

import java.util.Date;
import java.util.List;

@Service
public class ReservationService {
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private PassengerRepository passengerRepository;
    @Autowired
    private RouteRepository routeRepository;

    @Transactional(readOnly = true)
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Transactional(readOnly = false)
    public List<Reservation> getReservationsByPassenger(Passenger p) {
        if (!passengerRepository.findById(p.getId()).isPresent()) {
            passengerRepository.save(p);
            passengerRepository.flush();
        }
        return reservationRepository.findAllByPassenger(p);
    }

    @Transactional(readOnly = false)
    public Reservation addNewReservationIfPossible(Passenger passenger, Route route) {
        if (!passengerRepository.existsById(passenger.getId())) {
            passengerRepository.save(passenger);
        }
        String firstName = route.getDepartureStation().getName();
        String last = route.getArrivalStation().getName();
        Date depTime = route.getDepartureTime();
        Route test = routeRepository.findRouteByDepartureStation_NameAndArrivalStation_NameAndDepartureTime(firstName, last, depTime);
        System.out.println(passenger.getId());
        if (reservationRepository.existsReservationsByPassenger_IdAndBookedRoute(passenger.getId(), test)) {
            throw new ReservationAlreadyExists();
        }
        Reservation r = new Reservation();
        r.setPassenger(passenger);
        r.setBookedRoute(test);
        passenger.setDistance_travelled(passenger.getDistance_travelled() + test.getRouteLength());
        return reservationRepository.save(r);

    }


}
