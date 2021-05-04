package reservations.journey_planner.demo.entities;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="RESERVATION",schema = "JOURNEY_PLANNER")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @ManyToOne
    private Passenger passenger;

    @ManyToOne
    @JoinColumn(name="ROUTE")
    private Route booked_route;

    @Column(name="reservation_booking_date")
    private Date reservation_booking_date;

    @ManyToMany
    @JoinTable(name="SEATS_PER_RESERVATION")
    private List<Seat> seats;
}
