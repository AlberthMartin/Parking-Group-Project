package com.example.demo.responseDtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class BookingResponseDto {
    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal totalAmount;
    private String status;
    private Long renterId;
    private String renterName;
    private Long spotId;
    private String spotLocation;
    private LocalDateTime createdAt;
    private String clientSecret;
}
