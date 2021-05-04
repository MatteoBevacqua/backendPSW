package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reservations.journey_planner.demo.entities.Reservation;
import reservations.journey_planner.demo.repositories.ReservationRepository;

import java.util.List;
@Service
public class ReservationService {
    @Autowired private ReservationRepository reservationRepository;

    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }
}
