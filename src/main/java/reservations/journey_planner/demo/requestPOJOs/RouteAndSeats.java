package reservations.journey_planner.demo.requestPOJOs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.entities.Seat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@ToString
@Setter
@Getter
public class RouteAndSeats {
    @NotNull
    Route route;
    @NotNull
    @NotEmpty
    List<Seat> seats;

    public RouteAndSeats() {

    }
}
