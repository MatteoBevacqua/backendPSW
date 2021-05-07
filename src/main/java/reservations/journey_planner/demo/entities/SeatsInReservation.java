package reservations.journey_planner.demo.entities;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@ToString
@Getter
@Setter
@Entity
@Table(name = "SEATS_PER_RESERVATION")
public class SeatsInReservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @ManyToOne
    @JoinColumn(name = "RESERVATION_ID")
    private Reservation reservation;


    @ManyToOne
    @JoinColumn(name = "SEAT_ID")
    private Seat seat;

    @ToString.Exclude
    @ManyToOne
    @JoinColumn(name = "ROUTE_ID")
    private Route route;

}
