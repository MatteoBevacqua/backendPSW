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

    public Train(){}

    public int getTrain_id() {
        return train_id;
    }

    public void setTrain_id(int train_id) {
        this.train_id = train_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats;
    }

    @Override
    public String toString() {
        return "Train{" +
                "train_id=" + train_id +
                ", type='" + type + '\'' +
                ", seats=" + seats +
                '}';
    }
}
