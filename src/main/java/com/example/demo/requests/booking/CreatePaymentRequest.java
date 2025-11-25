package com.example.demo.requests.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePaymentRequest {
    private Long bookingId;
    private String currency; 
}
