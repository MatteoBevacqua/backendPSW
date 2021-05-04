package reservations.journey_planner.demo.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

enum FacingDirection {TRAVEL_DIRECTION, OPPOSITE};

enum SeatType {BUSINESS, ECONOMY, FIRST};

@Getter
@Setter
@Data
@Entity
@Table(name = "SEAT", schema = "JOURNEY_PLANNER")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private int id;

    @Column(name = "WAGON_NUMBER")
    private int wagon_number;

    @Column(name = "CLASS", columnDefinition = "ENUM('ECONOMY','BUSINESS','FIRST')")
    private SeatType seatClass;

    @Column(name = "ADULT_PRICE")
    private int adult_price;

    @Column(name = "CHILDREN_PRICE")
    private int children_price;

    @Column(name = "FACING_DIRECTION", columnDefinition = "ENUM('FACING_DIRECTION','OPPOSITE')")
    private FacingDirection direction;

    @ManyToOne
    @JoinColumn(name = "TRAIN_ID")
    private Train train;

    @ManyToMany(mappedBy = "seats")
    private List<Reservation> reservations;
}
