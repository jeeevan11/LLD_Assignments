import java.time.LocalDateTime;

public class Invoice {
    private final ParkingPass pass;
    private final LocalDateTime exitTime;
    private final long duration;
    private final int charge;

    public Invoice(ParkingPass pass, LocalDateTime exitTime, long duration, int charge) {
        this.pass = pass;
        this.exitTime = exitTime;
        this.duration = duration;
        this.charge = charge;
    }

    public ParkingPass getPass() { return pass; }
    public LocalDateTime getExitTime() { return exitTime; }
    public long getDuration() { return duration; }
    public int getCharge() { return charge; }
}
