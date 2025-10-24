package com.example.demo.service.parkingspot;

import com.example.demo.responseDtos.ParkingSpotResponseDto;
import com.example.demo.requests.parkingspot.CreateParkingSpotRequest;
import com.example.demo.requests.parkingspot.UpdateParkingSpotRequest;
import com.example.demo.security.user.AppUserDetails;

import java.time.LocalDateTime;
import java.util.List;

public interface IParkingSpotService {

    // Fetch all active spots
    List<ParkingSpotResponseDto> fetchAllActiveParkingSpots();

    // Fetch all spots created by current user
    List<ParkingSpotResponseDto> fetchParkingSpotsByUserId(AppUserDetails userDetails);

    //Fetch all active spots in specific city
    List<ParkingSpotResponseDto> fetchAllActiveParkingSpotsInGivenCity(String city);

    //Fetch available spots in a city with given data range
    //Fetch all active spots in specific city
    List<ParkingSpotResponseDto> fetchAllActiveParkingSpotsInGivenCityAndTimePeriod(String city, LocalDateTime startDate, LocalDateTime endDate);

    //Create a users parking spot and save it to the database
    //@params createparkingSpot request and the app user details
    ParkingSpotResponseDto createParkingSpot(CreateParkingSpotRequest request, AppUserDetails userDetails);

    ParkingSpotResponseDto updateParkingSpotById(UpdateParkingSpotRequest request, Long ParkingSpotId, AppUserDetails userDetails);

    void deleteParkingSpotById(Long parkingSpotId, AppUserDetails userDetails);

    void deactivateParkingSpotById(Long parkingSpotId, AppUserDetails userDetails);

    void activateParkingSpotById(Long parkingSpotId, AppUserDetails userDetails);
}
