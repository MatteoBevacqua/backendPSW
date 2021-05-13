package reservations.journey_planner.demo.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reservations.journey_planner.demo.entities.City;

import java.util.List;

@Repository
public interface CityRepository extends JpaRepository<City, String> {

    List<City> findAllByName(String name);
    City findByName(String name);
}
