package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reservations.journey_planner.demo.entities.TrainStation;
import reservations.journey_planner.demo.repositories.TrainStationRepository;

import java.util.List;

@Service
public class TrainStationService {
    @Autowired
    TrainStationRepository trainStationRepository;

    public List<TrainStation> getAll() {
        return trainStationRepository.findAll();
    }

    public List<TrainStation> getByCity(String city) {
        return trainStationRepository.getTrainStationByCity_Name(city);

    }
}
