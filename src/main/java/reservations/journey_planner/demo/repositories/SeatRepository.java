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

    @Query(value = "SELECT SEAT.* FROM SEAT,ACTIVE_ROUTES R WHERE R.ROUTE_ID = ?1 AND SEAT.TRAIN_ID = R.TRAIN_ID  AND SEAT.ID NOT IN (SELECT SEAT_ID FROM SEATS_PER_RESERVATION WHERE ROUTE_ID=?1)", nativeQuery = true)
    List<Seat> findSeatsNative(Integer route);

    @Query(value = "SELECT SEAT.* FROM SEAT,ACTIVE_ROUTES R WHERE R.ROUTE_ID = ?1 AND SEAT.TRAIN_ID = R.TRAIN_ID  AND SEAT.ID  IN (SELECT SEAT_ID FROM SEATS_PER_RESERVATION WHERE ROUTE_ID=?1)", nativeQuery = true)
    List<Seat> findNotAvailable(Integer routeID);

    List<Seat> findByIdIn(List<Integer> ids);


}
