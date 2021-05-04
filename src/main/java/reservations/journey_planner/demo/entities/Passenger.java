package reservations.journey_planner.demo.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "PASSENGER", schema = "JOURNEY_PLANNER")
public class Passenger {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID")
    private int id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SECOND_NAME")
    private String second_name;

    @Column(name = "SSN")
    private String SSN;

    @Column(name = "TOTAL_DISTANCE_TRAVELLED")
    private long distance_travelled;

    @OneToMany(mappedBy = "passenger")
    private List<Reservation> reservations;

}
