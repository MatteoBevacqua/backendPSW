package reservations.journey_planner.demo.requestPOJOs;

import lombok.Getter;
import lombok.Setter;
import org.jgrapht.graph.DefaultWeightedEdge;
import reservations.journey_planner.demo.entities.Route;

@Getter
@Setter
public class myGraphEdge extends DefaultWeightedEdge {
    private Route route;

}
