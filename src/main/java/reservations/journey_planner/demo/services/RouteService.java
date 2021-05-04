package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.repositories.RouteRepository;

import java.util.List;

@Service
public class RouteService {
    @Autowired
    private RouteRepository routeRepository;

    public List<Route> findAll() {
        return routeRepository.findAll();
    }

    public List<Route> findByCityBothDepartureAndArrival(String cityName){
        try{
            return routeRepository.findAllByArrivalStation_City_NameOrDepartureStation_City_Name(cityName,cityName);
        } catch (Exception e){
            return null;
        }
    }

    public List<Route> findByDepartureCity(String cityName){
        try{
            return routeRepository.findAllByDepartureStation_City_Name(cityName);
        } catch (Exception e){
            return null;
        }
    }

    public List<Route> findByArrivalCity(String cityName){
        try{
            return routeRepository.findAllByArrivalStation_City_Name(cityName);
        } catch (Exception e){
            return null;
        }
    }
}
