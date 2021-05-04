package reservations.journey_planner.demo.entities;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "TRAIN", schema = "JOURNEY_PLANNER")
public class Train {
    @Id
    @Column(name = "TRAIN_ID")
    private int train_id;


    @Column(name = "type")
    private String type;

    @OneToMany(mappedBy = "train")
    private List<Seat> seats;
}
