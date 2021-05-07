package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reservations.journey_planner.demo.entities.Seat;


import javax.persistence.NamedQuery;
import java.util.List;

import static javax.persistence.LockModeType.PESSIMISTIC_READ;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Integer> {

    @Query(value = "SELECT * FROM SEAT WHERE SEAT.ID NOT IN (SELECT DISTINCT SEAT_ID FROM SEATS_PER_RESERVATION WHERE RESERVATION_ID IN (SELECT ID FROM RESERVATION WHERE RESERVATION.ROUTE =?1))", nativeQuery = true)
    List<Seat> findSeatsNative(@Param("route_id") Integer route);

        //JPQL s member of r.seats s.id IN (:ids)  and r.bookedRoute.route_id=:route
    List<Seat> findByIdIn(List<Integer> ids);


}
