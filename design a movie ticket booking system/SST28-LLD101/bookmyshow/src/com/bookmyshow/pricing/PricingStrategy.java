package com.bookmyshow.pricing;

import com.bookmyshow.model.Show;
import com.bookmyshow.model.Seat;

public interface PricingStrategy {
    double calculatePrice(Show show, Seat seat, double basePrice);
}
