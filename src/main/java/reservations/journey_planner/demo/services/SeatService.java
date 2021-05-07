package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reservations.journey_planner.demo.entities.Reservation;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.entities.Seat;
import reservations.journey_planner.demo.entities.Train;
import reservations.journey_planner.demo.repositories.ReservationRepository;
import reservations.journey_planner.demo.repositories.RouteRepository;
import reservations.journey_planner.demo.repositories.SeatRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeatService {
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    ReservationRepository reservationRepository;

    public List<Seat> findAll() {
        return seatRepository.findAll();
    }

    public List<Seat> findAvailableByRoute(Route r) {
        return seatRepository.findSeatsNative(r.getId());
    }


}
