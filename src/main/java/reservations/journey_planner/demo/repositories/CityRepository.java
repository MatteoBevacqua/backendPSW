package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import reservations.journey_planner.demo.entities.City;

public interface CityRepository extends JpaRepository<City, String> {
}
