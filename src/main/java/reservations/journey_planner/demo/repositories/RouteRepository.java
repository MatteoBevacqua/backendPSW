package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reservations.journey_planner.demo.entities.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route,Integer> {
}
