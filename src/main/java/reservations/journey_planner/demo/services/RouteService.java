package reservations.journey_planner.demo.services;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxCircleLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
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
import java.util.List;
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
        List<Route> routes = routeRepository.findAll();
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

    public List<Route> findByArrivalAndDepartureCity(String departureCity, String arrivalCity, boolean shortestPath) {
        if (!shortestPath)
            routeRepository.findRouteByDepartureStation_City_NameAndArrivalStation_City_Name(departureCity, arrivalCity);
        City from = cityRepository.findByName(departureCity);
        City to = cityRepository.findByName(arrivalCity);
        if (from == null || to == null)
            throw new RuntimeException("No such city");
        return DijkstraShortestPath.findPathBetween(graph, from, to).getEdgeList()
                .stream().map(myGraphEdge::getRoute).collect(Collectors.toList());
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
    public List<Route> findByDepartureCity(String cityName) {
        return routeRepository.findAllByDepartureStation_City_Name(cityName);
    }

    @Transactional(readOnly = true)
    public List<Route> findByDepartureCityAndXSeatsLeft(String cityName, int seatsLeft) {
        return routeRepository.findByDepartureStationNameAndXSeats(cityName, seatsLeft);
    }

    @Transactional(readOnly = true)
    public List<Route> findByArrivalCity(String cityName) {
        return routeRepository.findAllByArrivalStation_City_Name(cityName);
    }

    @Transactional(readOnly = true)
    public List<Route> findByArrivalCityAndXSeatsLeft(String cityName, int seatsLeft) {
        return routeRepository.findByArrivalStationNameAndXSeats(cityName, seatsLeft);
    }
}
