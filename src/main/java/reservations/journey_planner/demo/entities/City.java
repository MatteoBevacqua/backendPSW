package reservations.journey_planner.demo.entities;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "City", schema = "JOURNEY_PLANNER")
public class City {

    @Id
    @Column(name = "NAME")
    private String name;

    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "POSTAL_CODE")
    private int zip_code;

    @OneToMany(mappedBy = "city")
    private List<TrainStation> trainStations;
}
