package reservations.journey_planner.demo.controllers;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.ConnectionScan;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.TransitNetwork;
import edu.kit.ifv.mobitopp.publictransport.model.*;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.entities.TrainStation;
import reservations.journey_planner.demo.repositories.RouteRepository;
import reservations.journey_planner.demo.repositories.TrainStationRepository;

import java.awt.geom.Point2D;
import java.time.Instant;
import java.util.*;

@SuppressWarnings({"all"})
@RestController
@RequestMapping("/fastestRoute")
public class FastestRouteController {
    @Autowired
    TrainStationRepository trainStationRepository;
    private static final RelativeTime noChangeTime = RelativeTime.ZERO;
    private static final Point2D.Double locationOfAmsterdam = new Point2D.Double(4.890444, 52.370197);
    private int journeyId = 0, connectionId = 0;
    @Autowired
    RouteRepository routeRepository;
    static final TransportSystem ice = new TransportSystem("Train");

    static Time fromDate(Date d) {
        return SimpleTime.of(0, d.getHours(), d.getMinutes(), d.getSeconds());
    }

    @GetMapping
    public ResponseEntity getRoute(@RequestParam(name = "from") String from,
                                   @RequestParam(name = "to") String to,
                                   @RequestParam(name = "time") int time) {

        List<TrainStation> trainStations = trainStationRepository.findAll();
        List<Stop> stops = new ArrayList<>();
        List<DefaultStation> stations = new ArrayList<>();
        HashMap<TrainStation, Stop> trainStationStopHashMap = new HashMap<>();
        trainStations.forEach(trainStation -> {
            DefaultStation s;
            Stop stop;
            stations.add(s = new DefaultStation(trainStation.getId() - 1, Collections.emptyList()));
            stops.add(stop = new Stop(s.id(), trainStation.getName(), trainStation.getCity().toPoint(), noChangeTime, s, s.id()));
            trainStationStopHashMap.put(trainStation, stop);
        });
        Connections connections = new Connections();
        List<Route> routes = routeRepository.findAllByDepartureTime_Day(Date.from(Instant.now()));
        routes.forEach(activeRoute -> {
            Stop fromS = trainStationStopHashMap.get(activeRoute.getDepartureStation());
            Stop toS = trainStationStopHashMap.get(activeRoute.getArrivalStation());
            Time depTime = fromDate(activeRoute.getDepartureTime());
            Time arrTime = fromDate(activeRoute.getArrivalTime());
            DefaultModifiableJourney journey = new DefaultModifiableJourney(activeRoute.getId(), new SimpleTime(2*60*60), ice, 0);
            RoutePoints route = RoutePoints.from(fromS, toS);
            Connection c = Connection.from(activeRoute.getId(), fromS, toS, depTime, arrTime, journey, route);
            connections.add(c);
        });
        TransitNetwork network = TransitNetwork.createOf(stops, connections);
        RouteSearch search = new ConnectionScan(network);
        TrainStation departure = trainStationRepository.findByCity_Name(from);
        TrainStation arrival = trainStationRepository.findByCity_Name(to);
        Stop fromm = trainStationStopHashMap.get(departure);
        Stop too = trainStationStopHashMap.get(arrival);
        //tempo di partenza
        Optional<PublicTransportRoute> route = search.findRoute(fromm, too, SimpleTime.from(RelativeTime.ofHours(time)));
        System.out.println(route.isPresent());
        if (route.isPresent())
            System.out.println(route.get());
        return new ResponseEntity(HttpStatus.MULTI_STATUS);

    }

}
