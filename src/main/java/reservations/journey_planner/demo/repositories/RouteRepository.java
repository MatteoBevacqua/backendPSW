package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import reservations.journey_planner.demo.entities.Route;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {
    @Query(value = "SELECT R.* FROM SEATS_PER_RESERVATION S,ACTIVE_ROUTES R,TRAIN_STATION T WHERE S.ROUTE_ID=R.ROUTE_ID AND R.SOURCE_STATION=T.ID AND T.CITY=?1 GROUP BY S.ROUTE_ID  HAVING COUNT(S.SEAT_ID)>?2", nativeQuery = true)
    List<Route> findByDepartureStationNameAndXSeats(String cityName, int seatsLeft);

    @Query(value = "SELECT R.* FROM SEATS_PER_RESERVATION S,ACTIVE_ROUTES R,TRAIN_STATION T WHERE S.ROUTE_ID=R.ROUTE_ID AND R.DESTINATION_STATION=T.ID AND T.CITY=?1 GROUP BY S.ROUTE_ID  HAVING COUNT(S.SEAT_ID)>?2", nativeQuery = true)
    List<Route> findByArrivalStationNameAndXSeats(String cityName, int seatsLeft);

    List<Route> findAllByArrivalStation_City_NameOrDepartureStation_City_Name(String arrivalCityName, String departureCityName);

    List<Route> findAllByDepartureStation_City_Name(String city);

    List<Route> findAllByArrivalStation_City_Name(String city);

    Route findRouteById(Integer id);

}
