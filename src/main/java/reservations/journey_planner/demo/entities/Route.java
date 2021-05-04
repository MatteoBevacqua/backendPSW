package reservations.journey_planner.demo.entities;

import javax.persistence.*;

@Entity
@Table(name = "ACTIVE_ROUTES")
public class Route {
    @Id
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
}
