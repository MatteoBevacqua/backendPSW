package reservations.journey_planner.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reservations.journey_planner.demo.entities.TrainStation;
import reservations.journey_planner.demo.services.CityService;
import reservations.journey_planner.demo.services.TrainStationService;

import java.util.List;

@RestController
@RequestMapping("/stations")
public class TrainStationController {
    @Autowired
    TrainStationService trainStationService;

    @GetMapping("/all")
    public ResponseEntity<List<TrainStation>> getAll() {
        return new ResponseEntity<>(trainStationService.getAll(), HttpStatus.OK);
    }

    @GetMapping("/search/byCity")
    public ResponseEntity<List<TrainStation>> getAll(@RequestParam(name = "city") String city) {
        return new ResponseEntity<>(trainStationService.getByCity(city), HttpStatus.OK);
    }
}
