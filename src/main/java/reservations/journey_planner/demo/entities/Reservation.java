package reservations.journey_planner.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.sql.Timestamp;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Data
@Entity
@ToString
@Table(name = "RESERVATION", schema = "JOURNEY_PLANNER")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Passenger passenger;

    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @ManyToOne
    @JoinColumn(name = "ROUTE")
    private Route bookedRoute;

    @Column(name = "reservation_booking_date",updatable = false)
    @CreationTimestamp
    private Timestamp reservation_booking_date;


    @Cascade(org.hibernate.annotations.CascadeType.MERGE)
    @OneToMany(targetEntity = SeatsInReservation.class,mappedBy = "reservation")
    private List<SeatsInReservation> reserved_seats = new ArrayList<>();


    @JsonIgnore
    @Transient
    List<Seat> seatsBooked;
}
