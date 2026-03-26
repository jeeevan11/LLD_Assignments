public class Gate {
    private final int gateId;
    private final int floor;

    public Gate(int gateId, int floor) {
        this.gateId = gateId;
        this.floor = floor;
    }

    public int getGateId() { return gateId; }
    public int getFloor() { return floor; }
}
