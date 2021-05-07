package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reservations.journey_planner.demo.entities.Passenger;
import reservations.journey_planner.demo.repositories.PassengerRepository;

@Service
public class PassengerService {
    @Autowired
    PassengerRepository passengerRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Passenger savePassengerIfNotExists(Passenger passenger) {
        if (!passengerRepository.existsById(passenger.getId()))
            return passengerRepository.save(passenger);
        return passengerRepository.findPassengerById(passenger.getId());
    }
}