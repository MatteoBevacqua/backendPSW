package reservations.journey_planner.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;


@Data
@Entity
@Table(name = "TRAIN_STATION", schema = "journey_planner")
public class TrainStation {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;


    @ManyToOne
    @JoinColumn(name = "CITY")
    private City city;


    @ToString.Exclude
    @OneToMany(mappedBy = "arrivalStation")
    @JsonIgnore
    private List<Route> routes;

    @ToString.Exclude
    @OneToMany(mappedBy = "departureStation")
    @JsonIgnore
    private List<Route> departure;

    public int hashCode() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TrainStation that = (TrainStation) o;
        return id == that.id;
    }
}
