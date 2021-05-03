package reservations.journey_planner.demo.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="City",schema = "JOURNEY_PLANNER")
public class City {

    @Id
    @Column(name="NAME")
    private String name;

    @Column(name="COUNTRY")
    private String country;

    @Column(name="POSTAL_CODE")
    private int zip_code;

}
