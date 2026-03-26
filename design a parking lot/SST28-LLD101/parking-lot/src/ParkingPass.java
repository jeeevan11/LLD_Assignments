import java.time.LocalDateTime;

public class ParkingPass {
    private final String passId;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final LocalDateTime entryTime;

    public ParkingPass(String passId, Vehicle vehicle, ParkingSpot spot, LocalDateTime entryTime) {
        this.passId = passId;
        this.vehicle = vehicle;
        this.spot = spot;
        this.entryTime = entryTime;
    }

    public String getPassId() { return passId; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSpot getSpot() { return spot; }
    public LocalDateTime getEntryTime() { return entryTime; }
}
