import java.util.List;

// Demo driver — 10-floor building, 3 lifts.
//
// Each scenario uses a fresh set of lifts so the output stays focused.
public class ElevatorDemo {

    public static void main(String[] args) {
        System.out.println("===== Elevator System Demo =====");
        System.out.println("Building: 10 floors  |  3 lifts\n");

        scenario1_basicUpRide();
        scenario2_scanAlgorithm();
        scenario3_dispatchPicksBestLift();
        scenario4_concurrentRequests();

        System.out.println("===== Done =====");
    }

    // -------------------------------------------------------------------------
    // Scenario 1 — simplest case: one hall call, one cab call
    // A starts at floor 1.
    //   Hall UP floor 3  → A dispatched (closest below floor 3)
    //   Cab call floor 7 → passenger boards at 3, presses 7
    // Expected path: 1 → 3 (stop) → 7 (stop) → idle
    // -------------------------------------------------------------------------
    static void scenario1_basicUpRide() {
        section("Scenario 1 — basic UP ride  (A@1)");

        LiftController ctrl = freshController(
                new Lift("A", 1),
                new Lift("B", 5),
                new Lift("C", 9)
        );

        ctrl.hallCall(3, Direction.UP);
        ctrl.cabCall("A", 7);

        runTicks(ctrl, 12);
    }

    // -------------------------------------------------------------------------
    // Scenario 2 — SCAN (LOOK) algorithm shown clearly on a single lift.
    // B starts at floor 5 with four pre-loaded stops:
    //   UP sweep:   floor 8, floor 9
    //   DOWN sweep: floor 3, floor 1
    // Expected path: 5→8(stop)→9(stop) ↕ flip → 3(stop)→1(stop) → idle
    // -------------------------------------------------------------------------
    static void scenario2_scanAlgorithm() {
        section("Scenario 2 — SCAN algorithm  (B@5, alone in the shaft)");

        // Only B in this controller so every call goes to B.
        LiftController ctrl = freshController(new Lift("B", 5));

        ctrl.cabCall("B", 8);   // 8 > 5  → upQueue
        ctrl.cabCall("B", 9);   // 9 > 5  → upQueue
        ctrl.cabCall("B", 3);   // 3 < 5  → downQueue
        ctrl.cabCall("B", 1);   // 1 < 5  → downQueue

        System.out.println("  B queues loaded: UP → {8, 9}   DOWN → {3, 1}");
        System.out.println("  Watch B sweep up, flip, then sweep back down.\n");

        runTicks(ctrl, 22);
    }

    // -------------------------------------------------------------------------
    // Scenario 3 — dispatch sends each call to the right lift.
    // A@1, B@6, C@10 — all idle.
    //   Hall UP floor 4   → A (below 4, correct side for UP;  B is above → penalised)
    //   Hall DOWN floor 8 → C (above 8, correct side for DOWN; B is below → penalised)
    //   B never moves.
    // -------------------------------------------------------------------------
    static void scenario3_dispatchPicksBestLift() {
        section("Scenario 3 — dispatch correctness  (A@1, B@6, C@10)");

        LiftController ctrl = freshController(
                new Lift("A", 1),
                new Lift("B", 6),
                new Lift("C", 10)
        );

        ctrl.hallCall(4, Direction.UP);    // A wins: score=3  (B: 1002, C: 1006)
        ctrl.hallCall(8, Direction.DOWN);  // C wins: score=2  (B: 1001, A: 1007)

        runTicks(ctrl, 10);
    }

    // -------------------------------------------------------------------------
    // Scenario 4 — three lifts running concurrently.
    // A@1: picks up floor 2 UP, then carries passenger to floor 9.
    // B@5: carries passenger down to floor 3.
    // C@9: picks up floor 7 DOWN, then carries passenger down to floor 4.
    // -------------------------------------------------------------------------
    static void scenario4_concurrentRequests() {
        section("Scenario 4 — three lifts working at once  (A@1, B@5, C@9)");

        LiftController ctrl = freshController(
                new Lift("A", 1),
                new Lift("B", 5),
                new Lift("C", 9)
        );

        ctrl.hallCall(2, Direction.UP);    // → A (below 2, going up)
        ctrl.cabCall("A", 9);              // A stops at 2, then continues to 9

        ctrl.cabCall("B", 3);              // B heads down to 3

        ctrl.hallCall(7, Direction.DOWN);  // → C (above 7, going down)
        ctrl.cabCall("C", 4);              // C stops at 7, then down to 4

        runTicks(ctrl, 18);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    static LiftController freshController(Lift... lifts) {
        return new LiftController(List.of(lifts), new NearestLiftStrategy());
    }

    static void section(String title) {
        System.out.println("--- " + title + " ---");
    }

    static void runTicks(LiftController ctrl, int max) {
        for (int i = 1; i <= max; i++) {
            System.out.println("  tick " + i + ":");
            ctrl.tick();
            ctrl.printStatus();
            if (ctrl.getLifts().stream().allMatch(Lift::isIdle)) {
                System.out.println("  [all idle — stopping early]");
                break;
            }
        }
        System.out.println();
    }
}
