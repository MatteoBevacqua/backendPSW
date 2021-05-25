package reservations.journey_planner.demo.requestPOJOs;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import reservations.journey_planner.demo.entities.Reservation;
import reservations.journey_planner.demo.entities.Seat;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
@ToString
public class ModifiedBookingDTO {
    @NotNull
    Reservation toModify;
    @NotNull
    List<Seat> toAdd;
    @NotNull
    List<Seat> toRemove;

}
