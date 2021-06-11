package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reservations.journey_planner.demo.entities.City;
import reservations.journey_planner.demo.repositories.CityRepository;

import java.util.List;

@Service
public class CityService {
    @Autowired
    private CityRepository cityRepository;

    public List<City> findAll() {
        return cityRepository.findAll();
    }

    public List<City> getByNameLike(String name) {
        return cityRepository.getAllByNameContains(name);
    }

    public City findByName(String name) {
        return cityRepository.findByName(name);
    }
}
