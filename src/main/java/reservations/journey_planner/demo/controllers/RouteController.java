package reservations.journey_planner.demo.controllers;

import org.apache.james.mime4j.field.datetime.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reservations.journey_planner.demo.configuration.Utils;
import reservations.journey_planner.demo.entities.Route;
import reservations.journey_planner.demo.services.RouteService;

import java.util.Date;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

@RestController
@RequestMapping("/routes")
public class RouteController {
    @Autowired RouteService routeService;

    @GetMapping("/all")
    public ResponseEntity<List<Route>> getAll() {
        return new ResponseEntity<>(routeService.findAll(), HttpStatus.OK);
    }


    @GetMapping("search/byDepartureCity")
    public ResponseEntity<List<Route>> getByDepartureCity(@RequestParam(name = "city") String cityName,
                                                          @RequestParam(value = "seatsLeft", required = false) Integer seatsLeft,
                                                          @RequestParam(name = "startDate", required = false) String startDate,
                                                          @RequestParam(name = "endDate", required = false) String endDate) {
        Date[] dates = Utils.converter(startDate, endDate);
        Date start = dates[0];
        Date end = dates[1];
        if (seatsLeft == null)
            return new ResponseEntity<>(routeService.findByDepartureCity(cityName, start, end), HttpStatus.OK);
        return new ResponseEntity<>(routeService.findByDepartureCityAndXSeatsLeft(cityName, seatsLeft), HttpStatus.OK);

    }

    @GetMapping("/search/byDepartureAndArrivalCity")
    public ResponseEntity getByDepartureAndArrival(
            @RequestParam(name = "departure") String depCity,
            @RequestParam(name = "arrival") String arrCity,
            @RequestParam(name = "shortest", required = false) Boolean shortestPath,
            @RequestParam(name = "seatsLeft", required = false) Integer seatsLeft,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate) {
        Date[] dates = Utils.converter(startDate, endDate);
        Date from = dates[0];
        Date to = dates[1];
        List<Route> routes = routeService.findByArrivalAndDepartureCity(depCity, arrCity, false, from, to);
        return new ResponseEntity(routes, HttpStatus.OK);
    }

    @GetMapping("search/byArrivalCity")
    public ResponseEntity<List<Route>> getByArrivalCity(
            @RequestParam(name = "city") String cityName,
            @RequestParam(value = "seatsLeft", required = false) Integer seatsLeft,
            @RequestParam(name = "startDate", required = false) String startDate,
            @RequestParam(name = "endDate", required = false) String endDate) {
        Date[] dates = Utils.converter(startDate, endDate);
        Date start = dates[0];
        Date end = dates[1];
        if (seatsLeft == null)
            return new ResponseEntity<>(routeService.findByArrivalCity(cityName,start,end), HttpStatus.OK);
        return new ResponseEntity<>(routeService.findByArrivalCityAndXSeatsLeft(cityName, seatsLeft, start, end), HttpStatus.OK);
    }


}
