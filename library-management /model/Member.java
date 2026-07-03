package model;

import java.util.ArrayList;
import java.util.List;

import strategy.FineStrategy;

public class Member extends User {
    
    private List<Loan> activeLoans;
    public static final int MAX_BOOKS = 5;
    private FineStrategy fineStrategy;

    public Member(String id, String name, String email, FineStrategy fineStrategy) {
        super(id, name, email);
        this.fineStrategy = fineStrategy;
        activeLoans = new ArrayList<>();
    }

    public boolean canBorrow() {
        return activeLoans.size() < MAX_BOOKS;
    }

    public void addLoan(Loan loan) {
        activeLoans.add(loan);
    }

    public void removeLoan(Loan loan) {
        activeLoans.remove(loan);
    }

    public List<Loan> getActiveLoans() {
        return activeLoans;
    }

    public FineStrategy getFineStrategy() {
        return fineStrategy;
    }

    public void setFineStrategy(FineStrategy fineStrategy) {
        this.fineStrategy = fineStrategy;
    }
}