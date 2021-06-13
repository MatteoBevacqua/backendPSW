package reservations.journey_planner.demo.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import reservations.journey_planner.demo.entities.Passenger;

@Component
public class EmailManagerBean {
    @Autowired
    private JavaMailSender javaMailSender;



    @Async
    public void sendTextEmail(String message,String subject, Passenger p) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(p.getEmail());
        msg.setSubject(subject);
        msg.setText(message);
        javaMailSender.send(msg);
    }
}
