package reservations.journey_planner.demo.requestPOJOs;

import lombok.Data;
import reservations.journey_planner.demo.entities.Passenger;


@Data
public class PassengerDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String telephoneNumber;
    private String username;
    private String password;

    public Passenger asPassenger() {
        Passenger p = new Passenger();
        p.setEmail(email);
        p.setName(firstName);
        p.setSecondName(lastName);
        p.setTelephoneNumber(telephoneNumber);
        return p;
    }

    public boolean isValid() {
        return firstName != null && lastName != null && email != null && password != null;
    }
}
