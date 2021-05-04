package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reservations.journey_planner.demo.entities.Route;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Integer> {

    List<Route> findAllByArrivalStation_City_NameOrDepartureStation_City_Name(String arrivalCityName,String departureCityName);

    List<Route> findAllByDepartureStation_City_Name(String city);
    List<Route> findAllByArrivalStation_City_Name(String city);

}
