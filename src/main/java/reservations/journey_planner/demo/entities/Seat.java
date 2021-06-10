package reservations.journey_planner.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@Data
@Entity
@Table(name = "SEAT", schema = "JOURNEY_PLANNER")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "WAGON_NUMBER")
    private int wagonNumber;

    @Column(name = "CLASS", columnDefinition = "ENUM('ECONOMY','BUSINESS','FIRST')")
    @Enumerated(EnumType.STRING)
    private SeatClass seatClass;

    @Column(name = "ADULT_PRICE")
    private int adultPrice;

    @Column(name = "CHILDREN_PRICE")
    private int childrenPrice;

    @Column(name = "FACING_DIRECTION", columnDefinition = "ENUM('FACING_DIRECTION','OPPOSITE')")
    @Enumerated(EnumType.STRING)
    private FacingDirection direction;

    @ManyToOne
    @JoinColumn(name = "TRAIN_ID")
    @JsonIgnore
    private Train train;

    @OneToMany(mappedBy = "seat",targetEntity = SeatsAndReservation.class,cascade = CascadeType.MERGE)
    @JsonIgnore
    @ToString.Exclude
    private List<SeatsAndReservation> reservations = new ArrayList<>();

    @Transient
    @JsonProperty(value = "isBooked")
    private boolean isBooked;

    @Transient
    private int pricePaid;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        return id == seat.id;
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ",pricePaid=" + pricePaid+
                '}';
    }
}
