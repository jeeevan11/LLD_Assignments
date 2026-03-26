package com.bookmyshow.pricing;

import com.bookmyshow.model.Show;
import com.bookmyshow.model.Seat;

import java.time.DayOfWeek;

public class WeekendPricingStrategy implements PricingStrategy {

    @Override
    public double calculatePrice(Show show, Seat seat, double basePrice) {
        DayOfWeek day = show.getStartTime().getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return basePrice * 1.2;
        }
        return basePrice;
    }
}
