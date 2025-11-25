package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.service.payment.PaymentService;
import com.google.gson.JsonSyntaxException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.StripeObject;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stripe")
@RequiredArgsConstructor
public class StripeWebhookController {

    @Value("${stripe.webhook.secret}")
    private String endpointSecret;

    private final PaymentService paymentService;

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeWebhook(
            @RequestBody String payload,
            @RequestHeader(value = "Stripe-Signature", required = false) String sigHeader
    ) {
        Event event;

        try {
            event = ApiResource.GSON.fromJson(payload, Event.class);
        } catch (JsonSyntaxException e) {
            System.out.println("⚠️  Webhook error while parsing basic request.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        if (endpointSecret != null && !endpointSecret.isEmpty() && sigHeader != null) {
            try {
                event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
            } catch (SignatureVerificationException e) {
                System.out.println("⚠️  Webhook error while validating signature.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
            }
        }

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        StripeObject stripeObject = dataObjectDeserializer.getObject().orElse(null);

        if (stripeObject == null) {
            System.out.println("⚠️  Unable to deserialize Stripe object from event.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        switch (event.getType()) {
            case "payment_intent.succeeded": {
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                System.out.println("✅ Payment for " + paymentIntent.getAmount() + " succeeded.");
                paymentService.handlePaymentSucceeded(paymentIntent);
                break;
            }
            case "payment_intent.processing": {
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                System.out.println("ℹ️ Payment for " + paymentIntent.getAmount() + " is processing.");
                break;
            }
            case "payment_intent.payment_failed": {
                PaymentIntent paymentIntent = (PaymentIntent) stripeObject;
                System.out.println("❌ Payment for " + paymentIntent.getAmount() + " failed.");
                paymentService.handlePaymentFailed(paymentIntent);
                break;
            }
            case "payment_method.attached": {
                PaymentMethod paymentMethod = (PaymentMethod) stripeObject;
                System.out.println("ℹ️ Payment method attached: " + paymentMethod.getId());
                break;
            }
            default:
                System.out.println("Unhandled event type: " + event.getType());
                break;
        }

        return ResponseEntity.ok("");
    }
}