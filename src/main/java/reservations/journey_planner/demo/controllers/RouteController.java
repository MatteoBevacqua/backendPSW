package reservations.journey_planner.demo.controllers;

import org.keycloak.KeycloakPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

    @GetMapping("search/byCity")
    public ResponseEntity<List<Route>> getByCity(@RequestParam(name = "city") String cityName) {
        return new ResponseEntity<>(routeService.findByArrivalCity(cityName), HttpStatus.OK);
    }

    @GetMapping("search/byDepartureCity")
    public ResponseEntity<List<Route>> getByDepartureCity(@RequestParam(name = "city") String cityName, @RequestParam(value = "seatsLeft", required = false) Integer seatsLeft) {
        if (seatsLeft == null)
            return new ResponseEntity<>(routeService.findByDepartureCity(cityName), HttpStatus.OK);
        return new ResponseEntity<>(routeService.findByArrivalCityAndXSeatsLeft(cityName, seatsLeft), HttpStatus.OK);

    }

    @GetMapping("search/byArrivalCity")
    public ResponseEntity<List<Route>> getByArrivalCity(@RequestParam(name = "city") String cityName, @RequestParam(value = "seatsLeft", required = false) Integer seatsLeft) {
        if (seatsLeft == null)
            return new ResponseEntity<>(routeService.findByArrivalCity(cityName), HttpStatus.OK);
        return new ResponseEntity<>(routeService.findByArrivalCityAndXSeatsLeft(cityName, seatsLeft), HttpStatus.OK);
    }


}
