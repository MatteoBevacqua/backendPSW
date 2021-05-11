package reservations.journey_planner.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Data
@Entity
@Table(name = "CITY", schema = "JOURNEY_PLANNER")
public class City implements Serializable {

    private final static long serialVersionUID=1;


    @Id
    @Column(name = "NAME")
    private String name;

    @Column(name = "COUNTRY")
    private String country;



    @ToString.Exclude
    @OneToMany(mappedBy = "city")
    @JsonIgnore
    private transient List<TrainStation> trainStations;
}
