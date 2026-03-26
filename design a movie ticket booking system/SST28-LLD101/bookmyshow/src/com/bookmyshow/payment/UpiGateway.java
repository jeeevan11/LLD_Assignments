package com.bookmyshow.payment;

import com.bookmyshow.model.Payment;
import com.bookmyshow.model.PaymentStatus;

public class UpiGateway implements PaymentGateway {

    @Override
    public boolean processPayment(Payment payment) {
        System.out.println("  [UPI] Processing Rs." + payment.getAmount());
        payment.setStatus(PaymentStatus.SUCCESS);
        return true;
    }

    @Override
    public boolean processRefund(Payment payment) {
        System.out.println("  [UPI] Refunding Rs." + payment.getAmount());
        payment.setStatus(PaymentStatus.REFUNDED);
        return true;
    }
}
