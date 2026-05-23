import java.util.*;

enum CarType {
    SEDAN,
    SUV_5, 
    SUV_7,
    HATCHBACK,
    LUXURY
}

enum CarStatus {
    AVAILABLE,
    MAINTENANCE,
    BOOKED
}


class User {
    private int id;
    private String name;
    private String email;

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

class Car {
    private int id;
    private String model;
    private CarType type;
    private double pricePerDay;
    private CarStatus status;

    public Car(int id, String model, CarType type, double pricePerDay) {
        this.id = id;
        this.model = model;
        this.type = type;
        this.pricePerDay = pricePerDay;
        this.status = CarStatus.AVAILABLE;
    }

    public int getId() {
        return id;
    }

    public String getModel() {
        return model;
    }

    public CarType getType() {
        return type;
    }

    public double getPricePerDay() {
        return pricePerDay;
    }

    public CarStatus getCarStatus() {
        return status;
    }

    public void setCarStatus(CarStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Car Id: " + id + ", Model: " + model + ", Type: " + type + ", Price/day: " + pricePerDay + ", Status: " + status;
    }
}

interface PricingStrategy {
    double calculatePrice(Car car, int days);
}

class NormalPricing implements PricingStrategy {
    @Override
    public double calculatePrice(Car car, int days) {
        return car.getPricePerDay() * days;
    }
}

class SurgePricing implements PricingStrategy {
    private double surgeMultiplier;

    public SurgePricing(double surgeMultiplier) {
        this.surgeMultiplier = surgeMultiplier;
    }

    @Override
    public double calculatePrice(Car car, int days) {
        return car.getPricePerDay() * days * 1.8;
    }
}

class Booking {
    private int bookingId;
    private User user;
    private Car car;
    private int days;
    private double totalPrice;

    public Booking(int bookingId, User user, Car car, int days, PricingStrategy ps) {
        this.bookingId = bookingId;
        this.user = user;
        this.car = car;
        this.days = days;
        this.totalPrice = ps.calculatePrice(car, days);
    }

    public int getBookingId() {
        return bookingId;
    }

    public Car getCar() {
        return car;
    }

    @Override
    public String toString() {
        return "\nBooking id: " + bookingId + "\nUser: " + user.getName() + "\nCar: " + car.getModel() + "\nDays: " + days + "\nTotal Price: " + totalPrice;
    }
}

class CarRentalSystem {
    private List<Car> cars;
    private List<Booking> bookings;
    private int bookingCounter;

    public CarRentalSystem() {
        cars = new ArrayList<>();
        bookings = new ArrayList<>();
        bookingCounter = 1;
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public List<Car> searchCars(CarType type) {
        List<Car> res = new ArrayList<>();

        for(Car car: cars) {
            if(car.getType() == type) {
                if(car.getCarStatus() == CarStatus.AVAILABLE) {
                    res.add(car);
                }
            }
        }

        return res;
    }

    public Booking bookCar(User user, int carId, int days, PricingStrategy ps) {
        for(Car car: cars) {
            if(car.getId() == carId) {
                if(car.getCarStatus() == CarStatus.AVAILABLE) {
                    car.setCarStatus(CarStatus.BOOKED);
                    Booking booking = new Booking(bookingCounter++, user, null, days, ps);
                    bookings.add(booking);
                    System.out.println("Car booked successfully");
                    return booking;
                }
            }
        }
        System.out.println("Car not available");
        return null;
    }

    public void cancelBooking(int bookingId) {
        for(Booking booking : bookings) {
            if(booking.getBookingId() == bookingId) {
                booking.getCar().setCarStatus(CarStatus.AVAILABLE);
                bookings.remove(booking);
                System.out.println("Booking cancelled");
                return;
            }
        }
        System.out.println("Booking not found");
    }

    public void displayAllCars() {
        for(Car car: cars) {
            System.out.println(car);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        CarRentalSystem system = new CarRentalSystem();

        Car car1 = new Car(1, "Honda City", CarType.SEDAN, 3000);
        Car car2 = new Car(2, "Swift", CarType.HATCHBACK, 1500);
        Car car3 = new Car(3, "Seltos", CarType.SUV_5, 3500);
        Car car4 = new Car(4, "Innova", CarType.SUV_7, 5000);
        Car car5 = new Car(1, "i7", CarType.LUXURY, 10000);

        system.addCar(car1);
        system.addCar(car2);
        system.addCar(car3);
        system.addCar(car4);
        system.addCar(car5);

        System.out.println("Available cars: ");
        system.displayAllCars();

        User user = new User(1, "Mokshe", "mokshe@jain.com");

        System.out.println("Searching SUVs: ");
        List<Car> suvs = system.searchCars(CarType.SUV_5);
        for(Car car : suvs) {
            System.out.println(car);
        }

        PricingStrategy ps = new SurgePricing(1.8);
        Booking booking = new Booking(1, user, car5, 10, ps);

        if(booking != null) {
            System.out.println(booking);
        }
    }
}