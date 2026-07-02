import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

enum RoomType {
    SINGLE,
    DOUBLE,
    DELUXE,
    SUITE
}

enum RoomStatus {
    AVAIALABLE,
    RESERVED,
    OCCUPIED,
    MAINTENANCE
}

enum BookingStatus {
    CREATED,
    CONFIRMED,
    CANCELLED,
    COMPLETED
}

enum PaymentStatus {
    PENDING,
    SUCCESS,
    FAILED,
    REFUNDED
}

class Room {
    private String roomId;
    private RoomType roomType;
    private double price;

    private RoomStatus status;

    private List<Booking> bookings;

    private ReentrantLock lock;

    public Room(String roomId, RoomType roomType, double price) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.price = price;
        this.status = RoomStatus.AVAIALABLE;
        this.bookings = new ArrayList<>();
        this.lock = new ReentrantLock();
    }

    public boolean isAvailable(LocalDate in, LocalDate out) {
        for(Booking booking: bookings) {
            if(booking.overlaps(in, out)) {
                return false;
            }
        }

        return status == RoomStatus.AVAIALABLE;
    }

    public List<Booking> getBookings() {
        return bookings;
    }

    public double getPrice() {
        return price;
    }

    public ReentrantLock getLock() {
        return lock;
    }

    public RoomType getRoomType() {
        return roomType;
    }
}

class Hotel {
    private String hotelId;
    private String name;
    private String city;
    private double rating;

    private List<Room> rooms;

    public Hotel(String hotelId, String name, String city, double rating) {
        this.hotelId = hotelId;
        this.name = name;
        this.city = city;
        this.rating = rating;
        this.rooms = new ArrayList<>();
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public String getCity() {
        return city;
    }
    
    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public List<Room> getAvailablerooms(LocalDate in, LocalDate out) {
        List<Room> availableRooms = new ArrayList<>();
        
        for(Room room: rooms) {
            if(room.isAvailable(in, out)) {
                availableRooms.add(room);
            }
        }

        return availableRooms;
    }
}

class Booking {
    private String bookingId;
    private Customer customer;
    private Room room;
    private LocalDate in;
    private LocalDate out;
    private BookingStatus status;
    private Payment payment;
    
    public Booking(String bookingId, Customer customer, Room room, LocalDate in, LocalDate out, Payment payment) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.room = room;
        this.in = in;
        this.out = out;
        this.payment = payment;
        this.status = BookingStatus.CONFIRMED;
    }

    public boolean overlaps(LocalDate in, LocalDate out) {
        return in.isBefore(out) && out.isAfter(in);
    }

    public void cancel() {
        status = BookingStatus.CANCELLED;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public Room getRoom() {
        return room;
    }

    public Payment getPayment() {
        return payment;
    }

    public String getBookingId() {
        return bookingId;
    }
}

abstract class User {
    protected String id;
    protected String name;
    protected String email;

