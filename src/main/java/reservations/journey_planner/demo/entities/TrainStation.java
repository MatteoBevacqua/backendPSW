package reservations.journey_planner.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Data
@Entity
@Table(name = "TRAIN_STATION", schema = "journey_planner")
public class TrainStation {
    @JsonIgnore
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    @Column(name = "name")
    private String name;

    @ManyToOne
    @JoinColumn(name = "CITY")
    private City city;

    @JsonIgnore
    @OneToMany(mappedBy = "arrivalStation")
    private List<Route> routes;

    @JsonIgnore
    @OneToMany(mappedBy = "departureStation")
    private List<Route> departure;

}
