package com.bookmyshow.service;

import com.bookmyshow.model.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MovieService {
    private final Map<String, Movie> movieStore = new ConcurrentHashMap<>();

    public void addMovie(Movie movie) {
        movieStore.put(movie.getMovieId(), movie);
        System.out.println("[ADMIN] Movie added: " + movie);
    }

    public Movie getMovie(String movieId) {
        return movieStore.get(movieId);
    }

    public List<Movie> getAllMovies() {
        return new ArrayList<>(movieStore.values());
    }
}
