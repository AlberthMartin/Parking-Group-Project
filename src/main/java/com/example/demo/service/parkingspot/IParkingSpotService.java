package com.example.demo.service.parkingspot;

import com.example.demo.dto.ParkingSpotDto;
import com.example.demo.model.ParkingSpot;
import com.example.demo.requests.parkingspot.CreateParkingSpotRequest;
import com.example.demo.requests.parkingspot.UpdateParkingSpotRequest;
import com.example.demo.security.user.AppUserDetails;

import java.time.LocalDateTime;
import java.util.List;

public interface IParkingSpotService {

    // Fetch all active spots
    List<ParkingSpotDto> fetchAllActiveParkingSpots();

    // Fetch all spots created by current user
    List<ParkingSpotDto> fetchParkingSpotsByUserId(AppUserDetails userDetails);

    //Fetch all active spots in specific city
    List<ParkingSpotDto> fetchAllActiveParkingSpotsInGivenCity(String city);

    //Fetch available spots in a city with given data range
    //Fetch all active spots in specific city
    List<ParkingSpotDto> fetchAllActiveParkingSpotsInGivenCityAndTimePeriod(String city, LocalDateTime startDate, LocalDateTime endDate);

    //Create a users parking spot and save it to the database
    //@params createparkingSpot request and the app user details
    ParkingSpotDto createParkingSpot(CreateParkingSpotRequest request, AppUserDetails userDetails);

    ParkingSpotDto updateParkingSpotById(UpdateParkingSpotRequest request, Long ParkingSpotId, AppUserDetails userDetails);

    void deleteParkingSpotById(Long parkingSpotId, AppUserDetails userDetails);
}
