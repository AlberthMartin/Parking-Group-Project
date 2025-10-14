package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class ParkingSpot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    //position
    private String address;
    private String city;
    private String postal_code;
    private String country;
    private BigDecimal latitude;
    private BigDecimal longitude;

    //availability
    private LocalDateTime available_from;
    private LocalDateTime available_to;

    //price
    private double price_per_hour;
    private double price_per_day;

    private boolean isActive; // is this a active parking spot?

    //One user can have multiple parking spots
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    //One parking spot can have many images
    @OneToMany(mappedBy = "parkingSpot", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SpotImage> spotImages = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;



}
