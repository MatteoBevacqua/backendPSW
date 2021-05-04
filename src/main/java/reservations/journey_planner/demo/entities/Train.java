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
