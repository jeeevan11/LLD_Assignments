import java.util.List;

public interface DispatchStrategy {
    // Pick the best lift to handle a hall call at the given floor/direction.
    Lift selectLift(List<Lift> lifts, int floor, Direction direction);
}
