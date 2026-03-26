package com.bookmyshow.service;

import com.bookmyshow.exception.ShowOverlapException;
import com.bookmyshow.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ShowService {
    private final Map<String, Show> showStore = new ConcurrentHashMap<>();

    public Show addShow(String showId, Movie movie, Screen screen, Theatre theatre,
                        LocalDateTime startTime) {
        LocalDateTime endTime = startTime.plusMinutes(movie.getDurationMinutes());

        // Validate no overlap on same screen
        for (Show existing : showStore.values()) {
            if (existing.getScreen().getScreenId().equals(screen.getScreenId())
                    && existing.getTheatre().getTheatreId().equals(theatre.getTheatreId())
                    && existing.overlaps(startTime, endTime)) {
                throw new ShowOverlapException(
                        "Show overlaps with existing show: " + existing);
            }
        }

        Show show = new Show(showId, movie, screen, theatre, startTime, endTime);
        showStore.put(showId, show);
        System.out.println("[ADMIN] Show added: " + show);
        return show;
    }

    public Show getShow(String showId) {
        return showStore.get(showId);
    }

    public List<Show> getShowsForMovie(String movieId) {
        return showStore.values().stream()
                .filter(s -> s.getMovie().getMovieId().equals(movieId))
                .collect(Collectors.toList());
    }

    public List<Show> getShowsByCity(String city) {
        return showStore.values().stream()
                .filter(s -> s.getTheatre().getCity().equalsIgnoreCase(city))
                .collect(Collectors.toList());
    }

    public List<Movie> getMoviesByCity(String city) {
        return showStore.values().stream()
                .filter(s -> s.getTheatre().getCity().equalsIgnoreCase(city))
                .map(Show::getMovie)
                .distinct()
                .collect(Collectors.toList());
    }

    public Map<String, SeatStatus> getSeatLayout(String showId) {
        Show show = showStore.get(showId);
        if (show == null) throw new IllegalArgumentException("Show not found: " + showId);
        return show.getSeatStatusMap();
    }
}
