package com.bookmyshow.pricing;

import com.bookmyshow.model.Show;
import com.bookmyshow.model.Seat;
import com.bookmyshow.model.SeatStatus;

public class DemandPricingStrategy implements PricingStrategy {

    @Override
    public double calculatePrice(Show show, Seat seat, double basePrice) {
        long totalSeats = show.getScreen().getSeats().size();
        long bookedSeats = show.getSeatStatusMap().values().stream()
                .filter(s -> s == SeatStatus.BOOKED || s == SeatStatus.LOCKED)
                .count();

        double occupancy = (double) bookedSeats / totalSeats;
        double multiplier = 1.0;

        if (occupancy > 0.75) multiplier = 1.5;
        else if (occupancy > 0.50) multiplier = 1.3;
        else if (occupancy > 0.25) multiplier = 1.1;

        return Math.max(basePrice, basePrice * multiplier);
    }
}
