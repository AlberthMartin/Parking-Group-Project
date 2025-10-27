package com.example.demo.model;

import com.example.demo.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    private double totalAmount; //total price for booking

    @Enumerated(EnumType.STRING)
    private BookingStatus status; //'PENDING','CONFIRMED','CANCELLED','COMPLETED'

    //one user can have many bookings (rent many spots)
    @ManyToOne(optional = false)
    @JoinColumn(name = "renter_id")
    private User renter;

    //Many bookings to one spot
    @ManyToOne(optional = false)
    @JoinColumn(name = "spot_id")
    private ParkingSpot spot;

    @CreationTimestamp
    private LocalDateTime createdAt;
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    //payment info...
    //private String stripePaymentIntentId;


}
