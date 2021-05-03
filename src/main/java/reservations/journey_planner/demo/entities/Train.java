package reservations.journey_planner.demo.entities;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TRAIN", schema = "JOURNEY_PLANNER")
public class Train {
    @Id
    @Column(name = "TRAIN_ID")
    private int train_id;

    @Column(name = "number_of_seats")
    private int number_of_seats;

    @Column(name = "type")
    private String type;
}
