package reservations.journey_planner.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Getter
@Setter
@Data
@Entity
@Table(name = "ACTIVE_ROUTES")
public class Route {
    @JsonIgnore
    @Id
    @Column(name="route_id")
    private int route_id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="TRAIN_ID")
    private Train train;

    @ManyToOne
    @JoinColumn(name="DESTINATION_STATION")
    private TrainStation arrivalStation;

    @ManyToOne
    @JoinColumn(name="SOURCE_STATION")
    private TrainStation departureStation;

    @Column(name="departure_time")
    private Date departureTime;

    @Column(name="arrival_time")
    private Date arrivalTime;

    @Column(name="route_length")
    private int routeLength;

    @Column(name="AVAILABLE_SEATS")
    private int availableSeats;

    @Version
    private long version;
}
