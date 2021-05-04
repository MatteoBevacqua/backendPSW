package reservations.journey_planner.demo.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;

@Getter
@Setter
@Data
@Entity
@Table(name = "ACTIVE_ROUTES")
public class Route {
    @Id
    @Column(name="route_id")
    private int route_id;


    @ManyToOne
    @JoinColumn(name="TRAIN_ID")
    private Train train;

    @ManyToOne
    @JoinColumn(name="DESTINATION_STATION")
    private TrainStation arrival;

    @ManyToOne
    @JoinColumn(name="SOURCE_STATION")
    private TrainStation departure;

    @Column(name="departure_time")
    private Date departure_time;

    @Column(name="arrival_time")
    private Date arrival_time;

}
