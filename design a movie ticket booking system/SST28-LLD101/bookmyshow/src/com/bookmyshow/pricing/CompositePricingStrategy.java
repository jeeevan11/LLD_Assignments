package com.bookmyshow.pricing;

import com.bookmyshow.model.Show;
import com.bookmyshow.model.Seat;

import java.util.List;

public class CompositePricingStrategy implements PricingStrategy {
    private final List<PricingStrategy> strategies;

    public CompositePricingStrategy(List<PricingStrategy> strategies) {
        this.strategies = strategies;
    }

    @Override
    public double calculatePrice(Show show, Seat seat, double basePrice) {
        double finalPrice = basePrice;
        for (PricingStrategy strategy : strategies) {
            double candidate = strategy.calculatePrice(show, seat, basePrice);
            finalPrice = Math.max(finalPrice, candidate);
        }
        return finalPrice;
    }
}
