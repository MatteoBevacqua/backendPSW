package reservations.journey_planner.demo.requestPOJOs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.entities.Seat;

import java.util.List;

@ToString
@Setter
@Getter
public class RouteAndSeats {
    Route route;
    List<Seat> seats;

    public RouteAndSeats() {

    }
}
