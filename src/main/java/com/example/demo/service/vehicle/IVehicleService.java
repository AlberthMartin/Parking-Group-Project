package com.example.demo.service.vehicle;

import com.example.demo.dto.VehicleDto;
import com.example.demo.requests.vehicle.VehicleRequest;
import com.example.demo.security.user.AppUserDetails;

import java.util.List;

public interface IVehicleService {

    VehicleDto createVehicle(VehicleRequest request, AppUserDetails userDetails);

    VehicleDto updateVehicle(VehicleRequest request, AppUserDetails userDetails, Long vehicleId);

    void deleteVehicle(AppUserDetails userDetails, Long vehicleId);

    List<VehicleDto> fetchVehiclesCreatedByActiveUser(AppUserDetails userDetails);

    /**
     * List<ParkingSpotDto> fetchAllActiveParkingSpotsInGivenCity(String city);
     *
     *     //Fetch available spots in a city with given data range
     *     //Fetch all active spots in specific city
     *     List<ParkingSpotDto> fetchAllActiveParkingSpotsInGivenCityAndTimePeriod(String city, LocalDateTime startDate, LocalDateTime endDate);
     *
     *     //Create a users parking spot and save it to the database
     *     //@params createparkingSpot request and the app user details
     *     ParkingSpotDto createParkingSpot(CreateParkingSpotRequest request, AppUserDetails userDetails);
     *
     *     ParkingSpotDto updateParkingSpotById(UpdateParkingSpotRequest request, Long ParkingSpotId, AppUserDetails userDetails);
     *
     *     void deleteParkingSpotById(Long parkingSpotId, AppUserDetails userDetails);
     */
}
