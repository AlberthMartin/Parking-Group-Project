package com.example.demo.requests.parkingspot;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UpdateParkingSpotRequest {
    private String title;
    private String description;
    private String address;
    private String city;
    private String postal_code;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private LocalDateTime available_from;
    private LocalDateTime available_to;
    private double price_per_hour;
    private double price_per_day;
}
