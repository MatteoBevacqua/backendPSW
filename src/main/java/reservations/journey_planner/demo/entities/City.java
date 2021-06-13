package reservations.journey_planner.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.List;


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

    @JsonIgnore
    @Column(name="LATITUDE")
    private float latitude;

    @JsonIgnore
    @Column(name="LONGITUDE")
    private float longitude;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "city")
    private List<TrainStation> trainStations;

    public Point2D toPoint(){
       return new Point2D.Double(latitude,longitude);
    }
}
