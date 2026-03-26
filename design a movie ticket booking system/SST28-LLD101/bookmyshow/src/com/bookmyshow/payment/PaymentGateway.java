package com.bookmyshow.payment;

import com.bookmyshow.model.Payment;

public interface PaymentGateway {
    boolean processPayment(Payment payment);
    boolean processRefund(Payment payment);
}
