package reservations.journey_planner.demo.requestPOJOs;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import reservations.journey_planner.demo.entities.Passenger;

@Getter
@Setter
@Data
public class PassengerDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    public Passenger asPassenger() {
        Passenger p = new Passenger();
        p.setEmail(email);
        p.setName(firstName);
        p.setSecond_name(lastName);
        return p;
    }
}
