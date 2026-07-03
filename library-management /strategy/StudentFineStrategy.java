package strategy;

/**
 * StudentFineStrategy
 */
public class StudentFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(int lateDays) {
        if(lateDays <= 5) {
            return 0;
        }

        return (lateDays - 5) * 5;
    }
}