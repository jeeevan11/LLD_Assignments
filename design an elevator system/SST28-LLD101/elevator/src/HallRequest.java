// Represents a button press at a floor (outside the lift).
// direction = which way the person wants to travel.
public class HallRequest {
    private final int floor;
    private final Direction direction;

    public HallRequest(int floor, Direction direction) {
        this.floor = floor;
        this.direction = direction;
    }

    public int getFloor() { return floor; }
    public Direction getDirection() { return direction; }

    @Override
    public String toString() {
        return "HallRequest[floor=" + floor + ", " + direction + "]";
    }
}
