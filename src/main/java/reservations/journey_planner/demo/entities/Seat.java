package reservations.journey_planner.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

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
    private int wagon_number;

    @Column(name = "CLASS", columnDefinition = "ENUM('ECONOMY','BUSINESS','FIRST')")
    @Enumerated(EnumType.STRING)
    private SeatClass seatClass;

    @Column(name = "ADULT_PRICE")
    private int adult_price;

    @Column(name = "CHILDREN_PRICE")
    private int children_price;

    @Column(name = "FACING_DIRECTION", columnDefinition = "ENUM('FACING_DIRECTION','OPPOSITE')")
    @Enumerated(EnumType.STRING)
    private FacingDirection direction;

    @ManyToOne
    @JoinColumn(name = "TRAIN_ID")
    @JsonIgnore
    private Train train;


    @ManyToMany(mappedBy = "seats")
    @JsonIgnore
    private List<Reservation> reservations = new ArrayList<>();
}
