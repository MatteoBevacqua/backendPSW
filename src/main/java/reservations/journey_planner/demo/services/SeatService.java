package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.entities.Seat;
import reservations.journey_planner.demo.repositories.RouteRepository;
import reservations.journey_planner.demo.repositories.SeatRepository;

import java.util.List;

@Service
public class SeatService {
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private RouteRepository routeRepository;


    public List<Seat> findAll() {
        return seatRepository.findAll();
    }


    public List<Seat> findAvailableByRoute(Route r) {
        return seatRepository.findSeatsNative(r.getId());
    }

    public List<Seat> findUnavailableByRoute(Integer r) {
        return seatRepository.findNotAvailable(r);
    }

    @Transactional(readOnly = true)
    public List<Seat> findAvailableByRouteId(Integer id) {
        Route r = routeRepository.findRouteById(id);
        if (r == null) return null;
        return seatRepository.findSeatsNative(id);
    }
}
