package com.example.demo.responseDtos;

import lombok.Data;

@Data
public class VehicleResponseDto {
    private String model;
    private String color;
    private String plate;
    private int length_cm;
    private int width_cm;
}
