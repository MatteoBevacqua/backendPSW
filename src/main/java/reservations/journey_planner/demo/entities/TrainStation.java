package reservations.journey_planner.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;


    @ManyToOne
    @JoinColumn(name = "CITY")
    @JsonIgnore
    private City city;


    @ToString.Exclude
    @OneToMany(mappedBy = "arrivalStation")
    @JsonIgnore
    private List<Route> routes;

    @ToString.Exclude
    @OneToMany(mappedBy = "departureStation")
    @JsonIgnore
    private List<Route> departure;

}
