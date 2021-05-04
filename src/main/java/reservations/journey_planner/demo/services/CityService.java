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

    public List<City> findByName(String name) {
        try {
            return cityRepository.findAllByName(name);
        } catch (Exception e) {
            return null;
        }
    }
}
