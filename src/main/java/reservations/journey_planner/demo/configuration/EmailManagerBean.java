package reservations.journey_planner.demo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reservations.journey_planner.demo.entities.Passenger;
import reservations.journey_planner.demo.entities.Reservation;

@Component
public class EmailManagerBean {
    @Autowired
    private JavaMailSender javaMailSender;

    @Async
    public void sendEmail(Reservation res, Passenger p) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(p.getEmail());
        msg.setSubject("Reservation #" + res.getId());
        msg.setText(res.toString());
        javaMailSender.send(msg);
    }
}
