package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reservations.journey_planner.demo.entities.SeatsAndReservation;

import java.util.List;

@Repository
public interface SeatInReservationRepository extends JpaRepository<SeatsAndReservation,Integer> {

    List<SeatsAndReservation> findAllByRoute_IdAndSeatIdIn(Integer routeId,List<Integer> seatIds);
     SeatsAndReservation findBySeat_IdAndReservation_Id(Integer s,Integer r);


}
