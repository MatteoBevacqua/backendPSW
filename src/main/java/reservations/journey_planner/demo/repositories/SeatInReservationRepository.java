package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reservations.journey_planner.demo.entities.SeatsAndReservation;

import java.util.HashSet;
import java.util.List;

@Repository
public interface SeatInReservationRepository extends JpaRepository<SeatsAndReservation,Integer> {

    HashSet<SeatsAndReservation> findAllByReservation_Id(Integer resId);
    List<SeatsAndReservation> findAllByRoute_IdAndSeatIdIn(Integer routeId,List<Integer> seatIds);
    List<SeatsAndReservation> findAllByRoute_IdAndReservation_Id(Integer routeId, Integer resId);
    List<SeatsAndReservation> findAllByRoute_IdAndReservation_IdAndSeat_IdIn(Integer routeId, Integer resId,List<Integer> seatIds);
    SeatsAndReservation findBySeat_IdAndReservation_Id(Integer s,Integer r);
    void deleteAllBySeat_IdInAndReservation_Id(List<Integer> ids,int resId);
}
