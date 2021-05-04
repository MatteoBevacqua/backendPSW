package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reservations.journey_planner.demo.entities.TrainStation;

@Repository
public interface TrainStationRepository extends JpaRepository<TrainStation, Integer> {
}
