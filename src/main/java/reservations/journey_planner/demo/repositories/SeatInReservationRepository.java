package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reservations.journey_planner.demo.entities.SeatsInReservation;

import java.util.List;

@Repository
public interface SeatInReservationRepository extends JpaRepository<SeatsInReservation,Integer> {

    public List<SeatsInReservation>  findAllBySeat_IdInAndRoute_IdAndReservationIsNull(List<Integer> seatIds,Integer routeId);

}
