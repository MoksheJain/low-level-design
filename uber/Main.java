package uber;
import java.util.*;

class Location {
    private double latitude;
    private double longitude;

    public Location(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double distanceTo(Location other) {
        double dx = latitude - other.latitude;
        double dy = longitude - other.longitude;

        return Math.sqrt(dx * dx + dy * dy);
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "(" + latitude + "," + longitude + ")";
    }
}

enum VehicleType {
    MINI,
    SEDAN,
    SUV
}

enum DriverStatus {
    AVAILABLE,
    ON_RIDE,
    OFFLINE
}

enum RideStatus {
    REQUESTED,
    ACCEPTED,
    STARTED,
    COMPLETED,
    CANCELLED
}

abstract class User {
    protected String id;
    protected String name;
    protected String phone;

    public User(String id, String name, String phone) {
        this.id = id;
        this.name = name;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}

class Rider extends User {
    public Rider(String id, String name, String phone) {
        super(id, name, phone);
    }
}

class Vehicle {
    private String number;
    private String model;
    private VehicleType type;

    public Vehicle(String number, String model, VehicleType type) {
        this.number = number;
        this.model = model;
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public String getModel() {
        return model;
    }

    public VehicleType getType() {
        return type;
    }
}

class Driver extends User {
    private Vehicle vehicle;
    private DriverStatus status;
    private Location currLocation;

    public Driver(String id, String name, String phone, Vehicle vehicle, Location currLocation) {
        super(id, name, phone);
        this.vehicle = vehicle;
        this.currLocation = currLocation;
        this.status = DriverStatus.AVAILABLE;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public DriverStatus getStatus() {
        return status;
    }

    public void setStatus(DriverStatus status) {
        this.status = status;
    }

    public Location getCurrLocation() {
        return currLocation;
    }

    public void updateLocation(Location location) {
        this.currLocation = location;
    }
}

class Ride {
    private String rideId;
    private Rider rider;
    private Driver driver;
    private Location pickup;
    private Location dropoff;
    private RideStatus status;
    private double distanceInKm;
    private int durationInMinutes;
    private double fare;
    
    public Ride(String rideId, Rider rider, Driver driver, Location pickup, Location dropoff, double distanceInKm, int durationInMinutes) {
        this.rideId = rideId;
        this.rider = rider;
        this.driver = driver;
        this.pickup = pickup;
        this.dropoff = dropoff;
        this.distanceInKm = distanceInKm;
        this.durationInMinutes = durationInMinutes;

        this.status = RideStatus.REQUESTED;
    }

    public String getRideId() {
        return rideId;
    }

    public Rider getRider() {
        return rider;
    }

    public Driver getDriver() {
        return driver;
    }

    public Location getPickupLocation() {
        return pickup;
    }

    public Location getDropoffLocation() {
        return dropoff;
    }

    public RideStatus getStatus() {
        return status;
    }

    public void setStatus(RideStatus status) {
        this.status = status;
    }

    public double getDistanceInKm() {
        return distanceInKm;
    }

    public int getDurationInMinutes() {
        return durationInMinutes;
    }

    public double getFare() {
        return fare;
    }

    public void setFare(double fare) {
        this.fare = fare;
    }
}

interface PricingStrategy {
    double calculateFare(Ride ride);
}

class NormalPricingStrategy implements PricingStrategy {
    private static final double BASE_FARE = 50;
    private static final double PER_KM_RATE = 12;
    private static final double PER_MINUTE_RATE = 2;

    @Override
    public double calculateFare(Ride ride) {
        return BASE_FARE + ride.getDistanceInKm() * PER_KM_RATE + ride.getDurationInMinutes() * PER_MINUTE_RATE;
    }
}

class SurgePricingStrategy implements PricingStrategy {
    private static final double BASE_FARE = 50;
    private static final double PER_KM_RATE = 12;
    private static final double PER_MINUTE_RATE = 2;

    private double surgeMultiplier;

    public SurgePricingStrategy(double surgeMultiplier) {
        this.surgeMultiplier = surgeMultiplier;
    }

