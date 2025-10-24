package com.example.demo.controller;

import com.example.demo.responseDtos.ParkingSpotResponseDto;
import com.example.demo.exeptions.ActionNotAllowedException;
import com.example.demo.exeptions.ResourceNotFoundException;
import com.example.demo.requests.parkingspot.CreateParkingSpotRequest;
import com.example.demo.requests.parkingspot.UpdateParkingSpotRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.security.user.AppUserDetails;
import com.example.demo.service.parkingspot.IParkingSpotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/parking-spots")
public class ParkingSpotController {
    private final IParkingSpotService parkingSpotService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> createParkingSpot(@RequestBody CreateParkingSpotRequest request, @AuthenticationPrincipal AppUserDetails userDetails){
        try{
            ParkingSpotResponseDto parkingSpotResponseDto = parkingSpotService.createParkingSpot(request, userDetails);
            return ResponseEntity.ok(new ApiResponse("Parking spot created", parkingSpotResponseDto));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @DeleteMapping("/parking-spot/{parkingSpotId}/delete")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> deleteParkingSpot(@PathVariable Long parkingSpotId, @AuthenticationPrincipal AppUserDetails userDetails){
        try{
            parkingSpotService.deleteParkingSpotById(parkingSpotId, userDetails);
            return ResponseEntity.ok(new ApiResponse("Parking spot deleted", null));
        }catch (ResourceNotFoundException | ActionNotAllowedException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/parking-spot/{parkingSpotId}/update")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> updateParkingSpot(@RequestBody UpdateParkingSpotRequest request, @PathVariable Long parkingSpotId, @AuthenticationPrincipal AppUserDetails userDetails){
        try{
            ParkingSpotResponseDto parkingSpotResponseDto = parkingSpotService.updateParkingSpotById(request,parkingSpotId, userDetails);
            return ResponseEntity.ok(new ApiResponse("Parking spot updated", parkingSpotResponseDto));
        }catch (ResourceNotFoundException | ActionNotAllowedException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/all/active-spots")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> getAllActiveParkingSpots(){
        try{
            List<ParkingSpotResponseDto> activeParkingSpots = parkingSpotService.fetchAllActiveParkingSpots();
            return ResponseEntity.ok(new ApiResponse("All active parking spots found.", activeParkingSpots));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/all/user-spots")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> getAllUsersParkingSpots(@AuthenticationPrincipal AppUserDetails userDetails){
        try{
            List<ParkingSpotResponseDto> usersParkingSpots = parkingSpotService.fetchParkingSpotsByUserId(userDetails);
            return ResponseEntity.ok(new ApiResponse("All current users spots fetched successfully.", usersParkingSpots));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/all/active-spots/{city}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> getAllActiveParkingSpots(@PathVariable String city){
        try{
            List<ParkingSpotResponseDto> spots = parkingSpotService.fetchAllActiveParkingSpotsInGivenCity(city);
            return ResponseEntity.ok(new ApiResponse("All active parking spots in: "+ city +", found.", spots));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/all/active-spots/{city}/from/{startTime}/to/{endTime}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> getAllActiveParkingSpotsInGivenCityBetweenStartDateAndEndDate(@PathVariable String city, @PathVariable LocalDateTime startTime, @PathVariable LocalDateTime endTime){
        try{
            List<ParkingSpotResponseDto> spots =
              parkingSpotService.fetchAllActiveParkingSpotsInGivenCityAndTimePeriod(city, startTime, endTime);
            return ResponseEntity.ok(new ApiResponse("All active parking spots in: "+ city +" active between"+ startTime +" and "+ endTime +", found.", spots));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    //TODO: Deactivate and Activate parking spots
    @PutMapping("/parking-spot/{parkingSpotId}/deactivate")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> deactivateParkingSpot(@AuthenticationPrincipal AppUserDetails userDetails, @PathVariable Long parkingSpotId){
        try{
            parkingSpotService.deactivateParkingSpotById(parkingSpotId, userDetails);
            return ResponseEntity.ok(new ApiResponse("Parking spot deactivated", null));
        }catch (ResourceNotFoundException | ActionNotAllowedException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/parking-spot/{parkingSpotId}/activate")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> activateParkingSpot(@AuthenticationPrincipal AppUserDetails userDetails, @PathVariable Long parkingSpotId){
        try{
            parkingSpotService.activateParkingSpotById(parkingSpotId, userDetails);
            return ResponseEntity.ok(new ApiResponse("Parking spot activated", null));
        }catch (ResourceNotFoundException | ActionNotAllowedException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        }
    }

}
