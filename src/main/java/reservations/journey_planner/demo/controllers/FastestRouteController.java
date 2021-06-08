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
import reservations.journey_planner.demo.configuration.Utils;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.entities.TrainStation;
import reservations.journey_planner.demo.repositories.RouteRepository;
import reservations.journey_planner.demo.repositories.TrainStationRepository;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings({"all"})
@RestController
@RequestMapping("/fastestRoute")
public class FastestRouteController {
    @Autowired
    TrainStationRepository trainStationRepository;
    private static final RelativeTime noChangeTime = RelativeTime.ZERO;
    private static Time day = new SimpleTime();
    private int journeyId = 0, connectionId = 0;
    @Autowired
    RouteRepository routeRepository;
    static final TransportSystem ice = new TransportSystem("Train");

    static Time fromDate(Date d) {
        return day.plusHours(d.getHours()).plusMinutes(d.getMinutes());
    }

    @GetMapping
    public ResponseEntity getRoute(@RequestParam(name = "from") String from,
                                   @RequestParam(name = "to") String to,
                                   @RequestParam(name = "hour") int hours,
                                   @RequestParam(name = "minutes") int minutes,
                                   @RequestParam(name = "date") String date) {
        Date[] dates = Utils.converter(date);
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
        List<Route> routes = routeRepository.findAllByDepartureTime_Day(dates[0]);
        routes.forEach(activeRoute -> {
            Stop fromS = trainStationStopHashMap.get(activeRoute.getDepartureStation());
            Stop toS = trainStationStopHashMap.get(activeRoute.getArrivalStation());
            Time depTime = fromDate(activeRoute.getDepartureTime());
            Time arrTime = fromDate(activeRoute.getArrivalTime());
            if(arrTime.isBefore(depTime))
                arrTime = arrTime.plusDays(1);
            DefaultModifiableJourney journey = new DefaultModifiableJourney(activeRoute.getId(), day, ice, 150);
            RoutePoints route = RoutePoints.from(fromS, toS);
            Connection c = Connection.from(activeRoute.getId(), fromS, toS, depTime, arrTime, journey, route);
            connections.add(c);
        });
        TransitNetwork network = TransitNetwork.createOf(stops, connections);
        System.out.println("Routes found : " + routes.size());
        RouteSearch search = new ConnectionScan(network);
        TrainStation departure = trainStationRepository.findByCity_Name(from);
        TrainStation arrival = trainStationRepository.findByCity_Name(to);
        Stop fromm = trainStationStopHashMap.get(departure);
        Stop too = trainStationStopHashMap.get(arrival);
        //tempo di partenza
        Optional<PublicTransportRoute> route = search.findRoute(fromm, too, new SimpleTime().plusHours(hours).plusMinutes(minutes));
        System.out.println(route.isPresent());
        if (route.isPresent())
            System.out.println(route.get());
        else return new ResponseEntity(HttpStatus.OK);
        List<Route> path = route.get().connections().stream().map(connection -> {
            return routes.stream().filter(r -> r.getId() == connection.id()).findFirst().get();
        }).collect(Collectors.toList());
        System.out.println(path);
        return new ResponseEntity(path, HttpStatus.MULTI_STATUS);

    }

    @GetMapping("/test")
    public ResponseEntity getRoute(@RequestParam(name = "from") String from,
                                   @RequestParam(name = "to") String to,
                                   @RequestParam(name = "time") int time) {
        TrainStation departure = trainStationRepository.findByCity_Name(from);
        TrainStation arrival = trainStationRepository.findByCity_Name(to);
        if (departure == null || arrival == null) return new ResponseEntity(HttpStatus.BAD_REQUEST);
        System.out.println("FROM : " + departure.getName() + " TO : " + arrival.getName());
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
            DefaultModifiableJourney journey = new DefaultModifiableJourney(activeRoute.getId(), day, ice, 25);
            RoutePoints route = RoutePoints.from(fromS, toS);
            System.out.println(fromS + "  " + toS + " " + depTime + "  " + arrTime);
            Connection c = Connection.from(activeRoute.getId(), fromS, toS, depTime, arrTime, journey, route);
            connections.add(c);
        });
        TransitNetwork network = TransitNetwork.createOf(stops, connections);
        RouteSearch search = new ConnectionScan(network);
        Stop source = trainStationStopHashMap.get(departure);
        Stop destination = trainStationStopHashMap.get(arrival);
        //tempo di partenza
        Optional<PublicTransportRoute> route = search.findRoute(source, destination, new SimpleTime().plusHours(time));
        System.out.println(route.isPresent());
        if (route.isPresent())
            System.out.println(route.get());
        else {
            System.out.println("not found");
            return new ResponseEntity(HttpStatus.OK);
        }
        List<Route> path = route.get().connections().stream().map(connection -> {
            return routes.stream().filter(r -> r.getId() == connection.id()).findFirst().get();
        }).collect(Collectors.toList());
        System.out.println(path);
        return new ResponseEntity(path, HttpStatus.MULTI_STATUS);

    }

}
