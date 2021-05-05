package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import reservations.journey_planner.demo.entities.Seat;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat,Integer> {

    @Query(value = "SELECT * FROM SEAT WHERE ID NOT IN ( SELECT SEAT_ID FROM SEATS_PER_RESERVATION WHERE RESERVATION_ID NOT IN ( SELECT ID FROM RESERVATION WHERE RESERVATION.ROUTE=':route_id'))", nativeQuery = true)
    List<Seat> findSeatsNative(@Param("route_id") Integer route);
}
