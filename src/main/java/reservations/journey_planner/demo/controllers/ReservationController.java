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
import reservations.journey_planner.demo.exceptions.NoSuchPassengerException;
import reservations.journey_planner.demo.exceptions.NoSuchReservationException;
import reservations.journey_planner.demo.exceptions.ReservationAlreadyExists;
import reservations.journey_planner.demo.exceptions.SeatsAlreadyBookedException;
import reservations.journey_planner.demo.requestPOJOs.ModifiedBookingDTO;
import reservations.journey_planner.demo.requestPOJOs.RouteAndSeats;
import reservations.journey_planner.demo.services.ReservationService;
import reservations.journey_planner.demo.services.SeatService;

import javax.validation.Valid;
import java.util.List;

@SuppressWarnings({"all"})
@RestController
@PreAuthorize("hasAuthority('passenger')")
@RequestMapping("/reservations")
public class ReservationController {
    @Autowired
    ReservationService reservationService;
    @Autowired
    SeatService seatService;


    @GetMapping("/all")
    public ResponseEntity getAll() {
        Jwt jwt = (Jwt) Utils.getPrincipal();
        Passenger p = Utils.getPassengerFromToken(jwt);
        List<Reservation> reservations = reservationService.getReservationsByPassenger(p);
        reservations.forEach(r -> r.getReservedSeats().forEach(resSeat -> resSeat.getSeat().setBooked(true)));
        return new ResponseEntity<>(reservations, HttpStatus.OK);

    }

    @PutMapping
    public ResponseEntity modifyReservation(@RequestBody @Valid ModifiedBookingDTO mod) {
        Passenger p = Utils.getPassengerFromToken(Utils.getPrincipal());
        try {
            return new ResponseEntity<>(reservationService.modifyReservation(p, mod), HttpStatus.OK);
        } catch (SeatsAlreadyBookedException e) {
            return new ResponseEntity(HttpStatus.CONFLICT);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Non existent seat specified");
        }
    }

    @PostMapping
    public ResponseEntity addReservation(@RequestBody @Valid RouteAndSeats r) {
        Jwt jwt = Utils.getPrincipal();
        Passenger p = Utils.getPassengerFromToken(jwt);
        Reservation res = null;
        Route toBook = r.getRoute();
        List<Seat> seatsToBook = r.getSeats();
        try {
            res = reservationService.addNewReservation(p, toBook, seatsToBook);
        } catch (ReservationAlreadyExists e) {
            //406
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        } catch (SeatsAlreadyBookedException e) {
            //409
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (NoSuchPassengerException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No such passenger");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Some of the seats you were trying to book were already taken");
        }
        return new ResponseEntity<>(res, HttpStatus.OK);
    }


    @DeleteMapping
    public ResponseEntity<String> delete(@RequestParam(name = "id") Integer id) {
        Passenger richiedente = Utils.getPassengerFromToken(Utils.getPrincipal());
        try {
            reservationService.deleteReservation(id, richiedente);
        } catch (NoSuchReservationException e) {
            return new ResponseEntity<>("No such reservation found", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Deleted", HttpStatus.OK);
    }


}

