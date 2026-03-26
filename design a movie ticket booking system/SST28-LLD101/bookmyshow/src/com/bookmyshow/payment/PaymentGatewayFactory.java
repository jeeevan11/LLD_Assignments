package com.bookmyshow.payment;

import com.bookmyshow.model.PaymentMethod;

import java.util.EnumMap;
import java.util.Map;

public class PaymentGatewayFactory {

    private static final Map<PaymentMethod, PaymentGateway> GATEWAYS = new EnumMap<>(PaymentMethod.class);

    static {
        GATEWAYS.put(PaymentMethod.UPI,         new UpiGateway());
        GATEWAYS.put(PaymentMethod.CREDIT_CARD, new CreditCardGateway());
        GATEWAYS.put(PaymentMethod.DEBIT_CARD,  new DebitCardGateway());
    }

    public static PaymentGateway getGateway(PaymentMethod method) {
        PaymentGateway gateway = GATEWAYS.get(method);
        if (gateway == null) {
            throw new IllegalArgumentException("Unsupported payment method: " + method);
        }
        return gateway;
    }
}
