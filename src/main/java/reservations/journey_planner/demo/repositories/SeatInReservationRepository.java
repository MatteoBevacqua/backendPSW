package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reservations.journey_planner.demo.entities.SeatsAndReservation;

import java.util.List;

@Repository
public interface SeatInReservationRepository extends JpaRepository<SeatsAndReservation,Integer> {

     List<SeatsAndReservation>  findAllBySeat_IdInAndRoute_IdAndReservationIsNull(List<Integer> seatIds, Integer routeId);


    List<SeatsAndReservation> findAllByRoute_IdAndReservation_Id(Integer routeId, Integer resId);
}
