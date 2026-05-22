package elevator;
import java.util.*;
import java.util.Locale.IsoCountryCode;

enum Direction {
    UP, 
    DOWN, 
    IDLE
}

enum ElevatorState {
    MOVING,
    STOPPED,
    IDLE,
    MAINTENANCE
}

class Request {
    int srcFloor;
    int destFloor;
    Direction direction;

    public Request(int srcFloor, int destFloor) {
        this.srcFloor = srcFloor;
        this.destFloor = destFloor;
        if(destFloor > srcFloor) {
            direction = Direction.UP;
        }
        else {
            direction = Direction.DOWN;
        }
    }
}

class Elevator {
    int id;
    int currFloor;
    Direction direction;
    ElevatorState state;

    TreeSet<Integer> upStops;
    TreeSet<Integer> downStops;

    public Elevator(int id) {
        this.id = id;
        this.currFloor = 0;
        this.direction = Direction.IDLE;
        this.state = ElevatorState.IDLE;

        upStops = new TreeSet<>();
        downStops = new TreeSet<>(Collections.reverseOrder());
    }

    public void addRequest(Request request) {
        state = ElevatorState.MOVING;
        if(request.srcFloor > currFloor) {
            upStops.add(request.srcFloor);
        }
        else {
            downStops.add(request.srcFloor);
        }
        if(request.destFloor > request.srcFloor) {
            upStops.add(request.destFloor);
        }
        else {
            downStops.add(request.destFloor);
        }
        if(direction == Direction.IDLE) {
            direction = request.direction;
        }
    }

    public void move() {
        if(direction == Direction.UP) {
            if(!upStops.isEmpty()) {
                int nxtFloor = upStops.pollFirst();
                while(currFloor < nxtFloor) {
                    currFloor++;
                    System.out.println("Elevator " + id + " moving up to the floor " + currFloor);
                }
                System.out.println("Elevator " + id + " stopped at " + currFloor);
            }
            if(upStops.isEmpty()) {
                if(!downStops.isEmpty()) {
                    direction = Direction.DOWN;
                }
                else {
                    direction = Direction.IDLE;
                    state = ElevatorState.IDLE;
                }
            }
        }
        else if(direction == Direction.DOWN) {
            if(!downStops.isEmpty()) {
                int nxtFloor = downStops.pollFirst();
                while(currFloor > nxtFloor) {
                    currFloor--;
                    System.out.println("Elevator " + id + " moving down to the floor " + currFloor);
                }
                System.out.println("Elevator " + id + " stopped at " + currFloor);
            }
            if(downStops.isEmpty()) {
                if(!upStops.isEmpty()) {
                    direction = Direction.UP;
                }
                else {
                    direction = Direction.IDLE;
                    state = ElevatorState.IDLE;
                }
            }
        }
    }
}

class ElevatorController {
    List<Elevator> elevators;

    public ElevatorController(int count) {
        elevators = new ArrayList<>();
        for(int i = 0; i < count; i++) {
            elevators.add(new Elevator(i));
        }
    }
    
    public Elevator findBestElevator(Request request) {
        Elevator bestElevator = null;
        int minDist = Integer.MAX_VALUE;

        for(Elevator elevator: elevators) {
            if(elevator.direction == Direction.IDLE) {
                int dist = Math.abs(elevator.currFloor - request.srcFloor);
                if(dist < minDist) {
                    minDist = dist;
                    bestElevator = elevator;
                }
            }
        }

        if(bestElevator == null) {
            for(Elevator elevator : elevators) {
                int dist = Math.abs(elevator.currFloor - request.srcFloor);
                if(dist < minDist) {
                    minDist = dist;
                    bestElevator = elevator;
                }
            }
        }
        return bestElevator;
    }

    public void submitRequest(Request request) {
        Elevator elevator = findBestElevator(request);
        System.out.println("Assigning elevator " + elevator.id + " to request");
        elevator.addRequest(request);
    }

    public void step() {
        for(Elevator elevator : elevators) {
            elevator.move();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        ElevatorController controller = new ElevatorController(3);
        controller.submitRequest(new Request(0, 5));
        controller.submitRequest(new Request(2, 8));
        controller.submitRequest(new Request(10, 1));

        for(int i = 0; i < 15; i++) {
            controller.step();
        }
    }
}