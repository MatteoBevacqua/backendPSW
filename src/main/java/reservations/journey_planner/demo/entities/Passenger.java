package reservations.journey_planner.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;


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
    private String secondName;

    @Column(name="email")
    private String email;

    @Column(name = "TOTAL_DISTANCE_TRAVELLED")
    private long distanceTravelled;

    @Column(name="TELEPHONE_NUMBER")
    private String telephoneNumber;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "passenger",cascade = {CascadeType.REMOVE,CascadeType.PERSIST},orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

}
