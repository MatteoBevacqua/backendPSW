package reservations.journey_planner.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reservations.journey_planner.demo.entities.Passenger;
import reservations.journey_planner.demo.requestPOJOs.PassengerDTO;
import reservations.journey_planner.demo.services.PassengerService;

@RestController
@RequestMapping("/users")
public class PassengerController {
    @Autowired private PassengerService passengerService;

    @PostMapping
    public ResponseEntity<Passenger> addPassenger(@RequestBody PassengerDTO passengerDTO) {
        return new ResponseEntity<>(passengerService.addUser(passengerDTO), HttpStatus.OK);
    }

}
