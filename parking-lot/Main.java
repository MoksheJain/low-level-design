import java.util.*;
abstract class Vehicle {
    protected String ln;
    public Vehicle(String ln) {
        this.ln = ln;
    }
    public String getLicenseNumber() {
        return ln;
    }
}
class Bike extends Vehicle {
    public Bike(String ln) {
        super(ln);
    }
}
class Car extends Vehicle {
    public Car(String ln) {
        super(ln);
    }
}
class Truck extends Vehicle {
    public Truck(String ln) {
        super(ln);
    }
}
abstract class ParkingSlot {
    protected int slotId;
    protected Vehicle vehicle;
    public ParkingSlot(int slotId) {
        this.slotId = slotId;
    }
    public boolean isAvailable() {
        return vehicle == null;
    }
    public void assignVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }
    public void removeVehicle() {
        this.vehicle = null;
    }
    public abstract boolean canFitVehicle(Vehicle vehicle);
}
class BikeSlot extends ParkingSlot {
    public BikeSlot(int slotId) {
        super(slotId);
    }
    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle instanceof Bike;
    }
}
class CarSlot extends ParkingSlot {
    public CarSlot(int slotId) {
        super(slotId);
    }
    @Override
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle instanceof Car;
    }
}
class TruckSlot extends ParkingSlot {
    public TruckSlot(int slotId) {
        super(slotId);
    }
    @Override 
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle instanceof Truck;
    }
}
class Floor {
    private int fn;
    private List<ParkingSlot> slots;
    public Floor(int fn, List<ParkingSlot> slots) {
        this.fn = fn;
        this.slots = slots;
    }
    public ParkingSlot getAvailableSlot(Vehicle vehicle) {
        for(ParkingSlot slot: slots) {
            if(slot.isAvailable() && slot.canFitVehicle(vehicle)) {
                return slot;
            }
        }
        return null;
    }
}
class Ticket {
    private static int counter = 0;
    private int id;
    private Vehicle vehicle;
    private ParkingSlot slot;
    private long entryTime;
    private long exitTime;
    public Ticket(Vehicle vehicle, ParkingSlot slot) {
        counter++;
        this.id = counter;
        this.vehicle = vehicle;
        this.slot = slot;
        this.entryTime = System.currentTimeMillis();
    }
    public void closeTicket() {
        this.exitTime = System.currentTimeMillis();
    }
    public long getDurationMinutes() {
        return Math.max(1, (exitTime - entryTime) / (1000 * 60));
    }
    public ParkingSlot getSlot() {
        return slot;
    }
}
interface PricingStrategy {
    double calculateFees(long durationMins);
}
class HourlyPricingStrategy implements PricingStrategy {
    private double ratePerHour = 20.0;
    @Override
    public double calculateFees(long durationMins) {
        double hours = Math.ceil(durationMins / 60.0);
        return hours * ratePerHour;
    }
}

class SecondlyPricingStrategy implements PricingStrategy {
    private double ratePerSec = 20.0;
    @Override
    public double calculateFees(long durationMins) {
        double seconds = durationMins * 60;
        return seconds * ratePerSec;
    }
}
class ParkingLot {
    private List<Floor> floors;
    private PricingStrategy ps;
    private Map<String, Ticket> active;
    public ParkingLot(List<Floor> floors, PricingStrategy ps) {
        this.floors = floors;
        this.ps = ps;
        this.active = new HashMap<>();
    }
    public Ticket parkVehicle(Vehicle vehicle) {
        for(Floor floor: floors) {
            ParkingSlot slot = floor.getAvailableSlot(vehicle);
            if(slot != null) {
                slot.assignVehicle(vehicle);
                Ticket ticket = new Ticket(vehicle, slot);
                active.put(vehicle.getLicenseNumber(), ticket);
                System.out.println("Parked vehicle: " + vehicle.getLicenseNumber());
                return ticket;
            }
        }
        System.out.println("Parking full");
        return null;
    }
    public double unparkVehicle(String ln) {
        Ticket ticket = active.get(ln);
        if(ticket == null) {
            throw new RuntimeException("Invalid ticket");
        }
        ticket.closeTicket();
        ticket.getSlot().removeVehicle();
        double fee = ps.calculateFees(ticket.getDurationMinutes());
        active.remove(ln);
        System.out.println("Unparked vehicle: " + ln + " , Fee: " + fee);
        return fee;
    }
}
public class Main {
    public static void main(String[] args) {
        List<ParkingSlot> slots = Arrays.asList(new BikeSlot(1), new CarSlot(2), new TruckSlot(3));
        Floor floor1 = new Floor(1, slots);
        ParkingLot pl = new ParkingLot(Arrays.asList(floor1), new SecondlyPricingStrategy());
        Vehicle car = new Car("MH-01-1234");
        Ticket ticket = pl.parkVehicle(car);
        try {
            Thread.sleep(2000);
        }
        catch(InterruptedException e) {}
        pl.unparkVehicle("MH-01-1234");
    }
}