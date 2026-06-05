import java.util.concurrent.locks.ReentrantLock;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

enum SeatStatus {
    AVAILABLE,
    LOCKED,
    BOOKED
};

enum BookingStatus {
    CREATED,
    PAYMENT_PENDING,
    CONFIRMED,
    CANCELLED,
    EXPIRED
};

class Seat {
    private final String seatId;

    public Seat(String seatId) {
        this.seatId = seatId;
    }

    public String getSeatId() {
        return seatId;
    }
}

class ShowSeat {
    private final Seat seat;
    private SeatStatus status;
    private final ReentrantLock lock;

    public ShowSeat(Seat seat) {
        this.seat = seat;
        this.status = SeatStatus.AVAILABLE;
        this.lock = new ReentrantLock();
    }

    public Seat getSeat() {
        return seat;
    }

    public SeatStatus getSeatStatus() {
        return status;
    }

    public void setSeatStatus(SeatStatus status) {
        this.status = status;
    }

    public ReentrantLock getLock() {
        return lock;
    }
}

class User {
    private final String userId;
    private final String name;

    public User(String userId, String name) {
        this.userId = userId;
        this.name = name;
    }

    public String getName() {
        return name;
    }
}

class Show {
    private final String showId;
    private final Map<String, ShowSeat> seats = new HashMap<>();

    public Show(String showId, List<Seat> seatList) {
        this.showId = showId;
        for(Seat seat: seatList) {
            seats.put(seat.getSeatId(), new ShowSeat(seat));
        }
    }

    public ShowSeat getSeat(String seatId) {
        return seats.get(seatId);
    }

    public String getShowId() {
        return showId;
    }
}

class Booking {
    private final String bookingId;
    private final User user;
    private final List<ShowSeat> seats;
    private BookingStatus status;
    
    public Booking(String bookingId, User user, List<ShowSeat> seats) {
        this.bookingId = bookingId;
        this.user = user;
        this.seats = seats;
        this.status = BookingStatus.CREATED;
    }

    public String getBookingId() {
        return bookingId;
    }

    public List<ShowSeat> getSeats() {
        return seats;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }
}

class SeatLockProvider {
    private static class LockInfo {
        User user;
        long expiryTime;

        LockInfo(User user, long expiryTime) {
            this.user = user;
            this.expiryTime = expiryTime;
        }
    }

    private final Map<ShowSeat, LockInfo> lockedSeats = new ConcurrentHashMap<>();
    private final long lockDurationMilliseconds;

    public SeatLockProvider(long lockDurationMilliseconds) {
        this.lockDurationMilliseconds = lockDurationMilliseconds;
    }

    public boolean lockSeat(ShowSeat seat, User user) {
        synchronized (seat) {
            if(seat.getSeatStatus() != SeatStatus.AVAILABLE) {
                return false;
            }
            seat.setSeatStatus(SeatStatus.AVAILABLE);
            lockedSeats.put(seat, new LockInfo(user, lockDurationMilliseconds));
            return true;
        }
    }

    public void unlockSeat(ShowSeat seat) {
        synchronized (seat) {
            seat.setSeatStatus(SeatStatus.AVAILABLE);
            lockedSeats.remove(seat);
        }
    }

    public boolean isLockExpired(ShowSeat seat) {
        LockInfo info = lockedSeats.get(seat);
        if(info == null) {
            return false;
        }
        return System.currentTimeMillis() > info.expiryTime;
    }

    public void releaseExpiredLock(ShowSeat seat) {
        if(!isLockExpired(seat)) {
            unlockSeat(seat);
        }
    }
}

class PaymentService {
    public boolean pay(Booking booking) {
        try {
            Thread.sleep(1000);
        } catch (Exception ignored) {}
        return true;
    }
}

class BookingService {
    private final SeatLockProvider seatLockProvider;
    private final PaymentService paymentService;

    public BookingService(SeatLockProvider seatLockProvider, PaymentService paymentService) {
        this.seatLockProvider = seatLockProvider;
        this.paymentService = paymentService;
    }

    public Booking createBooking(User user, Show show, List<String> seatIds) {
        List<ShowSeat> selectedSeats = new ArrayList<>();
        for(String seatId: seatIds) {
            ShowSeat seat = show.getSeat(seatId);
            seat.getLock().lock();
            
            try {
                seatLockProvider.releaseExpiredLock(seat);
                if(!seatLockProvider.lockSeat(seat, user)) {
                    for(ShowSeat bookedSeat: selectedSeats) {
                        seatLockProvider.unlockSeat(bookedSeat);
                    }
                    throw new RuntimeException(seatId + " already booked");
                }
                selectedSeats.add(seat);
            }
            
            finally {
                seat.getLock().unlock();
            }
        }

        Booking booking = new Booking(UUID.randomUUID().toString(), user, selectedSeats);
        booking.setStatus(BookingStatus.PAYMENT_PENDING);
        boolean paymentSuccess = paymentService.pay(booking);
        
        if(paymentSuccess) {
            for(ShowSeat seat: selectedSeats) {
                seat.setSeatStatus(SeatStatus.BOOKED);
            }
            booking.setStatus(BookingStatus.CONFIRMED);
        }

        else {
            for(ShowSeat seat: selectedSeats) {
                seatLockProvider.unlockSeat(seat);
            }
            booking.setStatus(BookingStatus.CANCELLED);
        }

        return booking;
    }
}

public class Main {
    public static void main(String[] args) {
        Seat seatA1 = new Seat("A1");
        Seat seatA2 = new Seat("A2");
        
        Show show = new Show("Show1", List.of(seatA1, seatA2));
        
        User user1 = new User("1", "Alice");
        User user2 = new User("2", "Bob");
        
        SeatLockProvider lockProvider = new SeatLockProvider(5000);
        
        BookingService bookingService = new BookingService(lockProvider, new PaymentService());
        
        Runnable task1 = () -> {

            try {
                Booking booking = bookingService.createBooking(user1, show, List.of("A1"));
                System.out.println(user1.getName() + " booking success " + booking.getStatus());
            } catch (Exception e) {
                System.out.println(user1.getName() + " failed : " + e.getMessage());
            }
        };

        Runnable task2 = () -> {

            try {
                Booking booking = bookingService.createBooking(user2, show, List.of("A1"));
                System.out.println(user2.getName() + " booking success " + booking.getStatus());
            } catch (Exception e) {
                System.out.println(user2.getName() + " failed : " + e.getMessage());
            }
        };

        new Thread(task1).start();
        new Thread(task2).start();
    }
}