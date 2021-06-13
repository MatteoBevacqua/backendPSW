package reservations.journey_planner.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "ACTIVE_ROUTES")
public class Route {
    @Id
    @Column(name="route_id")
    private int id;

    @ToString.Exclude
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

    @Column(name="SEATS_LEFT")
    private int seatsLeft;

    @Override
    public String toString() {
        return "Route id : #" + id +"\n"+
                 train.toString() +"\n"+
                "departureStation :" + departureStation.getName() +"\n"+
                "arrivalStation :" + arrivalStation.getName() +"\n"+
                "departureTime :" + departureTime +"\n"+
                "arrivalTime :" + arrivalTime +"\n"+
                "routeLength :" + routeLength;
    }
}
