package splitwise;
import java.util.*;

class User {
    private final String id;
    private final String name;

    public User(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}

class Group {
    private final String id;
    private final String name;
    private final List<User> members;

    public Group(String id, String name) {
        this.id = id;
        this.name = name;
        this.members = new ArrayList<>();
    }

    public void addMember(User user) {
        members.add(user);
    }

    public List<User> getMembers() {
        return members;
    }
}

class Split {
    private User user;
    private double amount;

    public Split(User user, double amount) {
        this.user = user;
        this.amount = amount;
    }

    public User getUser() {
        return user;
    }

    public double getAmount() {
        return amount;
    }
}

interface SplitStrategy {
    List<Split> calculateSplits(double totalAmount, List<User> participants);
}

class EqualSplitStrategy implements SplitStrategy {
    @Override
    public List<Split> calculateSplits(double totalAmount, List<User> participants) {
        List<Split> splits = new ArrayList<>();
        double share = totalAmount / participants.size();
        for(User user: participants) {
            splits.add(new Split(user, share));
        }
        return splits;
    }
}

class CustomSplitStrategy implements SplitStrategy {
    private final Map<User, Double> userAmounts;
    
    public CustomSplitStrategy(Map<User, Double> userAmounts) {
        this.userAmounts = userAmounts;
    }

    @Override
    public List<Split> calculateSplits(double totalAmount, List<User> participants) {
        double sum = 0; 
        for(double amount: userAmounts.values()) {
            sum += amount;
        }
        if(Math.abs(sum - totalAmount) > 0.001) {
            throw new IllegalArgumentException("Split amount mismatch");
        }
        List<Split> splits = new ArrayList<>();
        for(Map.Entry<User, Double> entry: userAmounts.entrySet()) {
            splits.add(new Split(entry.getKey(), entry.getValue()));
        }
        return splits;
    }
}

class Expense {
    private final User paidBy;
    private final double amount;
    private final List<Split> splits;

    public Expense(User paidBy, double amount, List<Split> splits) {
        this.paidBy = paidBy;
        this.amount = amount;
        this.splits = splits;
    }

    public User getPaidBy() {
        return paidBy;
    }

    public List<Split> getSplits() {
        return splits;
    }
}

class ExpenseManager {
    // debtor -> creditor -> amount
    private final Map<User, Map<User, Double>> balances = new HashMap<>();

    public void addExpense(Expense expense) {
        User payer = expense.getPaidBy();
        for(Split split: expense.getSplits()) {
            User participant = split.getUser();
            if(participant.equals(payer)) {
                continue;
            }
            double amount = split.getAmount();
            balances.computeIfAbsent(participant, k->new HashMap<>());
            balances.get(participant).merge(payer, amount, Double::sum);
        }
    }

    public void showBalances() {
        for(User debtor: balances.keySet()) {
            for(Map.Entry<User, Double> entry: balances.get(debtor).entrySet()) {
                System.out.println(debtor.getName() + " owes " + entry.getKey().getName() + " ₹" + entry.getValue());
            }
        }
    }
}

class Main {
    public static void main(String[] args) {
        User alice = new User("1", "Alice");
        User bob = new User("2", "Bob");
        User charlie = new User( "3", "Charlie");
        User dickon = new User("4", "Dickon");

        Group trip = new Group("1", "Trip");

        trip.addMember(dickon);
        trip.addMember(charlie);
        trip.addMember(bob);
        trip.addMember(alice);

        ExpenseManager manager = new ExpenseManager();
        
        // equal splits
        SplitStrategy equal = new EqualSplitStrategy();
        List<Split> equalSplits = equal.calculateSplits(2000, trip.getMembers());
        
        Expense e1 = new Expense(alice, 2000, equalSplits);

        manager.addExpense(e1);

        // custom splits
        Map<User, Double> customMap = new HashMap<>();
        customMap.put(bob, 450.0);
        customMap.put(dickon, 450.0);

        SplitStrategy customStrategy = new CustomSplitStrategy(customMap);

        List<Split> customSplits = customStrategy.calculateSplits(900, Arrays.asList(bob, dickon));

        Expense e2 = new Expense(dickon, 900, customSplits);

        manager.addExpense(e2);

        manager.showBalances();
    }
}