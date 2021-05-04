package reservations.journey_planner.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reservations.journey_planner.demo.entities.Train;
import reservations.journey_planner.demo.repositories.TrainRepository;

import java.util.List;

@Service
public class TrainService {
    @Autowired
    private TrainRepository trainRepository;

    public List<Train> findAll() {
        System.out.println(trainRepository.findAll());
        return trainRepository.findAll();
    }
    public List<Train> findByType(String type){
        return trainRepository.findAllByType(type);
    }
}
