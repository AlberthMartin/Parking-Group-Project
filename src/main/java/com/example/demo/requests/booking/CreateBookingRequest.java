package com.example.demo.requests.booking;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateBookingRequest {
    private Long spotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
