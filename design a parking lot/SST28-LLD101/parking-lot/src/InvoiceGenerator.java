import java.time.Duration;
import java.time.LocalDateTime;

public class InvoiceGenerator {
    private final RateStrategy rateStrategy;

    public InvoiceGenerator(RateStrategy rateStrategy) {
        this.rateStrategy = rateStrategy;
    }

    public Invoice createInvoice(ParkingPass pass, LocalDateTime exitTime) {
        long minutes = Duration.between(pass.getEntryTime(), exitTime).toMinutes();
        long hours = (minutes + 59) / 60;
        if (hours == 0) hours = 1;

        int rate = rateStrategy.getHourlyRate(pass.getSpot().getType());
        int charge = (int) (hours * rate);

        return new Invoice(pass, exitTime, hours, charge);
    }
}
