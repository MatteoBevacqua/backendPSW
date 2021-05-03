package reservations.journey_planner.demo.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class City {

    @Id
    @Column(name="NAME")
    private String name;

    @Column(name="COUNTRY")
    private String country;
}
