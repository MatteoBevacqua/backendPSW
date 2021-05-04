package reservations.journey_planner.demo.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Data
@Entity
@Table(name = "PASSENGER", schema = "JOURNEY_PLANNER")
public class Passenger {
    @Id
    @Column(name = "ID")
    private String id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "SECOND_NAME")
    private String second_name;

    @Column(name="email")
    private String email;

    @Column(name = "TOTAL_DISTANCE_TRAVELLED")
    private long distance_travelled;

    @OneToMany(mappedBy = "passenger")
    private List<Reservation> reservations;

}
