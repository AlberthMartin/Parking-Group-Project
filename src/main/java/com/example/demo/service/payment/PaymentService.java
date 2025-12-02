package com.example.demo.service.payment;

import java.math.BigDecimal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.enums.BookingStatus;
import com.example.demo.exeptions.ResourceNotFoundException;
import com.example.demo.model.Booking;
import com.example.demo.repository.BookingRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService implements IPaymentService {

    private final BookingRepository bookingRepository;

    @Override
    public String createPaymentIntentForBooking(Long bookingId, String currency) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        // Convert totalAmount (double) to minor units (e.g. cents)
        BigDecimal total = BigDecimal.valueOf(booking.getTotalAmount());
        long amountInMinorUnits = total.movePointRight(2).longValueExact(); // 10.50 -> 1050

        PaymentIntentCreateParams params = PaymentIntentCreateParams
                .builder()
                .setAmount(amountInMinorUnits)
                .setCurrency(currency)
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods
                                .builder()
                                .setEnabled(true)
                                .build()
                )
                .putMetadata("bookingId", booking.getId().toString())
                .build();

        try {
            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // store PaymentIntent id on booking
            booking.setStripePaymentIntentId(paymentIntent.getId());
            bookingRepository.save(booking);

            return paymentIntent.getClientSecret();
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create PaymentIntent", e);
        }
    }

    @Override
    public void handlePaymentSucceeded(PaymentIntent paymentIntent) {
        String bookingIdStr = paymentIntent.getMetadata().get("bookingId");
        if (bookingIdStr == null) {
            System.out.println("⚠️ No bookingId in PaymentIntent metadata.");
            return;
        }

        Long bookingId = Long.valueOf(bookingIdStr);

        bookingRepository.findById(bookingId).ifPresent(booking -> {
            if (booking.getStatus() == BookingStatus.PENDING) {
                booking.setStatus(BookingStatus.CONFIRMED);
                bookingRepository.save(booking);
                System.out.println("✅ Booking " + bookingId + " marked as CONFIRMED.");
            } else {
                System.out.println("ℹ️ Booking " + bookingId + " was not PENDING (status: " + booking.getStatus() + ")");
            }
        });
    }

    @Override
    public void handlePaymentFailed(PaymentIntent paymentIntent) {
        String bookingIdStr = paymentIntent.getMetadata().get("bookingId");
        if (bookingIdStr == null) {
            System.out.println("⚠️ No bookingId in PaymentIntent metadata.");
            return;
        }

        Long bookingId = Long.valueOf(bookingIdStr);

        bookingRepository.findById(bookingId).ifPresent(booking -> {
            // You could add a FAILED status if you want; for now just log.
            System.out.println("❌ Payment failed for booking " + bookingId);
        });
    }
}

