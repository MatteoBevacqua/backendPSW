package reservations.journey_planner.demo.services;

import edu.kit.ifv.mobitopp.publictransport.connectionscan.ConnectionScan;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.RouteSearch;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.TransitNetwork;
import edu.kit.ifv.mobitopp.publictransport.model.*;
import edu.kit.ifv.mobitopp.time.RelativeTime;
import edu.kit.ifv.mobitopp.time.SimpleTime;
import edu.kit.ifv.mobitopp.time.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.entities.TrainStation;
import reservations.journey_planner.demo.repositories.RouteRepository;
import reservations.journey_planner.demo.repositories.TrainStationRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class FastestRouteService {
    @Autowired
    TrainStationRepository trainStationRepository;
    @Autowired
    RouteRepository routeRepository;
    private static final RelativeTime noChangeTime = RelativeTime.ZERO;
    private static Time day = new SimpleTime();

    static final TransportSystem train = new TransportSystem("Train");

    static Time fromDate(Date d) {
        return day.plusHours(d.getHours()).plusMinutes(d.getMinutes());
    }



    public List<Route> computeFastestRoute(String from, String to, int hours, int minutes, Date date) {
        HashMap<TrainStation, Stop> trainStationStopHashMap = new HashMap<>();
        List<TrainStation> trainStations = trainStationRepository.findAll();
        List<Stop> stops = new ArrayList<>();
        List<DefaultStation> stations = new ArrayList<>();
        trainStations.forEach(trainStation -> {
            DefaultStation s;
            Stop stop;
            stations.add(s = new DefaultStation(trainStation.getId() - 1, Collections.emptyList()));
            stops.add(stop = new Stop(s.id(), trainStation.getName(), trainStation.getCity().toPoint(), noChangeTime, s, s.id()));
            trainStationStopHashMap.put(trainStation, stop);
        });
        Connections connections = new Connections();
        List<Route> routes = routeRepository.findAllByDepartureTime_Day(date);
        routes.forEach(activeRoute -> {
            Stop fromS = trainStationStopHashMap.get(activeRoute.getDepartureStation());
            Stop toS = trainStationStopHashMap.get(activeRoute.getArrivalStation());
            Time depTime = fromDate(activeRoute.getDepartureTime());
            Time arrTime = fromDate(activeRoute.getArrivalTime());
            if (arrTime.isBefore(depTime))
                arrTime = arrTime.plusDays(1);
            DefaultModifiableJourney journey = new DefaultModifiableJourney(activeRoute.getId(), day, train, 150);
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
        Optional<PublicTransportRoute> route = search.findRoute(fromm, too, new SimpleTime().plusHours(hours).plusMinutes(minutes));
        if (route.isEmpty())
            return null;
        return route.get().connections().stream().map(connection -> routes.stream().filter(r -> r.getId() == connection.id()).findFirst().get()).collect(Collectors.toList());
    }
}
