import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class ParkingLotDemo {
    public static void main(String[] args) {
        System.out.println("===== Parking Lot Demo =====\n");

        RateStrategy pricing = new HourlyRateStrategy(Map.of(
                SpotType.SMALL,  10,
                SpotType.MEDIUM, 20,
                SpotType.LARGE,  50
        ));

        InvoiceGenerator invoiceGen = new InvoiceGenerator(pricing);
        SpotAllocationStrategy strategy = new NearestSpotStrategy();

        // Ground floor: 2 small, 2 medium, 1 large | First floor: 1 small, 1 medium, 1 large
        List<ParkingSpot> spots = List.of(
                new ParkingSpot(1, SpotType.SMALL,  0),
                new ParkingSpot(2, SpotType.SMALL,  0),
                new ParkingSpot(3, SpotType.MEDIUM, 0),
                new ParkingSpot(4, SpotType.MEDIUM, 0),
                new ParkingSpot(5, SpotType.LARGE,  0),
                new ParkingSpot(6, SpotType.SMALL,  1),
                new ParkingSpot(7, SpotType.MEDIUM, 1),
                new ParkingSpot(8, SpotType.LARGE,  1)
        );

        ParkingLot lot = new ParkingLot(spots, strategy, invoiceGen);

        Gate groundGate = new Gate(1, 0);
        Gate firstFloorGate = new Gate(2, 1);

        System.out.println("--- Vehicles entering ---");

        // Bike enters via ground gate → expects Spot 1 (SMALL, floor 0)
        Vehicle bike = new Vehicle("KA-01-1234", VehicleType.TWO_WHEELER);
        ParkingPass p1 = lot.parkVehicle(bike, groundGate, LocalDateTime.of(2026, 4, 5, 10, 0));
        System.out.println("Bike  in → " + p1.getPassId() + " | " + p1.getSpot());

        // Car enters via first-floor gate → expects Spot 7 (MEDIUM, floor 1, closest to floor 1)
        Vehicle car = new Vehicle("KA-02-5678", VehicleType.CAR);
        ParkingPass p2 = lot.parkVehicle(car, firstFloorGate, LocalDateTime.of(2026, 4, 5, 10, 15));
        System.out.println("Car   in → " + p2.getPassId() + " | " + p2.getSpot());

        // Bus enters via ground gate → expects Spot 5 (LARGE, floor 0)
        Vehicle bus = new Vehicle("KA-03-9999", VehicleType.BUS);
        ParkingPass p3 = lot.parkVehicle(bus, groundGate, LocalDateTime.of(2026, 4, 5, 11, 0));
        System.out.println("Bus   in → " + p3.getPassId() + " | " + p3.getSpot());

        System.out.println("\nAvailable spots after 3 entries: " + lot.availableSpots());

        System.out.println("\n--- Vehicles exiting ---");

        Invoice inv1 = lot.releaseVehicle(p1.getPassId(), LocalDateTime.of(2026, 4, 5, 12, 30));
        System.out.printf("Bike  out → %dh @ SMALL  = Rs.%d%n", inv1.getDuration(), inv1.getCharge());

        Invoice inv2 = lot.releaseVehicle(p2.getPassId(), LocalDateTime.of(2026, 4, 5, 14, 15));
        System.out.printf("Car   out → %dh @ MEDIUM = Rs.%d%n", inv2.getDuration(), inv2.getCharge());

        Invoice inv3 = lot.releaseVehicle(p3.getPassId(), LocalDateTime.of(2026, 4, 5, 12, 0));
        System.out.printf("Bus   out → %dh @ LARGE  = Rs.%d%n", inv3.getDuration(), inv3.getCharge());

        // Overflow test: fill both SMALL spots on ground floor, third bike overflows to MEDIUM
        System.out.println("\n--- Overflow test (bikes filling SMALL spots) ---");
        Vehicle bike2 = new Vehicle("KA-04-1111", VehicleType.TWO_WHEELER);
        Vehicle bike3 = new Vehicle("KA-04-2222", VehicleType.TWO_WHEELER);
        ParkingPass pa = lot.parkVehicle(bike2, groundGate, LocalDateTime.of(2026, 4, 5, 13, 0));
        ParkingPass pb = lot.parkVehicle(bike3, groundGate, LocalDateTime.of(2026, 4, 5, 13, 0));
        System.out.println("Bike2 → " + pa.getSpot());
        System.out.println("Bike3 → " + pb.getSpot());

        Vehicle bike4 = new Vehicle("KA-04-3333", VehicleType.TWO_WHEELER);
        ParkingPass p4 = lot.parkVehicle(bike4, groundGate, LocalDateTime.of(2026, 4, 5, 13, 5));
        System.out.println("Bike4 (overflow) → " + p4.getSpot() + " — billed at MEDIUM rate");

        // Full lot test — fill remaining 5 spots then try one more
        System.out.println("\n--- Full lot test ---");
        lot.parkVehicle(new Vehicle("KA-05-0001", VehicleType.CAR),       groundGate,      LocalDateTime.of(2026, 4, 5, 14, 0));
        lot.parkVehicle(new Vehicle("KA-05-0002", VehicleType.BUS),       groundGate,      LocalDateTime.of(2026, 4, 5, 14, 0));
        lot.parkVehicle(new Vehicle("KA-05-0003", VehicleType.TWO_WHEELER), firstFloorGate, LocalDateTime.of(2026, 4, 5, 14, 0));
        lot.parkVehicle(new Vehicle("KA-05-0004", VehicleType.CAR),       firstFloorGate,  LocalDateTime.of(2026, 4, 5, 14, 0));
        lot.parkVehicle(new Vehicle("KA-05-0005", VehicleType.BUS),       firstFloorGate,  LocalDateTime.of(2026, 4, 5, 14, 0));
        System.out.println("Available spots: " + lot.availableSpots());

        try {
            lot.parkVehicle(new Vehicle("KA-99-9999", VehicleType.CAR), groundGate, LocalDateTime.of(2026, 4, 5, 14, 1));
        } catch (IllegalStateException e) {
            System.out.println("Lot full: " + e.getMessage());
        }

        System.out.println("\n===== Done =====");
    }
}
