package com.example.demo.repository;

import com.example.demo.model.ParkingSpot;
import com.example.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {

    // Fetch all active spots
    List<ParkingSpot> findByIsActiveTrue();

    // Fetch all spots created by a specific user
    List<ParkingSpot> findByCreatedBy(User user);

    //Fetch all spots in specific city (ignoring capitalization differences, and only fetching active)
    List<ParkingSpot> findByCityIgnoreCaseAndIsActiveTrue(String city);

    // Fetch available spots in a city within a given date range
    @Query("""
         SELECT ps FROM ParkingSpot ps
         WHERE ps.city = :city
           AND ps.isActive = true
           AND ps.available_from <= :startDate
           AND ps.available_to >= :endDate
         """)
    List<ParkingSpot> findAvailableSpotsInCity(
            @Param("city") String city,
            @Param("startDate")LocalDateTime startDate,
            @Param("endDate")LocalDateTime endDate);

    //Search by keyword (title or description)
    @Query(""" 
      SELECT ps FROM ParkingSpot ps
      WHERE LOWER(ps.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
         OR LOWER(ps.description) LIKE LOWER(CONCAT('%', :keyword, '%'))""")
    List<ParkingSpot> searchByKeyword(@Param("keyword") String keyword);

    //Fetch nearby spots bounding box query
    @Query("""
        SELECT ps FROM ParkingSpot ps
         WHERE ps.latitude BETWEEN :minLat AND :maxLat
           AND ps.longitude BETWEEN :minLon AND :maxLon
           AND ps.isActive = true
""")
    List<ParkingSpot> findNearbySpots(
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLon") double minLon,
            @Param("maxLon") double maxLon);
}




