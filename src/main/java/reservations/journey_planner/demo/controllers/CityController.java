package reservations.journey_planner.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reservations.journey_planner.demo.entities.City;
import reservations.journey_planner.demo.services.CityService;

import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "If-Match")
@RestController
@RequestMapping("/cities")
public class CityController {
    @Autowired
    private CityService cityService;

    @GetMapping("/all")
    public ResponseEntity<List<City>> getAll() {
        return new ResponseEntity<>(cityService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/search/byName")
    public ResponseEntity<List<City>> getByName(@RequestParam(name = "name") String name) {
        return new ResponseEntity<>(cityService.findByName(name), HttpStatus.OK);
    }

    @GetMapping("/search/like")
    public ResponseEntity<List<City>> getByNameLike(@RequestParam(name = "pattern") String pattern) {
        return new ResponseEntity<>(cityService.getByNameLike(pattern), HttpStatus.OK);
    }
}
