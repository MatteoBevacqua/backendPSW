package reservations.journey_planner.demo.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

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
    private String secondName;

    @Column(name="email")
    private String email;

    @Column(name = "TOTAL_DISTANCE_TRAVELLED")
    private long distanceTravelled;

    @Column(name="TELEPHONE_NUMBER")
    private String telephoneNumber;

    @JsonIgnore
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OneToMany(mappedBy = "passenger")
    private List<Reservation> reservations;

}
