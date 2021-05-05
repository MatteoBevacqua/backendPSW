package reservations.journey_planner.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.entities.Seat;
import reservations.journey_planner.demo.services.SeatService;

import java.util.List;

@RestController
@RequestMapping("/seats")
public class SeatsController {
    @Autowired SeatService seatService;

    @GetMapping("/available")
    public List<Seat> getAvailableSeatsByRoute(@RequestBody Route r) {
        return seatService.findAvailableByRoute(r);
    }
}
