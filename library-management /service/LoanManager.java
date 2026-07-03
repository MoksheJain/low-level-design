package service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import model.Loan;
import model.Member;

public class LoanManager {
    
    public double calculateFine(Loan loan) {
        LocalDate today = LocalDate.now();
        long lateDays = ChronoUnit.DAYS.between(loan.getDueDate(), today);

        if(lateDays < 0) {
            lateDays = 0;
        }

        Member member = loan.getMember();
        return member.getFineStrategy().calculateFine((int)lateDays);
    }
}
