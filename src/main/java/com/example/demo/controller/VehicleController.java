package com.example.demo.controller;

import com.example.demo.dto.ParkingSpotDto;
import com.example.demo.dto.VehicleDto;
import com.example.demo.exeptions.ActionNotAllowedException;
import com.example.demo.exeptions.ResourceNotFoundException;
import com.example.demo.model.Vehicle;
import com.example.demo.requests.parkingspot.UpdateParkingSpotRequest;
import com.example.demo.requests.vehicle.VehicleRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.security.user.AppUserDetails;
import com.example.demo.service.parkingspot.IParkingSpotService;
import com.example.demo.service.vehicle.IVehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/vehicles")
public class VehicleController {

    private final IVehicleService vehicleService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> createVehicle(@RequestBody VehicleRequest request, @AuthenticationPrincipal AppUserDetails userDetails){
        try {
            VehicleDto  vehicleDto = vehicleService.createVehicle(request,userDetails);
            return ResponseEntity.ok(new ApiResponse("Vehicle created",vehicleDto));
        }
        catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage(),null));
        }
    }

    @DeleteMapping("/vehicle/{vehicleId}/delete")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> deleteVehicle(@PathVariable Long vehicleId, @AuthenticationPrincipal AppUserDetails userDetails){
        try{
            vehicleService.deleteVehicle(userDetails, vehicleId);
            return ResponseEntity.ok(new ApiResponse("Vehicle deleted", null));
        }catch (ResourceNotFoundException | ActionNotAllowedException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PutMapping("/vehicle/{vehicleId}/update")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> updateVehicle(@RequestBody VehicleRequest request, @PathVariable Long vehicleId, @AuthenticationPrincipal AppUserDetails userDetails){
        try{
            VehicleDto vehicleDto = vehicleService.updateVehicle(request,userDetails, vehicleId);
            return ResponseEntity.ok(new ApiResponse("Vehicle updated", vehicleDto));
        }catch (ResourceNotFoundException | ActionNotAllowedException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/all/user-vehicles")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> getCurrentUsersVehicles(@AuthenticationPrincipal AppUserDetails userDetails){
        try{
            List<VehicleDto> vehicles = vehicleService.fetchVehiclesCreatedByActiveUser(userDetails);
            return ResponseEntity.ok(new ApiResponse("Current users vehicles fetched: ", vehicles));
        }catch (ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse(e.getMessage(), null));
        }
    }

}
