package strategy;

/**
 * NoFineStrategy
 */
public class NoFineStrategy implements FineStrategy {

    @Override
    public double calculateFine(int lateDays) {
        return 0;
    }
}