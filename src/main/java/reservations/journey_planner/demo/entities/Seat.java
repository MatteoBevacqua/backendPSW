package reservations.journey_planner.demo.entities;

import javax.persistence.*;

enum SeatType {BUSINESS,ECONOMY,FIRST};
@Entity
@Table(name="SEAT",schema = "JOURNEY_PLANNER")
public class Seat {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name="id")
    private int id;

    @Column(name="WAGON_NUMBER")
    private int wagon_number;

    @Column(name="CLASS",columnDefinition = "ENUM('ECONOMY','BUSINESS','FIRST')")
    private SeatType seatClass;
}
