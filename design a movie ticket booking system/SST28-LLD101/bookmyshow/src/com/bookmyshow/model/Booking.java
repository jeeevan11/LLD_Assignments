package com.bookmyshow.model;

import java.time.LocalDateTime;
import java.util.List;

public class Booking {
    private final String bookingId;
    private final User customer;
    private final Show show;
    private final List<Seat> bookedSeats;
    private BookingStatus status;
    private Payment payment;
    private final LocalDateTime createdAt;

    public Booking(String bookingId, User customer, Show show, List<Seat> bookedSeats) {
        this.bookingId = bookingId;
        this.customer = customer;
        this.show = show;
        this.bookedSeats = bookedSeats;
        this.status = BookingStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public String getBookingId() { return bookingId; }
    public User getCustomer() { return customer; }
    public Show getShow() { return show; }
    public List<Seat> getBookedSeats() { return bookedSeats; }
    public BookingStatus getStatus() { return status; }
    public Payment getPayment() { return payment; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setStatus(BookingStatus status) { this.status = status; }
    public void setPayment(Payment payment) { this.payment = payment; }

    @Override
    public String toString() {
        return "Booking[" + bookingId + " | " + show.getMovie().getTitle()
                + " | Seats: " + bookedSeats.size() + " | " + status + "]";
    }
}
