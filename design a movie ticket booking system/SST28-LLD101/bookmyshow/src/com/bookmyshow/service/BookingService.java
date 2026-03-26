package com.bookmyshow.service;

import com.bookmyshow.exception.PaymentFailedException;
import com.bookmyshow.exception.SeatNotAvailableException;
import com.bookmyshow.lock.SeatLockManager;
import com.bookmyshow.model.*;
import com.bookmyshow.payment.PaymentGateway;
import com.bookmyshow.payment.PaymentGatewayFactory;
import com.bookmyshow.pricing.PricingStrategy;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingService {
    private final SeatLockManager lockManager;
    private final PricingStrategy pricingStrategy;
    private final Map<String, Booking> bookingStore = new ConcurrentHashMap<>();
    private final Map<SeatType, Double> basePrices;
    private final AtomicInteger bookingCounter = new AtomicInteger(0);
    private final AtomicInteger paymentCounter = new AtomicInteger(0);

    public BookingService(SeatLockManager lockManager, PricingStrategy pricingStrategy,
                          Map<SeatType, Double> basePrices) {
        this.lockManager = lockManager;
        this.pricingStrategy = pricingStrategy;
        this.basePrices = basePrices;
    }

    public Booking bookTickets(User customer, Show show, List<Seat> seats,
                               PaymentMethod paymentMethod) {
        // lock seats first
        boolean locked = lockManager.lockSeats(show, seats, customer.getUserId());
        if (!locked) {
            throw new SeatNotAvailableException(
                    "One or more seats are not available for show: " + show.getShowId());
        }
        System.out.println("  Seats locked for " + customer.getName());

        // calculate price
        double totalPrice = 0;
        for (Seat seat : seats) {
            double base = basePrices.getOrDefault(seat.getSeatType(), 200.0);
            totalPrice += pricingStrategy.calculatePrice(show, seat, base);
        }
        System.out.println("  Total: Rs." + String.format("%.2f", totalPrice));

        // process payment
        String paymentId = "PAY-" + paymentCounter.incrementAndGet();
        Payment payment = new Payment(paymentId, totalPrice, paymentMethod);
        PaymentGateway gateway = PaymentGatewayFactory.getGateway(paymentMethod);
        boolean paymentSuccess = gateway.processPayment(payment);

        if (!paymentSuccess) {
            lockManager.releaseSeats(show, seats);
            throw new PaymentFailedException("Payment failed for booking attempt");
        }

        // confirm booking
        String bookingId = "BKG-" + bookingCounter.incrementAndGet();
        Booking booking = new Booking(bookingId, customer, show, seats);
        booking.setPayment(payment);
        booking.setStatus(BookingStatus.CONFIRMED);
        lockManager.confirmSeats(show, seats);
        bookingStore.put(bookingId, booking);

        System.out.println("  Booking confirmed: " + booking);
        return booking;
    }

    public void cancelBooking(String bookingId) {
        Booking booking = bookingStore.get(bookingId);
        if (booking == null) {
            throw new IllegalArgumentException("Booking not found: " + bookingId);
        }
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            System.out.println("  Already cancelled: " + bookingId);
            return;
        }

        lockManager.releaseSeats(booking.getShow(), booking.getBookedSeats());

        // refund to original payment method
        Payment payment = booking.getPayment();
        PaymentGateway gateway = PaymentGatewayFactory.getGateway(payment.getMethod());
        gateway.processRefund(payment);

        booking.setStatus(BookingStatus.CANCELLED);
        System.out.println("  Booking cancelled: " + bookingId);
    }

    public Booking getBooking(String bookingId) {
        return bookingStore.get(bookingId);
    }
}
