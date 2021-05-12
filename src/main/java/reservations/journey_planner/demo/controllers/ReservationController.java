package reservations.journey_planner.demo.controllers;


import com.mysql.cj.jdbc.exceptions.MySQLTransactionRollbackException;
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
import reservations.journey_planner.demo.exceptions.NoSuchReservationException;
import reservations.journey_planner.demo.exceptions.ReservationAlreadyExists;
import reservations.journey_planner.demo.exceptions.SeatsAlreadyBookedException;
import reservations.journey_planner.demo.requestPOJOs.ModifiedBookingDTO;
import reservations.journey_planner.demo.requestPOJOs.RouteAndSeats;
import reservations.journey_planner.demo.services.ReservationService;
import reservations.journey_planner.demo.services.SeatService;


import java.util.List;

@CrossOrigin(origins = "*", allowedHeaders = "*", exposedHeaders = "If-Match")
@RestController
@PreAuthorize("hasAuthority('passenger')")
@RequestMapping("/reservations")
public class ReservationController {
    @Autowired
    ReservationService reservationService;
    @Autowired
    SeatService seatService;

    @GetMapping("/all")
    public ResponseEntity<List<Reservation>> getAll() {
        Jwt jwt =(Jwt) Utils.getPrincipal();
        Passenger p = Utils.getPassengerFromToken(jwt);
        return new ResponseEntity<>(reservationService.getReservationsByPassenger(p), HttpStatus.OK);
    }

    @PutMapping
    public ResponseEntity modifyReservation(@RequestBody ModifiedBookingDTO mod){
        Passenger p = Utils.getPassengerFromToken(Utils.getPrincipal());
        try {
            return new ResponseEntity<>(reservationService.modifyReservation(p, mod), HttpStatus.OK);
        }catch (SeatsAlreadyBookedException e){
            return new ResponseEntity(e.getAvailableSeatsLeft(), HttpStatus.OK);
        }
    }

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
            System.out.println("already exists");
            res = reservationService.getByPassengerIdAndRoute(jwt.getSubject(), r.getRoute().getId());
            return new ResponseEntity<>(res, HttpStatus.NOT_ACCEPTABLE);
        } catch (SeatsAlreadyBookedException e) {
            System.out.println("Seats already booked");
            e.getAvailableSeatsLeft().stream().map(s -> s.getId()).forEach(System.out::println);
            return new ResponseEntity<List<Seat>>(e.getAvailableSeatsLeft(), HttpStatus.OK);
        }  /*  catch (Exception e) {

            System.out.println("Transation rolled back");
            return ResponseEntity.status(HttpStatus.OK).body("Some of the seats you were trying to book were already taken");
        }*/
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @DeleteMapping
    public ResponseEntity<String> delete(@RequestBody Reservation r) {
        Passenger richiedente = Utils.getPassengerFromToken(Utils.getPrincipal());
        try {
            reservationService.deleteReservation(r, richiedente);
        } catch (NoSuchReservationException e) {
            return new ResponseEntity<>("No such reservation found",HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }


}

