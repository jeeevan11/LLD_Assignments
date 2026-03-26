package com.bookmyshow;

import com.bookmyshow.exception.SeatNotAvailableException;
import com.bookmyshow.lock.SeatLockManager;
import com.bookmyshow.model.*;
import com.bookmyshow.pricing.*;
import com.bookmyshow.service.*;

import java.time.LocalDateTime;
import java.util.*;

public class App {
    public static void main(String[] args) {
        System.out.println("===== BookMyShow Demo =====\n");

        // setup
        SeatLockManager lockManager = new SeatLockManager();
        PricingStrategy pricing = new CompositePricingStrategy(List.of(
                new DemandPricingStrategy(),
                new WeekendPricingStrategy()
        ));
        Map<SeatType, Double> basePrices = Map.of(
                SeatType.SILVER, 150.0, SeatType.GOLD, 250.0, SeatType.PLATINUM, 400.0
        );

        MovieService movieService = new MovieService();
        TheatreService theatreService = new TheatreService();
        ShowService showService = new ShowService();
        BookingService bookingService = new BookingService(lockManager, pricing, basePrices);

        User alice = new User("U1", "Alice", "alice@gmail.com", false);
        User bob = new User("U2", "Bob", "bob@gmail.com", false);

        // add movies
        System.out.println("--- Adding movies ---");
        Movie m1 = new Movie("MOV1", "Inception", "Sci-Fi", 148);
        Movie m2 = new Movie("MOV2", "The Dark Knight", "Action", 152);
        movieService.addMovie(m1);
        movieService.addMovie(m2);

        // add theatre with 2 screens
        System.out.println("\n--- Adding theatre ---");
        List<Seat> screen1Seats = new ArrayList<>();
        screen1Seats.add(new Seat("S1-R1-1", 1, 1, SeatType.SILVER));
        screen1Seats.add(new Seat("S1-R1-2", 1, 2, SeatType.SILVER));
        screen1Seats.add(new Seat("S1-R2-1", 2, 1, SeatType.GOLD));
        screen1Seats.add(new Seat("S1-R2-2", 2, 2, SeatType.GOLD));
        screen1Seats.add(new Seat("S1-R3-1", 3, 1, SeatType.PLATINUM));
        screen1Seats.add(new Seat("S1-R3-2", 3, 2, SeatType.PLATINUM));
        Screen screen1 = new Screen("SCR1", "Screen 1", screen1Seats);

        List<Seat> screen2Seats = new ArrayList<>();
        screen2Seats.add(new Seat("S2-R1-1", 1, 1, SeatType.SILVER));
        screen2Seats.add(new Seat("S2-R1-2", 1, 2, SeatType.SILVER));
        screen2Seats.add(new Seat("S2-R2-1", 2, 1, SeatType.GOLD));
        screen2Seats.add(new Seat("S2-R2-2", 2, 2, SeatType.GOLD));
        Screen screen2 = new Screen("SCR2", "Screen 2", screen2Seats);

        Theatre theatre = new Theatre("TH1", "INOX Lido", "Bangalore",
                new ArrayList<>(List.of(screen1, screen2)));
        theatreService.addTheatre(theatre);

        // add shows
        System.out.println("\n--- Adding shows ---");
        Show show1 = showService.addShow("SH1", m1, screen1, theatre,
                LocalDateTime.of(2026, 4, 5, 14, 0));
        showService.addShow("SH2", m2, screen1, theatre,
                LocalDateTime.of(2026, 4, 5, 18, 0));
        showService.addShow("SH3", m1, screen2, theatre,
                LocalDateTime.of(2026, 4, 5, 14, 0));

        // overlap test
        System.out.println("\n--- Overlap test ---");
        try {
            showService.addShow("SH-BAD", m2, screen1, theatre,
                    LocalDateTime.of(2026, 4, 5, 15, 0));
        } catch (Exception e) {
            System.out.println("Overlap caught: " + e.getMessage());
        }

        // browse
        System.out.println("\n--- Browse ---");
        System.out.println("Theatres in Bangalore: " + theatreService.getTheatresByCity("Bangalore"));
        System.out.println("Movies in Bangalore: " + showService.getMoviesByCity("Bangalore"));

        // seat layout
        System.out.println("\n--- Seat layout (SH1) ---");
        Map<String, SeatStatus> layout = showService.getSeatLayout("SH1");
        for (Seat seat : screen1Seats)
            System.out.println("  " + seat + " -> " + layout.get(seat.getSeatId()));

        // Alice books gold seats
        System.out.println("\n--- Alice books 2 Gold seats (UPI) ---");
        List<Seat> aliceSeats = List.of(screen1Seats.get(2), screen1Seats.get(3));
        Booking aliceBooking = bookingService.bookTickets(alice, show1, aliceSeats, PaymentMethod.UPI);

        // Bob tries same seats - should fail
        System.out.println("\n--- Bob tries same seats ---");
        try {
            bookingService.bookTickets(bob, show1, aliceSeats, PaymentMethod.CREDIT_CARD);
        } catch (SeatNotAvailableException e) {
            System.out.println("Blocked: " + e.getMessage());
        }

        // Bob books platinum
        System.out.println("\n--- Bob books Platinum (Credit Card) ---");
        List<Seat> bobSeats = List.of(screen1Seats.get(4), screen1Seats.get(5));
        bookingService.bookTickets(bob, show1, bobSeats, PaymentMethod.CREDIT_CARD);

        // Alice cancels - refund to UPI
        System.out.println("\n--- Alice cancels ---");
        bookingService.cancelBooking(aliceBooking.getBookingId());

        // layout after cancel
        System.out.println("\n--- Seat layout after cancel ---");
        layout = showService.getSeatLayout("SH1");
        for (Seat seat : screen1Seats)
            System.out.println("  " + seat + " -> " + layout.get(seat.getSeatId()));

        // multi-threaded test - two users race for same seats
        System.out.println("\n--- Concurrency test ---");
        List<Seat> contested = List.of(screen1Seats.get(0), screen1Seats.get(1));

        Thread t1 = new Thread(() -> {
            try {
                Booking b = bookingService.bookTickets(alice, show1, contested, PaymentMethod.UPI);
                System.out.println("  Alice got it: " + b.getBookingId());
            } catch (SeatNotAvailableException e) {
                System.out.println("  Alice lost: " + e.getMessage());
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                Booking b = bookingService.bookTickets(bob, show1, contested, PaymentMethod.DEBIT_CARD);
                System.out.println("  Bob got it: " + b.getBookingId());
            } catch (SeatNotAvailableException e) {
                System.out.println("  Bob lost: " + e.getMessage());
            }
        });

        t1.start(); t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        System.out.println("\n--- Final layout ---");
        layout = showService.getSeatLayout("SH1");
        for (Seat seat : screen1Seats)
            System.out.println("  " + seat + " -> " + layout.get(seat.getSeatId()));

        System.out.println("\n===== Done =====");
    }
}
