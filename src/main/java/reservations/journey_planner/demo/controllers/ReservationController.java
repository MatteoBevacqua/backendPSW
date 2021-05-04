package reservations.journey_planner.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reservations.journey_planner.demo.configuration.Utils;
import reservations.journey_planner.demo.entities.Passenger;
import reservations.journey_planner.demo.entities.Reservation;
import reservations.journey_planner.demo.services.ReservationService;


import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    @Autowired
    ReservationService reservationService;


    @GetMapping("/all")
    @PreAuthorize("hasAuthority('passenger')")
    public ResponseEntity<List<Reservation>> getAll() {
        Jwt jwt = Utils.getPrincipal();
        String firstName = Utils.getName(jwt);
        String secondName = Utils.getSecondName(jwt);
        String id = jwt.getSubject();
        String email = Utils.getEmail(jwt);
        System.out.println(jwt.getId());
        Passenger p = new Passenger();
        p.setId(id);
        p.setName(firstName);
        p.setEmail(email);
        p.setSecond_name(secondName);
        return new ResponseEntity<>(reservationService.getReservationsByPassenger(p), HttpStatus.OK);
    }


}

