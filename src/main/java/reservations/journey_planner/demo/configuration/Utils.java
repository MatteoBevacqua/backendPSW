package reservations.journey_planner.demo.configuration;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import reservations.journey_planner.demo.entities.Passenger;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

@UtilityClass
public class Utils {

    public static Date[] converter(String startDate, String endDate) {
        Date from = null, to = null;
        TemporalAccessor ta;
        Instant i;
        if (startDate != null) {
            ta = DateTimeFormatter.ISO_INSTANT.parse(startDate + "Z");
            i = Instant.from(ta);
            from = Date.from(i);
        }
        if (endDate != null) {
            ta = DateTimeFormatter.ISO_INSTANT.parse(endDate + "Z");
            i = Instant.from(ta);
            to = Date.from(i);
        }
        return new Date[]{from, to};
    }


    public static Jwt getPrincipal() {
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        return (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Passenger getPassengerFromToken(Jwt jwt) {
        Passenger p = new Passenger();
        p.setEmail(getEmail(jwt));
        p.setName(getName(jwt));
        p.setSecond_name(getSecondName(jwt));
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
