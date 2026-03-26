import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class SpotCompatibility {

    private static final Map<VehicleType, List<SpotType>> ALLOWED = new EnumMap<>(VehicleType.class);

    static {
        ALLOWED.put(VehicleType.TWO_WHEELER, List.of(SpotType.SMALL, SpotType.MEDIUM, SpotType.LARGE));
        ALLOWED.put(VehicleType.CAR,         List.of(SpotType.MEDIUM, SpotType.LARGE));
        ALLOWED.put(VehicleType.BUS,         List.of(SpotType.LARGE));
    }

    public static List<SpotType> getAllowedSpots(VehicleType vehicleType) {
        List<SpotType> allowed = ALLOWED.get(vehicleType);
        if (allowed == null) {
            throw new IllegalArgumentException("Unknown vehicle type: " + vehicleType);
        }
        return allowed;
    }
}
