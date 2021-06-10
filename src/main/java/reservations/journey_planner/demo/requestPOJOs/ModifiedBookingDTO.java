package reservations.journey_planner.demo.requestPOJOs;

import lombok.Getter;
import lombok.Setter;
import reservations.journey_planner.demo.entities.Reservation;
import reservations.journey_planner.demo.entities.Seat;

import javax.validation.constraints.NotNull;
import java.util.List;

@Getter
@Setter
public class ModifiedBookingDTO {
    @NotNull
    Reservation toModify;
    @NotNull
    List<Seat> toAdd,toRemove,changePrice;

    @Override
    public String toString() {
        return "ModifiedBookingDTO{" +
                "toModify=" + toModify.getId() +
                ", toAdd=" + toAdd +
                ", toRemove=" + toRemove +
                ", changePrice=" + changePrice +
                '}';
    }
}
