package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reservations.journey_planner.demo.entities.Train;
import java.util.List;

@Repository
public interface TrainRepository extends JpaRepository<Train, Integer> {

    public List<Train> findAllByType(String type);


}
