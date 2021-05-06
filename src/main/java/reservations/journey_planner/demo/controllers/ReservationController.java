package reservations.journey_planner.demo.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import reservations.journey_planner.demo.configuration.Utils;
import reservations.journey_planner.demo.entities.Passenger;
import reservations.journey_planner.demo.entities.Reservation;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.entities.Seat;
import reservations.journey_planner.demo.exceptions.ReservationAlreadyExists;
import reservations.journey_planner.demo.exceptions.SeatsAlreadyBookedException;
import reservations.journey_planner.demo.requestPOJOs.RouteAndSeats;
import reservations.journey_planner.demo.services.ReservationService;
import reservations.journey_planner.demo.services.SeatService;


import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    @Autowired
    ReservationService reservationService;
    @Autowired
    SeatService seatService;

    @GetMapping("/all")
    @PreAuthorize("hasAuthority('passenger')")
    public ResponseEntity<List<Reservation>> getAll() {
        Jwt jwt = Utils.getPrincipal();
        Passenger p = Utils.getPassengerFromToken(jwt);
        return new ResponseEntity<>(reservationService.getReservationsByPassenger(p), HttpStatus.OK);
    }

    @PreAuthorize("hasAuthority('passenger')")
    @PostMapping("/new")
    public ResponseEntity addReservation(@RequestBody RouteAndSeats r) {
        Jwt jwt = Utils.getPrincipal();
        Passenger p = Utils.getPassengerFromToken(jwt);
        Reservation res = null;
        Route toBook = r.getRoute();
        List<Seat> seatsToBook = r.getSeats();
        try {
            res = reservationService.addNewReservationIfPossible(p, toBook, seatsToBook);
        } catch (ReservationAlreadyExists e) {
            System.out.println("Reservation già esistente");
            return ResponseEntity.status(HttpStatus.OK).body("You already made a reservation,change or delete the existing one");
        } catch (SeatsAlreadyBookedException e) {
            System.out.println("Seats already booked");
            return new ResponseEntity<List<Seat>>(seatService.findAvailableByRoute(r.getRoute()), HttpStatus.OK);
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    /*

    //TODO
    @PreAuthorize("hasAuthority('passenger')")
    @PostMapping("/new")
    public void modifyReservation(@RequestBody Route r) {

    }
*/
}