    @Override
    public double calculateFare(Ride ride) {
        double fare = BASE_FARE + ride.getDistanceInKm() * PER_KM_RATE + ride.getDurationInMinutes() * PER_MINUTE_RATE;
        return fare * surgeMultiplier;
    }
}

class PremiumPricingStrategy implements PricingStrategy {
    private static final double BASE_FARE = 120;
    private static final double PER_KM_RATE = 20;
    private static final double PER_MINUTE_RATE = 3;

    @Override
    public double calculateFare(Ride ride) {
        return BASE_FARE + ride.getDistanceInKm() * PER_KM_RATE + ride.getDurationInMinutes() * PER_MINUTE_RATE;
    }
}

interface DriverMatchingStrategy {
    Driver findDriver(List<Driver> drivers, Location pickup);
}

class NearestDriverStrategy implements DriverMatchingStrategy {
    @Override
    public Driver findDriver(List<Driver> drivers, Location pickup) {
        Driver nearestDriver = null;
        double minDistance = Double.MAX_VALUE;
        
        for(Driver driver : drivers) {
            if(driver.getStatus() != DriverStatus.AVAILABLE) {
                continue;
            }
            double distance = driver.getCurrLocation().distanceTo(pickup);
            if(distance < minDistance) {
                minDistance = distance;
                nearestDriver = driver;
            }
        }

        return nearestDriver;
    }
}

class DriverService {
    private List<Driver> drivers;
    public DriverService() {
        drivers = new ArrayList<>();
    }

    public void registerDriver(Driver driver) {
        drivers.add(driver);
    }

    public List<Driver> getAllDrivers() {
        return drivers;
    }
}

class RideService {
    private DriverService driverService;
    private DriverMatchingStrategy matchingStrategy;
    private PricingStrategy pricingStrategy;

    public RideService(DriverService driverService, DriverMatchingStrategy matchingStrategy, PricingStrategy pricingStrategy) {
        this.driverService = driverService;
        this.matchingStrategy = matchingStrategy;
        this.pricingStrategy = pricingStrategy;
    }

    public Ride requestRide(Rider rider, Location pickup, Location dropoff, double distance, int duration) {
        Driver driver = matchingStrategy.findDriver(driverService.getAllDrivers(), pickup);
        if(driver == null) {
            throw new RuntimeException("No drivers available");
        }

        driver.setStatus(DriverStatus.ON_RIDE);
        Ride ride = new Ride(UUID.randomUUID().toString(), rider, driver, pickup, dropoff, distance, duration);
        double fare = pricingStrategy.calculateFare(ride);
        ride.setFare(fare);
        return ride;
    }

    public void startRide(Ride ride) {
        ride.setStatus(RideStatus.STARTED);
    }

    public void completeRide(Ride ride) {
        ride.setStatus(RideStatus.COMPLETED);
        ride.getDriver().setStatus(DriverStatus.AVAILABLE);
        ride.getDriver().updateLocation(ride.getDropoffLocation());
    }
}

public class Main {
    public static void main(String[] args) {
        DriverService driverService = new DriverService();

        Driver d1 = new Driver("D1", "Rahul", "999", new Vehicle("PB10AA1111", "Swift", VehicleType.MINI), new Location(5, 5));
        Driver d2 = new Driver("D2", "Aman", "888", new Vehicle("PB10BB2222", "Creta", VehicleType.SUV), new Location(2, 2));

        driverService.registerDriver(d1);
        driverService.registerDriver(d2);

        RideService rideService = new RideService(driverService, new NearestDriverStrategy(), new SurgePricingStrategy(1.5));

        Rider rider = new Rider("R1", "Mokshe", "777");

        Ride ride = rideService.requestRide(rider, new Location(1, 1), new Location(10, 10), 12, 25);

        System.out.println("Driver : " + ride.getDriver().getName());
        System.out.println("Fare : " + ride.getFare());
        rideService.startRide(ride);
        rideService.completeRide(ride);
        System.out.println(ride.getStatus());
    }

}