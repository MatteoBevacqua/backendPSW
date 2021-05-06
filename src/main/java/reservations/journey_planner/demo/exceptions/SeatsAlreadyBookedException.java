package reservations.journey_planner.demo.exceptions;

public class SeatsAlreadyBookedException extends RuntimeException{
    public SeatsAlreadyBookedException(){

    }

    public SeatsAlreadyBookedException(String msg){
        super(msg);
    }
}
