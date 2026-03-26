package com.bookmyshow.lock;

import com.bookmyshow.model.Seat;
import com.bookmyshow.model.Show;
import com.bookmyshow.model.SeatStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SeatLockManager {
    private static final int LOCK_TIMEOUT_MINUTES = 5;
    private final Map<String, LockInfo> lockMap = new ConcurrentHashMap<>();

    public boolean lockSeats(Show show, List<Seat> seats, String userId) {
        synchronized (show) {
            for (Seat seat : seats) {
                String key = buildKey(show.getShowId(), seat.getSeatId());
                SeatStatus status = show.getSeatStatus(seat.getSeatId());

                if (status == SeatStatus.BOOKED) {
                    return false;
                }
                if (status == SeatStatus.LOCKED) {
                    LockInfo lock = lockMap.get(key);
                    if (lock != null && !lock.isExpired()) {
                        return false;
                    }
                    // expired, allow it
                }
            }

            // all good, lock them
            LocalDateTime expiry = LocalDateTime.now().plusMinutes(LOCK_TIMEOUT_MINUTES);
            for (Seat seat : seats) {
                String key = buildKey(show.getShowId(), seat.getSeatId());
                lockMap.put(key, new LockInfo(expiry));
                show.updateSeatStatus(seat.getSeatId(), SeatStatus.LOCKED);
            }
            return true;
        }
    }

    public void confirmSeats(Show show, List<Seat> seats) {
        synchronized (show) {
            for (Seat seat : seats) {
                String key = buildKey(show.getShowId(), seat.getSeatId());
                lockMap.remove(key);
                show.updateSeatStatus(seat.getSeatId(), SeatStatus.BOOKED);
            }
        }
    }

    public void releaseSeats(Show show, List<Seat> seats) {
        synchronized (show) {
            for (Seat seat : seats) {
                String key = buildKey(show.getShowId(), seat.getSeatId());
                lockMap.remove(key);
                show.updateSeatStatus(seat.getSeatId(), SeatStatus.AVAILABLE);
            }
        }
    }

    public void cleanExpiredLocks(Show show) {
        synchronized (show) {
            for (Seat seat : show.getScreen().getSeats()) {
                String key = buildKey(show.getShowId(), seat.getSeatId());
                LockInfo lock = lockMap.get(key);
                if (lock != null && lock.isExpired()) {
                    lockMap.remove(key);
                    show.updateSeatStatus(seat.getSeatId(), SeatStatus.AVAILABLE);
                    System.out.println("  [LOCK] Expired lock released: " + seat);
                }
            }
        }
    }

    private String buildKey(String showId, String seatId) {
        return showId + "::" + seatId;
    }

    private static class LockInfo {
        private final LocalDateTime expiry;

        LockInfo(LocalDateTime expiry) {
            this.expiry = expiry;
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiry);
        }
    }
}
