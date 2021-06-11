package reservations.journey_planner.demo.configuration;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import reservations.journey_planner.demo.entities.Passenger;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

@Component
public class Utils {

    public static Date[] converter(String... dates) {
        Date[] date = new Date[dates.length];
        TemporalAccessor temporalAccessor;
        Instant instant;
        try {
            for (int i = 0; i < date.length; i++) {
                temporalAccessor = DateTimeFormatter.ISO_INSTANT.parse(dates[i] + "Z");
                instant = Instant.from(temporalAccessor);
                date[i] = Date.from(instant);
            }
        } catch (Exception e) {
            return new Date[dates.length];
        }
        return date;
    }


    public static Jwt getPrincipal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println(principal);
        return (Jwt) principal;
    }

    public static Passenger getPassengerFromToken(Jwt jwt) {
        Passenger p = new Passenger();
        p.setEmail(getEmail(jwt));
        p.setName(getName(jwt));
        p.setSecondName(getSecondName(jwt));
        p.setId(jwt.getSubject());
        return p;
    }

    public static String getAuthServerId(Jwt jwt) {
        return getTokenNode(jwt).get("subject").asText();
    }

    public static String getName(Jwt jwt) {
        return getTokenNode(jwt).get("claims").get("given_name").asText();
    }

    public static String getSecondName(Jwt jwt) {
        return getTokenNode(jwt).get("claims").get("family_name").asText();
    }

    public static String getEmail(Jwt jwt) {
        return getTokenNode(jwt).get("claims").get("email").asText();
    }

    public String getUsername(Jwt jwt) {
        return getTokenNode(jwt).get("claims").get("preferred_username").asText();
    }

    private static JsonNode getTokenNode(Jwt jwt) {
        ObjectMapper objectMapper = new ObjectMapper();
        String jwtAsString;
        JsonNode jsonNode;
        try {
            jwtAsString = objectMapper.writeValueAsString(jwt);
            jsonNode = objectMapper.readTree(jwtAsString);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Unable to retrieve user's info!");
        }
        return jsonNode;
    }


}
