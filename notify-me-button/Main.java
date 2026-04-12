import java.util.List;

class User {
    private String name;
    private Long id;
    private String email;
    private String phoneNo; 
    public String getEmail() {
        return email;
    }
    public String getPhone() {
        return phoneNo;
    }
    public Long getId() {
        return id;
    }
}

class Product {
    private Long id;
    private String name;
    private int stock;
    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public boolean isAvailable() {
        return stock > 0;
    }
    public void setStock(int s) {
        this.stock = s;
    }
}

enum SubscriptionStatus {
    ACTIVE,
    NOTIFIED,
    UNSUBSCRIBED
}

enum NotificationChannel {
    EMAIL,
    SMS,
    PUSH
}

class Subscription {
    private Long id;
    private Long userId;
    private Long productId;
    private NotificationChannel channel;
    private SubscriptionStatus status;
    public Long getUserId() {
        return userId;
    }
    public Long getProductId() {
        return productId;
    }
    public NotificationChannel getChannel() {
        return channel;
    }
    public SubscriptionStatus getStatus() {
        return status;
    }
    public void setStatus(SubscriptionStatus s) {
        this.status = s;
    }
}

interface NotificationSender {
    void send(User user, Product product);
}

class EmailNotificationSender implements NotificationSender {
    public void send(User user, Product product) {
        System.out.println("Email sent to " + user.getEmail());
    }
}

class SMSNotificationSender implements NotificationSender {
    public void send(User user, Product product) {
        System.out.println("SMS sent to " + user.getPhone());
    }
}

class PushNotificationSender implements NotificationSender {
    public void send(User user, Product product) {
        System.out.println("Push notification sent to " + user.getId());
    }
}

class NotificationFactory {
    public static NotificationSender getSender(NotificationChannel channel) {
        switch (channel) {
            case EMAIL: return new EmailNotificationSender();
            case SMS: return new SMSNotificationSender();
            case PUSH: return new PushNotificationSender();
            default: throw new IllegalArgumentException("Invalid channel");
        }
    }
}

interface SubscriptionRepository {
    void save(Subscription sub);
    List<Subscription> findActiveByProductId(Long productId);
    boolean exists(Long userId, Long productId);
}

interface ProductRepository {
    Product findById(Long id);
    void save(Product product);
}

interface UserRepository {
    User findById(Long id);
}

class NotificationService {
    private SubscriptionRepository subscritpionRepo;
    private UserRepository userRepo;
    public void notifyUsers(Long productId) {
        List<Subscription> subs = subscritpionRepo.findActiveByProductId(productId);
        for(Subscription sub : subs) {
            User user = userRepo.findById(sub.getUserId());
            NotificationSender sender = NotificationFactory.getSender(sub.getChannel());
            sender.send(user, null);
            sub.setStatus(SubscriptionStatus.NOTIFIED);
        }
    }
}

class SubscriptionService {
    private SubscriptionRepository repo;
    public void subscribe(Long userId, Long productId, NotificationChannel channel) {
        if(repo.exists(userId, productId)) {
            return;
        }
        Subscription sub = new Subscription();
        sub.setStatus(SubscriptionStatus.ACTIVE);
        // set other attributes here
        repo.save(sub);
    }
    public void unsubscribe(Long userId, Long productId) {
        // fetch and update status
    }
}

class ProductService {
    private ProductRepository productRepo;
    private NotificationService notificationService;
    public void updateStock(Long productId, int newStock) {
        Product product = productRepo.findById(productId);
        boolean wasOutOfStock = !product.isAvailable();
        product.setStock(newStock);
        productRepo.save(product);
        if(wasOutOfStock && product.isAvailable()) {
            notificationService.notifyUsers(productId);
        }
    }
}