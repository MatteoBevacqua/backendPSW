package reservations.journey_planner.demo.entities;

import javax.persistence.*;

@Entity
@Table(name = "TRAIN_STATION", schema = "journey_planner")
public class TrainStation {
    @Id
    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "services")
    private String services;


    @ManyToOne
    @JoinColumn(name = "CITY")
    private City city;
}
