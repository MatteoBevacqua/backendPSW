package reservations.journey_planner.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reservations.journey_planner.demo.entities.Passenger;
import reservations.journey_planner.demo.exceptions.EmailAlreadyInUseException;
import reservations.journey_planner.demo.requestPOJOs.PassengerDTO;
import reservations.journey_planner.demo.services.PassengerService;

@RestController
@RequestMapping("/users")
public class PassengerController {
    @Autowired
    private PassengerService passengerService;

    @PostMapping
    public ResponseEntity addPassenger(@RequestBody PassengerDTO passengerDTO) {
        Passenger p;
        try {
            p = passengerService.addUser(passengerDTO);
        } catch (EmailAlreadyInUseException e) {
            return ResponseEntity.status(HttpStatus.OK).body("The supplied email is already in use");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.OK).body("Failed to reach auth server,try again later");
        }
        return new ResponseEntity<>(p, HttpStatus.OK);
    }

}
