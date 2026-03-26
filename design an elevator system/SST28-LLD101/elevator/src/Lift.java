import java.util.TreeSet;

// A single lift running the SCAN (LOOK) algorithm.
//
// Two queues:
//   upQueue   — floors to stop at while going UP   (served smallest-first)
//   downQueue — floors to stop at while going DOWN (served largest-first)
//
// Routing rule: items are placed in the queue based on WHERE the floor is
// relative to the lift's current position, NOT the passenger's intended direction.
// The intended direction only matters for dispatch (which lift to pick), not
// for which queue to use.
//
// When the UP run is exhausted, we flip to DOWN (if downQueue is non-empty), and vice versa.
public class Lift {
    private final String id;
    private int currentFloor;
    private Direction direction;
    private boolean doorsOpen;

    // Floors above (or at) current that we need to visit going up
    private final TreeSet<Integer> upQueue   = new TreeSet<>();
    // Floors below (or at) current that we need to visit going down
    private final TreeSet<Integer> downQueue = new TreeSet<>();

    public Lift(String id, int startFloor) {
        this.id = id;
        this.currentFloor = startFloor;
        this.direction = Direction.IDLE;
        this.doorsOpen = false;
    }

    // Passenger pressed a button inside the lift (destination floor).
    public void addCabRequest(int floor) {
        if (floor == currentFloor) { doorsOpen = true; return; }
        enqueue(floor);
        wakeIfIdle(floor);
    }

    // Hall call routed to this lift by the dispatcher.
    // direction here is for information only — routing is purely positional.
    public void addHallRequest(int floor, Direction dir) {
        if (floor == currentFloor) { doorsOpen = true; return; }
        enqueue(floor);
        wakeIfIdle(floor);
    }

    // Advance the lift by one tick.
    // Returns a short string describing what happened.
    public String step() {
        if (doorsOpen) {
            doorsOpen = false;
            return "[" + id + "] doors close  @ floor " + currentFloor;
        }

        // If idle, decide a direction if there's work to do
        if (direction == Direction.IDLE) {
            if (!upQueue.isEmpty())        direction = Direction.UP;
            else if (!downQueue.isEmpty()) direction = Direction.DOWN;
            else return "[" + id + "] idle @ floor " + currentFloor;
        }

        if (direction == Direction.UP) {
            Integer next = upQueue.ceiling(currentFloor);   // nearest stop >= currentFloor
            if (next == null) {
                // Nothing left above — flip
                direction = downQueue.isEmpty() ? Direction.IDLE : Direction.DOWN;
                return "[" + id + "] ↕  UP run done @ floor " + currentFloor
                        + "  →  now " + direction;
            }
            if (next.equals(currentFloor)) {
                upQueue.remove(currentFloor);
                doorsOpen = true;
                return "[" + id + "] ▲  STOP   floor " + currentFloor + "  — doors open";
            }
            currentFloor++;
            return "[" + id + "] ▲  floor " + currentFloor;
        }

        if (direction == Direction.DOWN) {
            Integer next = downQueue.floor(currentFloor);   // nearest stop <= currentFloor
            if (next == null) {
                direction = upQueue.isEmpty() ? Direction.IDLE : Direction.UP;
                return "[" + id + "] ↕  DOWN run done @ floor " + currentFloor
                        + "  →  now " + direction;
            }
            if (next.equals(currentFloor)) {
                downQueue.remove(currentFloor);
                doorsOpen = true;
                return "[" + id + "] ▼  STOP   floor " + currentFloor + "  — doors open";
            }
            currentFloor--;
            return "[" + id + "] ▼  floor " + currentFloor;
        }

        return "[" + id + "] idle @ floor " + currentFloor;
    }

    // Estimate ticks to arrive at 'floor' for a call with the given direction.
    // Used by dispatch strategy — lower is better.
    public int estimatedArrival(int floor, Direction requestedDir) {
        int dist = Math.abs(currentFloor - floor);

        if (direction == Direction.IDLE) {
            // Prefer a lift that is on the correct side to serve the direction:
            //   UP call  → lift below or at the floor
            //   DOWN call → lift above or at the floor
            if (requestedDir == Direction.UP   && floor >= currentFloor) return dist;
            if (requestedDir == Direction.DOWN && floor <= currentFloor) return dist;
            return 1000 + dist;   // wrong side — big penalty
        }

        // Already heading the right way and the floor is still ahead
        if (direction == Direction.UP   && requestedDir == Direction.UP   && floor >= currentFloor) return dist;
        if (direction == Direction.DOWN && requestedDir == Direction.DOWN && floor <= currentFloor) return dist;

        // Otherwise the lift is going away or in the wrong direction
        return 1000 + dist;
    }

    public boolean isIdle() {
        return direction == Direction.IDLE && !doorsOpen
                && upQueue.isEmpty() && downQueue.isEmpty();
    }

    // Put the floor in the right queue based purely on position.
    private void enqueue(int floor) {
        if (floor > currentFloor) upQueue.add(floor);
        else                      downQueue.add(floor);
    }

    private void wakeIfIdle(int floor) {
        if (direction == Direction.IDLE) {
            direction = floor > currentFloor ? Direction.UP : Direction.DOWN;
        }
    }

    public String getId()           { return id; }
    public int getCurrentFloor()    { return currentFloor; }
    public Direction getDirection() { return direction; }

    public LiftState getState() {
        if (doorsOpen)                   return LiftState.DOORS_OPEN;
        if (direction == Direction.UP)   return LiftState.MOVING_UP;
        if (direction == Direction.DOWN) return LiftState.MOVING_DOWN;
        return LiftState.IDLE;
    }

    @Override
    public String toString() {
        return id + "@floor" + currentFloor + "(" + getState() + ")";
    }
}
