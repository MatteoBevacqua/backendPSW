package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import reservations.journey_planner.demo.entities.Train;

public interface TrainRepository extends JpaRepository<Train, Integer> {
}
