package com.example.demo.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.requests.booking.CreatePaymentRequest;
import com.example.demo.service.payment.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class StripePaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-intent")
    public ResponseEntity<Map<String, String>> createPaymentIntent(
            @RequestBody CreatePaymentRequest requestBody
    ) {
        Map<String, String> response = new HashMap<>();

        try {
            String clientSecret = paymentService.createPaymentIntentForBooking(
                    requestBody.getBookingId(),
                    requestBody.getCurrency()
            );
            response.put("client_secret", clientSecret);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}