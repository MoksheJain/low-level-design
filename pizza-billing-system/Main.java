import java.util.*;

interface Pizza {
    String getDescription();
    double getCost();    
}

class Margherita implements Pizza {
    public String getDescription() {
        return "Margherita";
    }
    public double getCost() {
        return 200;
    }
}

class Farmhouse implements Pizza {
    public String getDescription() {
        return "Farmhouse";
    }
    public double getCost() {
        return 300;
    }
}

abstract class ToppingDecorator implements Pizza {
    protected Pizza pizza;
    public ToppingDecorator(Pizza pizza) {
        this.pizza = pizza;
    }
}

class Cheese extends ToppingDecorator {
    public Cheese(Pizza pizza) {
        super(pizza);
    }
    public String getDescription() {
        return pizza.getDescription() + ", Cheese";
    }
    public double getCost() {
        return pizza.getCost() + 50;
    }
}

class Olives extends ToppingDecorator {
    public Olives(Pizza pizza) {
        super(pizza);
    }
    public String getDescription() {
        return pizza.getDescription() + ", Olives";
    }
    public double getCost() {
        return pizza.getCost() + 30;
    }
}

class PizzaFactory {
    public static Pizza createPizza(String type) {
        switch (type.toLowerCase()) {
            case "margherita": return new Margherita();
            case "farmhouse": return new Farmhouse();
            default: throw new IllegalArgumentException("Invalid pizza type");
        }
    }
}

class OrderItem {
    private Pizza pizza;
    private int quantity;
    public OrderItem(Pizza pizza, int quantity) {
        this.pizza = pizza;
        this.quantity = quantity;
    }
    public double getTotalAmount() {
        return pizza.getCost() * quantity;
    }
    public String getDescription() {
        return pizza.getDescription() + " x " + quantity;
    }
}

class Cart {
    private List<OrderItem> items = new ArrayList<>();
    public void addItem(OrderItem item) {
        items.add(item);
    }
    public List<OrderItem> getItems() {
        return items;
    }
    public double getTotal() {
        return items.stream().mapToDouble(OrderItem::getTotalAmount).sum();
    }
}

interface PricingStrategy {
    double applyDiscount(double amount);
}

class NoDiscount implements PricingStrategy {
    public double applyDiscount(double amount) {
        return amount;
    }
}

class FlatDiscount implements PricingStrategy {
    private double discount;
    public FlatDiscount(double discount) {
        this.discount = discount;
    }
    public double applyDiscount(double amount) {
        return Math.max(0, amount-discount);
    }
}

class PercentageDiscount implements PricingStrategy {
    private double percentage;
    public PercentageDiscount(double percentage) {
        this.percentage = percentage;
    }
    public double applyDiscount(double amount) {
        return amount - (percentage / 100 * amount);
    }
}

class Bill {
    private Cart cart;
    private PricingStrategy strategy;
    private static final double TAX = 0.05;
    public Bill(Cart cart, PricingStrategy strategy) {
        this.cart = cart;
        this.strategy = strategy;
    }
    public void generateBill() {
        double subtotal = cart.getTotal();
        double discounted = strategy.applyDiscount(subtotal);
        double tax = discounted * TAX;
        double total = discounted + tax;
        System.out.println("------BILL------"); 
        for (OrderItem item : cart.getItems()) {
            System.out.println(item.getDescription() + " = £" + item.getTotalAmount());
        }
        System.out.println("Subtotal = £" + subtotal);
        System.out.println("After Discount = £" + discounted);
        System.out.println("Tax = £" + tax);
        System.out.println("Total = £" + total);
    }
}

public class Main {
    public static void main(String[] args)  {
        Pizza pizza1 = PizzaFactory.createPizza("margherita");
        pizza1 = new Cheese(pizza1);
        pizza1 = new Olives(pizza1);
        Pizza pizza2 = PizzaFactory.createPizza("farmhouse");
        pizza2 = new Cheese(pizza2);
        Cart cart = new Cart();
        cart.addItem(new OrderItem(pizza1, 1));
        cart.addItem(new OrderItem(pizza2, 2));
        PricingStrategy strategy = new FlatDiscount(10);
        Bill bill = new Bill(cart, strategy);
        bill.generateBill();
    }
}