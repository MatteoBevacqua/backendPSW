package reservations.journey_planner.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.services.RouteService;

import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteController {
    @Autowired
    RouteService routeService;

    @GetMapping("/all")
    public ResponseEntity<List<Route>> getAll() {
        return new ResponseEntity<>(routeService.findAll(), HttpStatus.OK);
    }
}
