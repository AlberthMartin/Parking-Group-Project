package com.example.demo.repository;
import com.example.demo.enums.BookingStatus;
import com.example.demo.model.Booking;
import com.example.demo.model.ParkingSpot;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    //Find booking by user (renter)
    List<Booking> findByRenter(User renter);

    //Find booking for a specific parking spot
    List<Booking> findBySpot(ParkingSpot spot);

    // Check if a spot is already booked in a given time range
    boolean existsBySpotAndStart_timeLessThanEqualAndEnd_timeGreaterThanEqual(
            ParkingSpot spot,
            LocalDateTime endTime,
            LocalDateTime startTime
    );

    List<Booking> findByStatus(BookingStatus status);

    Optional<Booking> findBySpotAndStatus(ParkingSpot spot, BookingStatus status);

    User renter(User renter);
}
