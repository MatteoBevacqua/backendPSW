package reservations.journey_planner.demo.services;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import org.apache.james.mime4j.field.datetime.DateTime;
import org.jgrapht.GraphPath;
import org.jgrapht.ListenableGraph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.ext.JGraphXAdapter;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.DirectedWeightedMultigraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reservations.journey_planner.demo.entities.City;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.repositories.CityRepository;
import reservations.journey_planner.demo.repositories.RouteRepository;
import com.mxgraph.util.mxCellRenderer;
import reservations.journey_planner.demo.requestPOJOs.myGraphEdge;

import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


@Service
public class RouteService {
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private CityRepository cityRepository;

    private DirectedWeightedMultigraph<City, myGraphEdge> graph;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void constructGraph() {
        List<City> cities = cityRepository.findAll();
        List<Route> routes = routeRepository.findAllByDepartureTimeAfter(Date.from(Instant.now()));
        graph = new DirectedWeightedMultigraph<>(myGraphEdge.class);
        cities.forEach(graph::addVertex);
        myGraphEdge[] edges = new myGraphEdge[1];
        routes.forEach(route -> {
                    edges[0] = graph.addEdge(route.getDepartureStation().getCity(), route.getArrivalStation().getCity());
                    edges[0].setRoute(route);
                    graph.setEdgeWeight(edges[0], route.getRouteLength());
                }
        );

    }

    public List<Route> findByArrivalAndDepartureCity(String departureCity, String arrivalCity, boolean shortestPath, Date start, Date end) {
        if (!shortestPath) {
            if (start == null && end == null)
                return routeRepository.findRouteByDepartureStation_City_NameAndArrivalStation_City_Name(departureCity, arrivalCity);
            if (start != null)
                return routeRepository.findRouteByDepartureStation_City_NameAndArrivalStation_City_NameAndDepartureTimeAfter(departureCity, arrivalCity, start);
            List<Route> bef = routeRepository.findRouteByDepartureStation_City_NameAndArrivalStation_City_NameAndDepartureTimeBefore(departureCity, arrivalCity, end);
            bef.forEach(route -> System.out.println(route.getDepartureTime().before(end)));
            return bef;
        }
        City from = cityRepository.findByName(departureCity);
        City to = cityRepository.findByName(arrivalCity);
        if (from == null || to == null)
            throw new RuntimeException("No such city");
        if (start == null && end == null)
            return DijkstraShortestPath.findPathBetween(graph, from, to).getEdgeList()
                    .stream().map(myGraphEdge::getRoute).collect(Collectors.toList());
        List<Route> routes;
        City[] first = new City[2];
        myGraphEdge[] edge = new myGraphEdge[1];
        if (start != null)
            routes = routeRepository.findAllByDepartureTimeAfter(start);
        else routes = routeRepository.findAllByDepartureTimeBefore(end);
        DirectedWeightedMultigraph<City, myGraphEdge> myGraph = new DirectedWeightedMultigraph<>(myGraphEdge.class);
        routes.forEach(
                route -> {
                    myGraph.addVertex(first[0] = route.getDepartureStation().getCity());
                    myGraph.addVertex(first[1] = route.getArrivalStation().getCity());
                    edge[0] = myGraph.addEdge(first[0], first[1]);
                    myGraph.setEdgeWeight(edge[0], route.getRouteLength());
                }
        );
        return DijkstraShortestPath.findPathBetween(myGraph, from, to).getEdgeList().stream().map(myGraphEdge::getRoute).collect(Collectors.toList());

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

    @Transactional(readOnly = true)
    public List<Route> findByDepartureCityAndXSeatsLeft(String cityName, int seatsLeft) {
        return routeRepository.findByDepartureStationNameAndXSeats(cityName, seatsLeft);
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
            return routeRepository.findByArrivalStationNameAndXSeats(cityName, seatsLeft);
        if (start != null && end != null)
            return routeRepository.findByArrivalStationNameAndXSeatsInTimePeriod(cityName, seatsLeft, start, end);
        if (start != null)
            return routeRepository.findByArrivalStationNameAndXSeatsAfter(cityName, seatsLeft, start);
        return routeRepository.findByArrivalStationNameAndXSeatsBefore(cityName, seatsLeft, end);
    }
}
