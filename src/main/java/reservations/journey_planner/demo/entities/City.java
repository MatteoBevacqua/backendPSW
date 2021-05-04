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
@Table(name = "CITY", schema = "JOURNEY_PLANNER")
public class City {

    @Id
    @Column(name = "NAME")

    private String name;


    @Column(name = "COUNTRY")
    private String country;

    @Column(name = "ZIP_CODE")
    private int zip_code;

    @OneToMany(mappedBy = "city")
    private List<TrainStation> trainStations;
}
