package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import reservations.journey_planner.demo.entities.Route;

public interface RouteRepository extends JpaRepository<Route,Integer> {
}
