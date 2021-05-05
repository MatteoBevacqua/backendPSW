package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reservations.journey_planner.demo.entities.Passenger;
import reservations.journey_planner.demo.entities.Reservation;
import reservations.journey_planner.demo.entities.Route;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Integer> {

    List<Reservation> findAllByPassenger(Passenger p);

    boolean existsReservationsByPassenger_IdAndBookedRoute(String id, Route r);

    List<Reservation> findAllReservationsByPassenger_IdAndBookedRoute(String id, Route r);

    List<Reservation> findAllByBookedRoute(Route r);
}