    public User(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

class Customer extends User {
    private List<Booking> bookings;

    public Customer(String id, String name, String email) {
        super(id, name, email);
        bookings = new ArrayList<>();
    }

    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    public List<Booking> getBookings() {
        return bookings;
    }
} 

class HotelOwner extends User {
    private List<Hotel> hotels;

    public HotelOwner(String id, String name, String email) {
        super(id, name, email);
        hotels = new ArrayList<>();
    }

    public void addHotel(Hotel hotel) {
        hotels.add(hotel);
    }

    public List<Hotel> getHotels() {
        return hotels;
    }
}

class Admin extends User {
    public Admin(String id, String name, String email) {
        super(id, name, email);
    }
}

interface PaymentStrategy {
    boolean pay(double amount);
}

class UpiPayment implements PaymentStrategy {
    private String upiId;

    public UpiPayment(String id) {
        this.upiId = id;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("Paid using UPI: " + amount);
        return true;
    }
}

class CardPayment implements PaymentStrategy {
    private String cardNo;

    public CardPayment(String no) {
        this.cardNo = no;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("Paid using card: " + amount);
        return true;
    }
}

class NetBankingPayment implements PaymentStrategy {
    private String bankName;

    public NetBankingPayment(String bankName) {
        this.bankName = bankName;
    }

    @Override
    public boolean pay(double amount) {
        System.out.println("Paid using net banking: " + amount + " via " + bankName);
        return true;
    }
}

class Payment {
    private PaymentStrategy paymentStrategy;
    private PaymentStatus paymentStatus;

    public Payment(PaymentStrategy paymentStrategy) {
        this.paymentStrategy = paymentStrategy;
        paymentStatus = PaymentStatus.PENDING;
    }

    public boolean pay(double amount) {
        boolean success = paymentStrategy.pay(amount);
        paymentStatus = success ? PaymentStatus.SUCCESS : PaymentStatus.FAILED;

        return success;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }
}

interface PricingStrategy {
    double calculatePrice(Room room, LocalDate in, LocalDate out);
}

class RegularPricing implements PricingStrategy {
    @Override
    public double calculatePrice(Room room, LocalDate in, LocalDate out) {
        long days = ChronoUnit.DAYS.between(in, out);
        return days * room.getPrice();
    }
}

class WeekendPricing implements PricingStrategy {
    @Override
    public double calculatePrice(Room room, LocalDate in, LocalDate out) {
        double total = 0;
        LocalDate date = in;

        while(date.isBefore(out)) {
            double price = room.getPrice();
            if(date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
                price *= 1.20;
            }

            total += price;
            date = date.plusDays(1);
        }

        return total;
    }
}

class DiscountPricing implements PricingStrategy {
    private double discountPercentage;

    public DiscountPricing(double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    @Override
    public double calculatePrice(Room room, LocalDate in, LocalDate out) {
        long days = out.toEpochDay() - in.toEpochDay();
        double total = days * room.getPrice();

        return total * (100 - discountPercentage) / 100;
    }
}

interface SearchStrategy {
    List<Hotel> search(List<Hotel> hotels);
}

class SearchByCity implements SearchStrategy {
    private String city;

    public SearchByCity(String city) {
        this.city = city;
    }

    @Override
    public List<Hotel> search(List<Hotel> hotels) {
        List<Hotel> result = new ArrayList<>();
        for(Hotel hotel: hotels) {
            if(hotel.getCity().equalsIgnoreCase(city)) {
                result.add(hotel);
            }
        }

        return result;
    }
}

class SearchByRating implements SearchStrategy {
    private double minRating;

    public SearchByRating(double minRating) {
        this.minRating = minRating;
    }

    @Override
    public List<Hotel> search(List<Hotel> hotels) {
        List<Hotel> result = new ArrayList<>();
        for(Hotel hotel: hotels) {
            if(hotel.getRating() >= minRating) {
                result.add(hotel);
            }
        }

        return result;
    }
}

class SearchByRoomType implements SearchStrategy {
    private RoomType roomType;

    public SearchByRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    @Override
    public List<Hotel> search(List<Hotel> hotels) {
        List<Hotel> result = new ArrayList<>();
        for(Hotel hotel: hotels) {
            for(Room room: hotel.getRooms()) {
                if(room.getRoomType() == roomType) {
                    result.add(hotel);
                    break;
                }
            }
        }

        return result;
    }
}

class PaymentFactory {
    public static Payment createUpiPayment(String upiId) {
        return new Payment(new UpiPayment(upiId));
    }

    public static Payment createCardPayment(String cardNumber) {
        return new Payment(new CardPayment(cardNumber));
    }

    public static Payment createNetBanking(String bankName) {
        return new Payment(new NetBankingPayment(bankName));
    }
}

class HotelRepository {
    private final Map<String, Hotel> hotels = new HashMap<>();

    public void addHotel(Hotel hotel) {
        hotels.put(hotel.getName(), hotel);
    }

    public Hotel getHotel(String hotelName) {
        return hotels.get(hotelName);
    }

    public List<Hotel> getAllHotels() {
        return new ArrayList<>(hotels.values());
    }

    public void removeHotel(String hotelName) {
        hotels.remove(hotelName);
    }
}

class BookingRepository {
    private final Map<String, Booking> bookings = new HashMap<>();

    public void save(Booking booking) {
        bookings.put(booking.getBookingId(), booking);
    }

    public Booking getBooking(String bookingId) {
        return bookings.get(bookingId);
    }

    public void delete(String bookingId) {
        bookings.remove(bookingId);
    }

    public Collection<Booking> getAllBookings() {
        return bookings.values();
    }
}

class SearchService {
    public List<Hotel> search(List<Hotel> hotels, SearchStrategy strategy) {
        return strategy.search(hotels);
    }
}

class HotelService {
    private HotelRepository hotelRepository;
    
    public HotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }

    public void addHotel(Hotel hotel) {
        hotelRepository.addHotel(hotel);
    }

    public Hotel getHotel(String hotelName) {
        return hotelRepository.getHotel(hotelName);
    }

    public List<Room> getAvailableRooms(String hotelName, LocalDate in, LocalDate out) {
        Hotel hotel = hotelRepository.getHotel(hotelName);
        if(hotel == null) {
            return List.of();
        }

        return hotel.getAvailablerooms(in, out);
    }
}

class BookingService {
    private static BookingService instance;
    private BookingRepository bookingRepository;

    public BookingService() {
        bookingRepository = new BookingRepository();
    }

    public static BookingService getInstance() {
        if(instance == null) {
            instance = new BookingService();
        }

        return instance;
    }

    public Booking bookRoom(Customer customer, Room room, LocalDate in, LocalDate out, PricingStrategy pricingStrategy, Payment payment) {
        room.getLock().lock();
        try {
            if(!room.isAvailable(in, out)) {
                throw new RuntimeException("Room not available");
            }

            double amount = pricingStrategy.calculatePrice(room, in, out);
            if(!payment.pay(amount)) {
                throw new RuntimeException("Payment failed");
            }

            Booking booking = new Booking(UUID.randomUUID().toString(), customer, room, in, out, payment);
            room.getBookings().add(booking);
            customer.addBooking(booking);
            bookingRepository.save(booking);

            return booking;
        } finally {
            room.getLock().unlock();
        }
    }

    public void cancelBooking(String bookingId) {
        Booking booking = bookingRepository.getBooking(bookingId);

        if(booking == null) {
            return;
        }

        booking.cancel();
        booking.getRoom().getBookings().remove(booking);
    }
}

public class Main {
    public static void main(String[] args) {
        HotelRepository hotelRepository = new HotelRepository();
        HotelService hotelService = new HotelService(hotelRepository);
        BookingService bookingService = BookingService.getInstance();

        Hotel hotel = new Hotel("H1", "Taj", "Delhi", 4.9);
        Room room101 = new Room("101", RoomType.DELUXE, 5000);
        Room room102 = new Room("102", RoomType.SUITE, 8000);

        hotel.addRoom(room101);
        hotel.addRoom(room102);

        hotelService.addHotel(hotel);

        Customer customer = new Customer("C1", "Mokshe", "abc@gmail.com");

        Payment payment = PaymentFactory.createUpiPayment("mokshe@upi");

        Booking booking = bookingService.bookRoom(customer, room102, LocalDate.of(2026, 7, 5), LocalDate.of(2026, 7, 8), new RegularPricing(), payment);

        System.out.println();

        System.out.println("Booking success");
        System.out.println(booking.getBookingId());
    }
}