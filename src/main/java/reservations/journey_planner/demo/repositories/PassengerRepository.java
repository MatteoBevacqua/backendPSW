package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import reservations.journey_planner.demo.entities.Passenger;

public interface PassengerRepository extends JpaRepository<Passenger,Integer> {
}
