package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;
import reservations.journey_planner.demo.entities.Route;


import javax.persistence.LockModeType;
import javax.persistence.QueryHint;
import java.util.Date;
import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {
    @Query(value = "SELECT * FROM ACTIVE_ROUTES AS R  WHERE  DATE_FORMAT(R.DEPARTURE_TIME, '%Y-%m-%d')=DATE_FORMAT(?1,'%Y-%m-%d')",nativeQuery = true)
    List<Route> findAllByDepartureTimeSameDay(Date day);

    @Query(value = "SELECT * FROM ACTIVE_ROUTES AS R  WHERE R.SEATS_LEFT>=?2 AND  DATE_FORMAT(R.DEPARTURE_TIME, '%Y-%m-%d')=DATE_FORMAT(?1,'%Y-%m-%d')",nativeQuery = true)
    List<Route> findAllByDepartureTimeSameDayAndXSeatsLeft(Date day,int seats);


    List<Route> findByDepartureStation_City_NameAndSeatsLeftIsGreaterThanEqual(String cityName, int seatsLeft);

    List<Route> findByArrivalStation_City_NameAndSeatsLeftIsGreaterThanEqual(String cityName, int seatsLeft);

    List<Route> findByArrivalStation_City_NameAndSeatsLeftIsGreaterThanEqualAndDepartureTimeIsBetween(String name,Integer seats,Date low,Date up);

    List<Route> findByArrivalStation_City_NameAndSeatsLeftIsGreaterThanEqualAndDepartureTimeAfter(String name,Integer seats,Date before);

    List<Route> findByArrivalStation_City_NameAndSeatsLeftIsGreaterThanEqualAndDepartureTimeBefore(String name,Integer seats,Date after);


    List<Route> findAllByArrivalStation_City_NameOrDepartureStation_City_Name(String arrivalCityName, String departureCityName);

    List<Route> findAllByDepartureStation_City_Name(String city);
    List<Route> findAllByDepartureStation_City_NameAndDepartureTimeBefore(String city,Date end);
    List<Route> findAllByDepartureStation_City_NameAndDepartureTimeAfter(String city,Date start);
    List<Route> findAllByDepartureStation_City_NameAndDepartureTimeBetween(String city,Date start,Date end);
    List<Route> findRouteByDepartureStation_City_NameAndArrivalStation_City_NameAndDepartureTimeAfter(String dep, String arr, Date start);
    List<Route> findRouteByDepartureStation_City_NameAndArrivalStation_City_NameAndDepartureTimeAfterAndSeatsLeftGreaterThanEqual(String dep, String arr, Date start,int seatsLeft);
    List<Route> findRouteByDepartureStation_City_NameAndArrivalStation_City_NameAndDepartureTimeBefore(String dep, String arr, Date end);
    List<Route> findAllByArrivalStation_City_Name(String city);
    List<Route> findAllByArrivalStation_City_NameAndDepartureTimeBefore(String city,Date end);
    List<Route> findAllByArrivalStation_City_NameAndDepartureTimeAfter(String city,Date start);
    List<Route> findAllByArrivalStation_City_NameAndDepartureTimeAfterAndDepartureTimeBefore(String city,Date after,Date before);
    Route findRouteById(Integer id);
    List<Route> findRouteByDepartureStation_City_NameAndArrivalStation_City_Name(String depCity, String arrCity);
    List<Route> findRouteByDepartureStation_City_NameAndArrivalStation_City_NameAndSeatsLeftGreaterThanEqual(String depCity, String arrCity,int seatsLeft);




}
