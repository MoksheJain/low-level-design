import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

class Product {
    private final String code;
    private final String name;
    private int price;

    public Product(String code, String name, int price) {
        this.code = code;
        this.name = name;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}

class Item {
    private Product product;
    private int qty;

    public Item(Product product, int qty) {
        this.product = product;
        this.qty = qty;
    }

    public Product getProduct() {
        return product;
    }

    public int getQty() {
        return qty;
    }

    public void addQty(int q) {
        qty += q;
    }

    public void reduceQty() {
        qty--;
    }
}

enum Coin {
    ONE(1),
    TWO(2),
    THREE(3),
    TEN(10);

    private final int value;

    Coin(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

enum Note {
    TEN(10),
    TWENTY(20),
    FIFTY(50),
    HUNDRED(100),
    TWO_HUNDRED(200),
    FIVE_HUNDRED(500);

    private final int value;

    Note(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}

class Inventory {
    private final Map<String, Item> items = new HashMap<>();

    public void addProduct(Product product, int qty) {
        if(items.containsKey(product.getCode())) {
            items.get(product.getCode()).addQty(qty);
        }
        else {
            items.put(product.getCode(), new Item(product, qty));
        }
    }

    public boolean isAvailable(String code) {
        return items.containsKey(code) && items.get(code).getQty() > 0;
    }

    public Item getItem(String code) {
        return items.get(code);
    }

    public void dispense(String code) {
        items.get(code).reduceQty();
    }

    public void printInventory() {
        for(Item item: items.values()) {
            System.out.println(item.getProduct().getName() + " Price = " + item.getProduct().getPrice() + " Quantity = " + item.getQty());
        }
    }
}

interface State {
    void insertMoney(int amount);
    void selectProduct(String code);
    void cancelTransaction();
}

class IdleState implements State {
    private final VendingMachine machine;

    public IdleState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertMoney(int amount) {
        machine.addMoney(amount);
        System.out.println("Inserted " + amount);
        machine.setState(machine.getHasMoneyState());
    }

    @Override
    public void selectProduct(String code) {
        System.out.println("Insert money first");
    }

    @Override
    public void cancelTransaction() {
        System.out.println("No transaction to cancel");
    }
}

class HasMoneyState implements State {
    private final VendingMachine machine;

    public HasMoneyState(VendingMachine machine) {
        this.machine = machine;
    }

    @Override
    public void insertMoney(int amount) {
        machine.addMoney(amount);
        System.out.println("Inserted " + amount);
    }

    @Override
    public void selectProduct(String code) {
        machine.dispenseProduct(code);
    }

    @Override
    public void cancelTransaction() {
        int refund = machine.getCurrentAmount();
        machine.resetTransaction();
        System.out.println("Refunded amount = " + refund);
        machine.setState(machine.getIdleState());
    }
}

class VendingMachine {
    private static final VendingMachine INSTANCE = new VendingMachine();
    private final Inventory inventory;
    private final State idleState;
    private final State hasMoneyState;
    private State currentState;
    private int currentAmount;
    private int totalCash;
    private final ReentrantLock lock = new ReentrantLock();
    
    private VendingMachine() {
        inventory = new Inventory();
        idleState = new IdleState(this);
        hasMoneyState = new HasMoneyState(this);
        currentState = idleState;
    }

    public static VendingMachine getInstance() {
        return INSTANCE;
    }

    public void insertCoin(Coin coin) {
        currentState.insertMoney(coin.getValue());
    }

    public void insertNote(Note note) {
        currentState.insertMoney(note.getValue());
    }

    public void selectProduct(String code) {
        currentState.selectProduct(code);
    }

    public void cancelTransaction() {
        currentState.cancelTransaction();
    }

    public void dispenseProduct(String code) {
        lock.lock();
        try {
            if(!inventory.isAvailable(code)) {
                System.out.println("Product out of stock");
                return;
            }
            Item item = inventory.getItem(code);
            Product product = item.getProduct();
            int price = product.getPrice();
            if(currentAmount < price) {
                System.out.println("Insufficient Amount. Need " + (price - currentAmount));
                return;
            }
            inventory.dispense(code);
            System.out.println("Dispensing " + product.getName());
            int change = currentAmount - price;
            if(change > 0) {
                System.out.println("Returning change = " + change);
            }
            totalCash += price;
            resetTransaction();
            currentState = idleState;
        }
        finally {
            lock.unlock();
        }
    }

    public void addMoney(int amount) {
        currentAmount += amount;
    }

    public int getCurrentAmount() { 
        return currentAmount;
    }

    public void resetTransaction() {
        currentAmount = 0;
    }

    public void setState(State state) {
        currentState = state;
    }

    public State getIdleState() {
        return idleState;
    }

    public State getHasMoneyState() {
        return hasMoneyState;
    }

    public void addProduct(Product product, int qty) {
        inventory.addProduct(product, qty);
    }

    public void refillProduct(String code, int qty) {
        Item item = inventory.getItem(code);
        if(item != null) {
            item.addQty(qty);
        }
    }

    public void updatePrice(String code, int newPrice) {
        Item item = inventory.getItem(code);
        if(item != null) {
            item.getProduct().setPrice(newPrice);
        }
    }

    public void collectCash() {
        System.out.println("Admin collected " + totalCash);
        totalCash = 0;
    }

    public void showInventory() {
        inventory.printInventory();
    }
}

public class Main {
    public static void main(String[] args) {
        VendingMachine vendingMachine = VendingMachine.getInstance();
        
        Product Coke = new Product("COKE", "Coke", 30);
        Product Pepsi = new Product("PEPSI", "Pepsi", 40);
        
        vendingMachine.addProduct(Coke, 20);
        vendingMachine.addProduct(Pepsi, 20);

        vendingMachine.showInventory();

        System.out.println();

        vendingMachine.insertCoin(Coin.TEN);
        vendingMachine.insertCoin(Coin.TEN);
        vendingMachine.insertNote(Note.TEN);

        vendingMachine.selectProduct("COKE");

        System.out.println();

        vendingMachine.cancelTransaction();

        vendingMachine.refillProduct("COKE", 10);

        vendingMachine.collectCash();
    }
}