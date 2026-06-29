package truecaller;
import java.util.*;

class User {
    private String id;
    private String name;
    private String phoneNumber;

    private List<Contact> contacts;
    private Set<String> blockedNumbers;

    public User(String id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;

        this.contacts = new ArrayList<>();
        this.blockedNumbers = new HashSet<>();
    }

    public void addContact(Contact contact) {
        contacts.add(contact);
    }

    public void blockNumber(String phoneNumber) {
        blockedNumbers.add(phoneNumber);
    }

    public boolean isBlocked(String phoneNumber) {
        return blockedNumbers.contains(phoneNumber);
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getName() {
        return name;
    }

    public List<Contact> getContacts() {
        return contacts;
    }
}

class Contact {
    private String name;
    private String phoneNumber;

    public Contact(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}

class CallerProfile {
    private String phone;
    private String displayName;
    private int spamCount;
    private boolean registered;

    public CallerProfile(String phone, String displayName, boolean registered) {
        this.phone = phone;
        this.displayName = displayName;
        this.registered = registered;
    }

    public void increaseSpam() {
        spamCount++;
    }

    public int getSpamCount() {
        return spamCount;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getPhoneNumber() {
        return phone;
    }

    public boolean isRegistered() {
        return registered;
    }
}

class UserRepository {
    private Map<String, User> users = new HashMap<>();

    public void save(User user) {
        users.put(user.getPhoneNumber(), user);
    }

    public User find(String phone) {
        return users.get(phone);
    }

    public Collection<User> getAllUsers() {
        return users.values();
    }
}

class CallerRepository {
    private Map<String, CallerProfile> profiles = new HashMap<>();

    public CallerProfile find(String phone) {
        return profiles.get(phone);
    }

    public void save(CallerProfile profile) {
        profiles.put(profile.getPhoneNumber(), profile);
    }
}

class TruecallerService {
    private UserRepository userRepository;
    private CallerRepository callerRepository;

    public TruecallerService(UserRepository userRepository, CallerRepository callerRepository) {
        this.userRepository = userRepository;
        this.callerRepository = callerRepository;
    }

    public void registerUser(String id, String name, String phone) {
        User user = new User(id, name, phone);
        userRepository.save(user);

        CallerProfile profile = new CallerProfile(phone, name, true);
        callerRepository.save(profile);
    }

    public CallerProfile searchNumber(String phone) {
        return callerRepository.find(phone);
    }

    public void uploadContacts(String phone, List<Contact> contacts) {
        User user = userRepository.find(phone);
        for(Contact con: contacts) {
            user.addContact(con);
            if(callerRepository.find(con.getPhoneNumber()) == null) {
                callerRepository.save(new CallerProfile(con.getPhoneNumber(), con.getName(), false));
            }
        }
    }

    public void reportSpam(String phone) {
        CallerProfile profile = callerRepository.find(phone);
        if(profile != null) {
            profile.increaseSpam();
        }
    }

    public List<CallerProfile> searchByName(String name) {
        List<CallerProfile> res = new ArrayList<>();
        for(User u: userRepository.getAllUsers()) {
            if(u.getName().toLowerCase().contains(name.toLowerCase())) {
                res.add(callerRepository.find(u.getPhoneNumber()));
            }
        }
        return res;
    }
}

public class Main {
    public static void main(String[] args) {
        UserRepository userRepository = new UserRepository();
        CallerRepository callerRepository = new CallerRepository();
        
        TruecallerService service = new TruecallerService(userRepository, callerRepository);
        service.registerUser("1", "Rahul", "999999999");

        List<Contact> contacts = new ArrayList<>();

        contacts.add(new Contact("Amit", "8930382040"));
        contacts.add(new Contact("Neha", "93038204931"));

        service.uploadContacts("999999999", contacts);

        service.reportSpam("8930382040");
        service.reportSpam("8930382040");

        CallerProfile profile = service.searchNumber("8930382040");

        System.out.println(profile.getDisplayName());
        System.out.println(profile.getSpamCount());
    }
}