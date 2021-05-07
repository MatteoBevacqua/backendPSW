package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reservations.journey_planner.demo.entities.Passenger;

@Repository
public interface PassengerRepository extends JpaRepository<Passenger,String> {

    Passenger findPassengerById(String id);

    boolean existsById(String id);
}
