package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.repositories.RouteRepository;

import java.util.List;

@Service
public class RouteService {
    @Autowired
    private RouteRepository routeRepository;

    @Transactional(readOnly = true)
    public List<Route> findAll() {
        return routeRepository.findAll();
    }
    @Transactional(readOnly = true)
    public List<Route> findByCityBothDepartureAndArrival(String cityName) {
        return routeRepository.findAllByArrivalStation_City_NameOrDepartureStation_City_Name(cityName, cityName);
    }
    @Transactional(readOnly = true)
    public List<Route> findByDepartureCity(String cityName) {
        return routeRepository.findAllByDepartureStation_City_Name(cityName);
    }
    @Transactional(readOnly = true)
    public List<Route> findByDepartureCityAndXSeatsLeft(String cityName,int seatsLeft) {
        return routeRepository.findByDepartureStationNameAndXSeats(cityName,seatsLeft);
    }
    @Transactional(readOnly = true)
    public List<Route> findByArrivalCity(String cityName) {
        return routeRepository.findAllByArrivalStation_City_Name(cityName);
    }
    @Transactional(readOnly = true)
    public List<Route> findByArrivalCityAndXSeatsLeft(String cityName,int seatsLeft) {
        return routeRepository.findByArrivalStationNameAndXSeats(cityName,seatsLeft);
    }
}
