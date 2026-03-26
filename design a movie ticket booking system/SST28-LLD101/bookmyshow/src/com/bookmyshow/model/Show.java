package com.bookmyshow.model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Show {
    private final String showId;
    private final Movie movie;
    private final Screen screen;
    private final Theatre theatre;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final Map<String, SeatStatus> seatStatusMap;

    public Show(String showId, Movie movie, Screen screen, Theatre theatre,
                LocalDateTime startTime, LocalDateTime endTime) {
        this.showId = showId;
        this.movie = movie;
        this.screen = screen;
        this.theatre = theatre;
        this.startTime = startTime;
        this.endTime = endTime;
        this.seatStatusMap = new ConcurrentHashMap<>();
        for (Seat seat : screen.getSeats()) {
            seatStatusMap.put(seat.getSeatId(), SeatStatus.AVAILABLE);
        }
    }

    public String getShowId() { return showId; }
    public Movie getMovie() { return movie; }
    public Screen getScreen() { return screen; }
    public Theatre getTheatre() { return theatre; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public Map<String, SeatStatus> getSeatStatusMap() { return Collections.unmodifiableMap(seatStatusMap); }

    public SeatStatus getSeatStatus(String seatId) {
        return seatStatusMap.getOrDefault(seatId, SeatStatus.AVAILABLE);
    }

    public void updateSeatStatus(String seatId, SeatStatus status) {
        seatStatusMap.put(seatId, status);
    }

    public boolean overlaps(LocalDateTime otherStart, LocalDateTime otherEnd) {
        return startTime.isBefore(otherEnd) && otherStart.isBefore(endTime);
    }

    @Override
    public String toString() {
        return movie.getTitle() + " @ " + theatre.getName() + "/" + screen.getName()
                + " [" + startTime + " - " + endTime + "]";
    }
}
