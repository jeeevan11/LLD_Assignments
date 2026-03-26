import java.util.Collections;
import java.util.List;

// Sits between the outside world and the individual lifts.
// Handles hall calls (floor buttons), cab requests (buttons inside a lift),
// and ticking all lifts forward one step at a time.
public class LiftController {
    private final List<Lift> lifts;
    private final DispatchStrategy strategy;

    public LiftController(List<Lift> lifts, DispatchStrategy strategy) {
        this.lifts = lifts;
        this.strategy = strategy;
    }

    // Someone pressed UP or DOWN at a floor.
    public void hallCall(int floor, Direction direction) {
        Lift chosen = strategy.selectLift(lifts, floor, direction);
        System.out.println("  >> Dispatch: " + chosen.getId()
                + " assigned to floor " + floor + " (" + direction + ")");
        chosen.addHallRequest(floor, direction);
    }

    // Passenger inside a lift pressed a destination floor button.
    public void cabCall(String liftId, int floor) {
        lifts.stream()
             .filter(l -> l.getId().equals(liftId))
             .findFirst()
             .ifPresentOrElse(
                 l -> {
                     System.out.println("  >> Cab call: " + liftId + " → floor " + floor);
                     l.addCabRequest(floor);
                 },
                 () -> System.out.println("  >> Unknown lift id: " + liftId)
             );
    }

    // Advance every lift by one tick.
    public void tick() {
        lifts.forEach(l -> System.out.println("  " + l.step()));
    }

    // Print current position + state of all lifts.
    public void printStatus() {
        System.out.println("  Status → " + lifts.stream()
                .map(l -> l.getId() + ":F" + l.getCurrentFloor() + "(" + l.getState() + ")")
                .reduce((a, b) -> a + "  |  " + b)
                .orElse("no lifts"));
    }

    public List<Lift> getLifts() { return Collections.unmodifiableList(lifts); }
}
