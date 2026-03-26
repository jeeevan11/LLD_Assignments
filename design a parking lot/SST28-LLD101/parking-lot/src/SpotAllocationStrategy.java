import java.util.List;
import java.util.Optional;

public interface SpotAllocationStrategy {
    Optional<ParkingSpot> allocateSpot(List<ParkingSpot> spots, VehicleType vehicleType, Gate entryGate);
}
