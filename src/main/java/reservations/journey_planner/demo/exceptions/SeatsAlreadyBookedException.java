package reservations.journey_planner.demo.exceptions;

import lombok.Getter;
import reservations.journey_planner.demo.entities.Seat;

import java.util.List;


public class SeatsAlreadyBookedException extends RuntimeException {
    @Getter

    List<Seat> availableSeatsLeft;

    public SeatsAlreadyBookedException(List<Seat> availableSeatsLeft) {
        this.availableSeatsLeft = availableSeatsLeft;
    }

    public SeatsAlreadyBookedException(List<Seat> availableSeatsLeft,String msg) {
        super(msg);
        this.availableSeatsLeft = availableSeatsLeft;

    }
}
