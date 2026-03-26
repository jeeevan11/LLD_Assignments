public class ParkingSpot {
    private final int spotNumber;
    private final SpotType type;
    private final int floor;
    private boolean reserved;

    public ParkingSpot(int spotNumber, SpotType type, int floor) {
        this.spotNumber = spotNumber;
        this.type = type;
        this.floor = floor;
        this.reserved = false;
    }

    public int getSpotNumber() { return spotNumber; }
    public SpotType getType() { return type; }

    @Override
    public String toString() {
        return "Spot#" + spotNumber + "[" + type + ", Floor " + floor + "]";
    }
    public int getFloor() { return floor; }
    public boolean isReserved() { return reserved; }

    public void reserve() { this.reserved = true; }
    public void release() { this.reserved = false; }
}
