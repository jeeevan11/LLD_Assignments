import java.util.List;

// Picks the lift with the lowest estimated arrival time.
// Idle lifts are scored by pure distance.
// Lifts already heading the same way past the floor get a large penalty.
public class NearestLiftStrategy implements DispatchStrategy {

    @Override
    public Lift selectLift(List<Lift> lifts, int floor, Direction direction) {
        Lift best = null;
        int bestScore = Integer.MAX_VALUE;

        for (Lift lift : lifts) {
            int score = lift.estimatedArrival(floor, direction);
            if (score < bestScore) {
                bestScore = score;
                best = lift;
            }
        }
        return best;
    }
}
