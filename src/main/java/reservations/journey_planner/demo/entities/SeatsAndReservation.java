package reservations.journey_planner.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sun.istack.Nullable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Objects;

@ToString
@Getter
@Setter
@Entity
@Table(name = "SEATS_PER_RESERVATION")
public class SeatsAndReservation {
    @JsonIgnore
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "RESERVATION_ID")
    private Reservation reservation;


    @ManyToOne
    @JoinColumn(name = "SEAT_ID")
    private Seat seat;

    @ToString.Exclude
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "ROUTE_ID")
    private Route route;

    @Column(name="PRICE_PAID")
    private int pricePaid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SeatsAndReservation that = (SeatsAndReservation) o;
        return this.seat.equals(that.seat);
    }

    @Override
    public int hashCode() {
        return this.seat.getId();
    }
}
