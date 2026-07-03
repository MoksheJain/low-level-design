package strategy;

/**
 * StandardFineStrategy
 */
public class StandardFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(int lateDays) {
        if(lateDays <= 0) {
            return 0;
        }
        return lateDays * 10;
    }
}