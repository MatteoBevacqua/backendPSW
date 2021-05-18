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
    @Autowired
    SeatService seatService;

    @GetMapping("/availableByRoute")
    public List<Seat> getAvailableSeatsByRoute(@RequestParam(name = "route_id") Integer r) {
        return seatService.findAvailableByRouteId(r);
    }

    @GetMapping("/notAvailableByRoute")
    public List<Seat> findUnavailableByRoute(@RequestParam(name = "route_id") Integer r) {
        return seatService.findUnavailableByRoute(r);
    }

    @GetMapping("/byRouteWithSelectionStatus")
    public List<Seat> displayerEndpoint(@RequestParam(name = "route_id") Integer routeId) {
        List<Seat> available = seatService.findAvailableByRouteId(routeId);
        List<Seat> unavailable = seatService.findUnavailableByRoute(routeId);
        available.forEach(seat -> seat.setBooked(false));
        unavailable.forEach(seat -> seat.setBooked(true));
        available.addAll(unavailable);
        return available;
    }
}
