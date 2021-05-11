package reservations.journey_planner.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Data
@Entity
@Table(name = "ACTIVE_ROUTES")
public class Route {
    @Id
    @Column(name="route_id")
    private int id;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name="TRAIN_ID")
    private Train train;

    @ManyToOne
    @JoinColumn(name="SOURCE_STATION")
    private TrainStation departureStation;

    @ManyToOne
    @JoinColumn(name="DESTINATION_STATION")
    private TrainStation arrivalStation;

    @JsonIgnore
    @OneToMany(mappedBy = "route")
    private List<SeatsAndReservation> bookableSeats;

    @Column(name="departure_time")
    private Date departureTime;


    @Column(name="arrival_time")
    private Date arrivalTime;

    @Column(name="route_length")
    private int routeLength;


}
