package reservations.journey_planner.demo.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reservations.journey_planner.demo.configuration.Utils;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.services.FastestRouteService;

import java.util.Date;
import java.util.List;

@SuppressWarnings({"all"})
@RestController
@RequestMapping("/fastestRoute")
public class FastestRouteController {

    @Autowired
    FastestRouteService fastestRouteService;


    @GetMapping
    public ResponseEntity<List<Route>> getRoute(@RequestParam(name = "from") String from,
                                                @RequestParam(name = "to") String to,
                                                @RequestParam(name = "hour") int hours,
                                                @RequestParam(name = "minutes") int minutes,
                                                @RequestParam(name = "date") String date) {
        Date[] dates = Utils.converter(date);
        return new ResponseEntity(fastestRouteService.computeFastestRoute(from, to, hours, minutes, dates[0]),
                HttpStatus.OK);
    }


}
