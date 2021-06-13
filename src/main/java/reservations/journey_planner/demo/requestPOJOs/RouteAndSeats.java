package reservations.journey_planner.demo.requestPOJOs;

import lombok.Data;
import lombok.ToString;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.entities.Seat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@ToString
@Data
public class RouteAndSeats {
    @NotNull
    Route route;
    @NotNull
    @NotEmpty
    List<Seat> seats;


}
