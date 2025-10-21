package com.example.demo.requests.vehicle;

import lombok.Data;

@Data
public class VehicleRequest {
    private String model;
    private String color;
    private String plate;
    private int length_cm;
    private int width_cm;
}
