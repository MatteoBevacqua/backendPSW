package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import reservations.journey_planner.demo.entities.Route;


import java.util.Date;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {
    @Query(value = "SELECT R.* FROM SEATS_PER_RESERVATION S,ACTIVE_ROUTES R,TRAIN_STATION T WHERE S.ROUTE_ID=R.ROUTE_ID AND R.SOURCE_STATION=T.ID AND T.CITY=?1 GROUP BY S.ROUTE_ID  HAVING COUNT(S.SEAT_ID)>?2", nativeQuery = true)
    List<Route> findByDepartureStationNameAndXSeats(String cityName, int seatsLeft);

    @Query(value = "SELECT R.* FROM SEATS_PER_RESERVATION S,ACTIVE_ROUTES R,TRAIN_STATION T WHERE S.ROUTE_ID=R.ROUTE_ID AND R.DESTINATION_STATION=T.ID AND T.CITY=?1 GROUP BY S.ROUTE_ID  HAVING COUNT(S.SEAT_ID)>?2", nativeQuery = true)
    List<Route> findByArrivalStationNameAndXSeats(String cityName, int seatsLeft);

    @Query(value = "SELECT R.* FROM SEATS_PER_RESERVATION S,ACTIVE_ROUTES R,TRAIN_STATION T WHERE S.ROUTE_ID=R.ROUTE_ID AND R.DEPARTURE_TIME <=?3 AND R.ARRIVAL_TIME >=?4 AND  R.DESTINATION_STATION=T.ID AND T.CITY=?1 GROUP BY S.ROUTE_ID  HAVING COUNT(S.SEAT_ID)>?2", nativeQuery = true)
    List<Route> findByArrivalStationNameAndXSeatsInTimePeriod(String name,Integer seats,Date low,Date up);

    @Query(value = "SELECT R.* FROM SEATS_PER_RESERVATION S,ACTIVE_ROUTES R,TRAIN_STATION T WHERE S.ROUTE_ID=R.ROUTE_ID AND R.DEPARTURE_TIME <=?3  AND  R.DESTINATION_STATION=T.ID AND T.CITY=?1 GROUP BY S.ROUTE_ID  HAVING COUNT(S.SEAT_ID)>?2", nativeQuery = true)
    List<Route> findByArrivalStationNameAndXSeatsBefore(String name,Integer seats,Date before);

    @Query(value = "SELECT R.* FROM SEATS_PER_RESERVATION S,ACTIVE_ROUTES R,TRAIN_STATION T WHERE S.ROUTE_ID=R.ROUTE_ID AND R.DEPARTURE_TIME >=?3 AND  R.DESTINATION_STATION=T.ID AND T.CITY=?1 GROUP BY S.ROUTE_ID  HAVING COUNT(S.SEAT_ID)>?2", nativeQuery = true)
    List<Route> findByArrivalStationNameAndXSeatsAfter(String name,Integer seats,Date after);


    List<Route> findAllByArrivalStation_City_NameOrDepartureStation_City_Name(String arrivalCityName, String departureCityName);

    List<Route> findAllByDepartureStation_City_Name(String city);
    List<Route> findAllByDepartureStation_City_NameAndDepartureTimeBefore(String city,Date end);
    List<Route> findAllByDepartureStation_City_NameAndDepartureTimeAfter(String city,Date start);
    List<Route> findAllByDepartureStation_City_NameAndDepartureTimeBetween(String city,Date start,Date end);
    List<Route> findRouteByDepartureStation_City_NameAndArrivalStation_City_NameAndDepartureTimeAfter(String dep, String arr, Date start);

    List<Route> findRouteByDepartureStation_City_NameAndArrivalStation_City_NameAndDepartureTimeBefore(String dep, String arr, Date end);




    List<Route> findAllByArrivalStation_City_Name(String city);
    List<Route> findAllByArrivalStation_City_NameAndDepartureTimeBefore(String city,Date end);
    List<Route> findAllByArrivalStation_City_NameAndDepartureTimeAfter(String city,Date start);
    List<Route> findAllByArrivalStation_CityAndDepartureTimeAfterAndDepartureTimeBefore(String city,Date after,Date before);

    Route findRouteById(Integer id);

    List<Route> findRouteByDepartureStation_City_NameAndArrivalStation_City_Name(String depCity, String arrCity);

    List<Route> findAllByDepartureTimeBefore(Date end);
    List<Route> findAllByDepartureTimeAfter(Date start);
}
