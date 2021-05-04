package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import reservations.journey_planner.demo.entities.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Integer> {
}
