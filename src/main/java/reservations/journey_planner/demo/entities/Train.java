package reservations.journey_planner.demo.entities;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;


@Data
@Entity
@Table(name = "TRAIN", schema = "JOURNEY_PLANNER")
public class Train {
    @Id
    @ToString.Exclude
    @Column(name = "TRAIN_ID")
    private int id;


    @Column(name = "type")
    private String type;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "train")
    private List<Seat> seats;

    @Override
    public String toString() {
        return "Train type= " + type;
    }
}
