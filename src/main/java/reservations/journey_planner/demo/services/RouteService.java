package reservations.journey_planner.demo.services;

import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reservations.journey_planner.demo.entities.City;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.repositories.CityRepository;
import reservations.journey_planner.demo.repositories.RouteRepository;
import reservations.journey_planner.demo.requestPOJOs.myGraphEdge;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class RouteService {
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    EntityManager entityManager;


    public List<Route> findByArrivalAndDepartureCity(String departureCity, String arrivalCity, Date start, Date end) {
        if (start == null && end == null)
            return routeRepository.findRouteByDepartureStation_City_NameAndArrivalStation_City_Name(departureCity, arrivalCity);
        if (start != null)
            return routeRepository.findRouteByDepartureStation_City_NameAndArrivalStation_City_NameAndDepartureTimeAfter(departureCity, arrivalCity, start);
        return routeRepository.findRouteByDepartureStation_City_NameAndArrivalStation_City_NameAndDepartureTimeBefore(departureCity, arrivalCity, end);

    }

    public List<Route> findByArrivalAndDepartureCityAndXSeats(String departureCity, String arrivalCity, Date start, Date end, int seatsLeft) {

        if (start == null && end == null)
            return routeRepository.findRouteByDepartureStation_City_NameAndArrivalStation_City_NameAndSeatsLeftGreaterThanEqual(departureCity, arrivalCity, seatsLeft);
        if (start != null)
            return routeRepository.findRouteByDepartureStation_City_NameAndArrivalStation_City_NameAndDepartureTimeAfterAndSeatsLeftGreaterThanEqual(departureCity, arrivalCity, start, seatsLeft);
        return routeRepository.findRouteByDepartureStation_City_NameAndArrivalStation_City_NameAndDepartureTimeBefore(departureCity, arrivalCity, end);

    }

    public List<Route> shortestPathInADay(String departureCity, String arrivalCity, Date day, Integer seatsLeft) {
        City from = cityRepository.findByName(departureCity);
        City to = cityRepository.findByName(arrivalCity);
        if (from == null || to == null)
            throw new RuntimeException("No such city");
        List<Route> routes;
        if (seatsLeft == null)
            routes = routeRepository.findAllByDepartureTimeSameDay(day);
        else
            routes = routeRepository.findAllByDepartureTimeSameDayAndXSeatsLeft(day, seatsLeft);
        return findShortestPath(routes, from, to);
    }

    @Transactional(readOnly = true)
    List<Route> findShortestPath(List<Route> routes, City from, City to) {
        City[] first = new City[2];
        myGraphEdge[] edge = new myGraphEdge[1];
        DirectedWeightedMultigraph<City, myGraphEdge> myGraph = new DirectedWeightedMultigraph<>(myGraphEdge.class);
        myGraph.addVertex(from);
        myGraph.addVertex(to);
        routes.forEach(
                route -> {
                    myGraph.addVertex(first[0] = route.getDepartureStation().getCity());
                    myGraph.addVertex(first[1] = route.getArrivalStation().getCity());
                    edge[0] = myGraph.addEdge(first[0], first[1]);
                    edge[0].setRoute(route);
                    myGraph.setEdgeWeight(edge[0], route.getRouteLength());
                }
        );
        GraphPath<City, myGraphEdge> path = DijkstraShortestPath.findPathBetween(myGraph, from, to);
        if (path != null)
            return path.getEdgeList().stream().map(myGraphEdge::getRoute).collect(Collectors.toList());
        return null;
    }


    @Transactional(readOnly = true)
    public List<Route> findAll() {
        return routeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Route> findByCityBothDepartureAndArrival(String cityName) {
        return routeRepository.findAllByArrivalStation_City_NameOrDepartureStation_City_Name(cityName, cityName);
    }

    @Transactional(readOnly = true)
    public List<Route> findByDepartureCity(String cityName, Date start, Date end) {
        if (start == null && end == null)
            return routeRepository.findAllByDepartureStation_City_Name(cityName.toLowerCase());
        if (start != null && end != null)
            return routeRepository.findAllByDepartureStation_City_NameAndDepartureTimeBetween(cityName, start, end);
        if (start != null)
            return routeRepository.findAllByDepartureStation_City_NameAndDepartureTimeBefore(cityName, start);
        return routeRepository.findAllByDepartureStation_City_NameAndDepartureTimeAfter(cityName, end);
    }


    public List<Route> findByDepartureCityAndXSeatsLeft(String cityName, int seatsLeft) {
        return routeRepository.findByDepartureStation_City_NameAndSeatsLeftIsGreaterThanEqual(cityName, seatsLeft);
    }


    public List<Route> findByArrivalCity(String cityName, Date start, Date end) {
        if (start == null && end == null)
            return routeRepository.findAllByArrivalStation_City_Name(cityName);
        if (start != null && end != null)
            return routeRepository.findAllByArrivalStation_City_NameAndDepartureTimeAfterAndDepartureTimeBefore(cityName, start, end);
        if (start != null)
            return routeRepository.findAllByArrivalStation_City_NameAndDepartureTimeAfter(cityName, start);
        return routeRepository.findAllByArrivalStation_City_NameAndDepartureTimeBefore(cityName, end);
    }

    public List<Route> findByArrivalCityAndXSeatsLeft(String cityName, int seatsLeft, Date start, Date end) {
        if (start == null && end == null)
            return routeRepository.findByArrivalStation_City_NameAndSeatsLeftIsGreaterThanEqual(cityName, seatsLeft);
        if (start != null && end != null)
            return routeRepository.findByArrivalStation_City_NameAndSeatsLeftIsGreaterThanEqualAndDepartureTimeIsBetween(cityName, seatsLeft, start, end);
        if (start != null)
            return routeRepository.findByArrivalStation_City_NameAndSeatsLeftIsGreaterThanEqualAndDepartureTimeAfter(cityName, seatsLeft, start);
        return routeRepository.findByArrivalStation_City_NameAndSeatsLeftIsGreaterThanEqualAndDepartureTimeBefore(cityName, seatsLeft, end);
    }
}
