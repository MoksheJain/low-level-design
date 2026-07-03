package strategy;

/**
 * PremiumFineStrategy
 */
public class PremiumFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(int lateDays) {
        if(lateDays <= 0) {
            return 0;
        }
        return lateDays * 2;
    }
}