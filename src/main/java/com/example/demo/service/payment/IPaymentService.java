package com.example.demo.service.payment;

import com.stripe.model.PaymentIntent;

public interface IPaymentService {

    /**
     * Create a PaymentIntent for a booking and return the client secret.
     */
    String createPaymentIntentForBooking(Long bookingId, String currency);

    /**
     * Handle successful payment (called from webhook).
     */
    void handlePaymentSucceeded(PaymentIntent paymentIntent);

    /**
     * Handle failed payment (called from webhook).
     */
    void handlePaymentFailed(PaymentIntent paymentIntent);
}

