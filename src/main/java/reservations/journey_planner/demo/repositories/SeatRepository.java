package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import reservations.journey_planner.demo.entities.Seat;

public interface SeatRepository extends JpaRepository<Seat,Integer> {
}
