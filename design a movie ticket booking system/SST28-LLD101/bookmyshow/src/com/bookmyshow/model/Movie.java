package com.bookmyshow.model;

public class Movie {
    private final String movieId;
    private final String title;
    private final String genre;
    private final int durationMinutes;

    public Movie(String movieId, String title, String genre, int durationMinutes) {
        this.movieId = movieId;
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
    }

    public String getMovieId() { return movieId; }
    public String getTitle() { return title; }
    public String getGenre() { return genre; }
    public int getDurationMinutes() { return durationMinutes; }

    @Override
    public String toString() {
        return title + " (" + genre + ", " + durationMinutes + "min)";
    }
}
