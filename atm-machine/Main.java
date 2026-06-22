interface ATMState {
    void insertCard(Card card);
    void enterPin(int pin);
    void checkBalance();
    void withdrawCash(double amount);
    void depositCash(double amount);
    void ejectCard();
}

class BankAccount {
    private String accountNumber;
    private double balance;

    public BankAccount(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public boolean withdraw(double amount) {
        if(amount > balance) {
            return false;
        }
        balance -= amount;
        return true;
    }

    public void depsoit(double amount) {
        balance += amount;
    }
}

class Card {
    private String cardNumber;
    private int pin;
    private BankAccount account;

    public Card(String cardNumber, int pin, BankAccount account) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.account = account;
    }

    public boolean validatePin(int eneteredPin) {
        return pin == eneteredPin;
    }

    public BankAccount getAccount() {
        return account;
    }
}

class ATM {
    private ATMState curr;
    private ATMState idleState;
    private ATMState hasCardState;
    private ATMState authenticatedState;
    private Card currentCard;

    public ATM() {
        idleState = new IdleState(this);
        hasCardState = new HasCardState(this);
        authenticatedState = new AuthenticatedState(this);
        curr = idleState;
    }

    public void setState(ATMState state) {
        curr = state;
    } 

    public ATMState getIdleState() {
        return idleState;
    }

    public ATMState getHasCardState() {
        return hasCardState;
    }

    public ATMState getAuthenticatedState() {
        return authenticatedState;
    }

    public Card getCurrentCard() {
        return currentCard;
    }

    public void setCurrentCard(Card card) {
        this.currentCard = card;
    }

    public void insertCard(Card card) {
        curr.insertCard(card);
    }

    public void enterPin(int pin) {
        curr.enterPin(pin);
    }

    public void checkBalance() {
        curr.checkBalance();
    }

    public void withdrawCash(double amount) {
        curr.withdrawCash(amount);
    }

    public void depositCash(double amount) {
        curr.depositCash(amount);
    }

    public void ejectCard() {
        curr.ejectCard();
    }
}

class IdleState implements ATMState {
    private ATM atm;

    public IdleState(ATM atm) {
        this.atm = atm;
    }

    @Override
    public void insertCard(Card card) {
        atm.setCurrentCard(card);
        System.out.println("Card inserted.");
        atm.setState(atm.getHasCardState());
    }

    @Override
    public void enterPin(int pin) {
        System.out.println("Insert card first");
    }

    @Override
    public void checkBalance() {
        System.out.println("Insert card first");
    }

    @Override
    public void withdrawCash(double amount) {
        System.out.println("Insert card first");
    }

    @Override
    public void depositCash(double amount) {
        System.out.println("Insert card first");
    }

    @Override
    public void ejectCard() {
        System.out.println("No card found");
    }
}

class HasCardState implements ATMState {
    private ATM atm;

    public HasCardState(ATM atm) {
        this.atm = atm;
    }

    @Override
    public void insertCard(Card card) {
        System.out.println("Card already present.");
    }

    @Override
    public void enterPin(int pin) {
        if(atm.getCurrentCard().validatePin(pin)) {
            System.out.println("PIN correct");
            atm.setState(atm.getAuthenticatedState());
        }
        else {
            System.out.println("Incorrect pin");
            ejectCard();
        }
    }

    @Override
    public void checkBalance() {
        System.out.println("Enter PIN first");
    }

    @Override
    public void withdrawCash(double amount) {
        System.out.println("Enter PIN first");
    }

    @Override
    public void depositCash(double amount) {
        System.out.println("Enter PIN first");
    }

    @Override
    public void ejectCard() {
        System.out.println("Card ejected");
        atm.setCurrentCard(null);
        atm.setState(atm.getIdleState());
    }
}

class AuthenticatedState implements ATMState {
    private ATM atm;

    public AuthenticatedState(ATM atm) {
        this.atm = atm;
    }

    @Override
    public void insertCard(Card card) {
        System.out.println("Card already inserted");
    }

    @Override
    public void enterPin(int pin) {
        System.out.println("Already authenticated");
    }

    @Override
    public void checkBalance() {
        double balance = atm.getCurrentCard().getAccount().getBalance();
        System.out.println("The balance of the account is: " + balance);
    }

    @Override
    public void withdrawCash(double amount) {
        BankAccount account = atm.getCurrentCard().getAccount();
        if(account.withdraw(amount)) {
            System.out.println("Please collect cash.");
            System.out.println("Remaining balance = " + account.getBalance());
        }
        else {
            System.out.println("Insufficient balance.");
        }
    }

    @Override
    public void depositCash(double amount) {
        BankAccount account = atm.getCurrentCard().getAccount();
        account.depsoit(amount);
        System.out.println("Amount deposited successfully.");
        System.out.println("Updated balance = " + account.getBalance());
    }

    @Override
    public void ejectCard() {
        System.out.println("Card ejected.");
        atm.setCurrentCard(null);
        atm.setState(atm.getIdleState());
    }
}

public class Main {
    public static void main(String[] args) {
        BankAccount account = new BankAccount("ACC101", 10000);
        Card card = new Card("1234567890", 1234, account);
        ATM atm = new ATM();
        atm.insertCard(card);
        atm.enterPin(1234);
        atm.checkBalance();
        atm.withdrawCash(483202);
        atm.depositCash(500);
        atm.withdrawCash(100);
        atm.checkBalance();
        atm.ejectCard();
    }
}