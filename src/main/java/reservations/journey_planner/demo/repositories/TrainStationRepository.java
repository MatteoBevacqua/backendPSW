package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import reservations.journey_planner.demo.entities.TrainStation;

public interface TrainStationRepository extends JpaRepository<TrainStation, Integer> {
}
