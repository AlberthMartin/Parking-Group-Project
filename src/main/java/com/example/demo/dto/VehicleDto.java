package com.example.demo.dto;

import lombok.Data;

@Data
public class VehicleDto {
    private String model;
    private String color;
    private String plate;
    private int length_cm;
    private int width_cm;
}
