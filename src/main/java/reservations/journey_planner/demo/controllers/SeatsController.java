package reservations.journey_planner.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.entities.Seat;
import reservations.journey_planner.demo.services.SeatService;

import java.util.List;

@RestController
@RequestMapping("/seats")
public class SeatsController {
    @Autowired SeatService seatService;

    @GetMapping("/available")
    public List<Seat> getAvailableSeatsByRoute(@RequestParam(name="route_id") Integer r) {
        return seatService.findAvailableByRouteId(r);
    }
}
