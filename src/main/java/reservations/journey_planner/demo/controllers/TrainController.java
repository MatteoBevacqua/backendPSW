package reservations.journey_planner.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import reservations.journey_planner.demo.entities.Train;
import reservations.journey_planner.demo.services.TrainService;

import java.util.List;

@RestController
@RequestMapping("/trains")
public class TrainController {
    @Autowired TrainService trainService;

    @GetMapping("/all")
    public ResponseEntity<List<Train>> getAll() {
        return new ResponseEntity<>(trainService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/search/byType")
    public ResponseEntity<List<Train>> getTrainsByType(@RequestParam(name = "type") String type) {
        return new ResponseEntity(trainService.findByType(type), HttpStatus.OK);
    }
}
