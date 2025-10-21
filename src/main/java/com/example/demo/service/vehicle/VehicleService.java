package com.example.demo.service.vehicle;

import com.example.demo.dto.ParkingSpotDto;
import com.example.demo.dto.VehicleDto;
import com.example.demo.exeptions.ActionNotAllowedException;
import com.example.demo.exeptions.ResourceNotFoundException;
import com.example.demo.model.ParkingSpot;
import com.example.demo.model.User;
import com.example.demo.model.Vehicle;
import com.example.demo.repository.UserRepository;
import com.example.demo.repository.VehicleRepository;
import com.example.demo.requests.vehicle.VehicleRequest;
import com.example.demo.security.user.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService implements IVehicleService {

    private final VehicleRepository vehicleRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public VehicleDto createVehicle(VehicleRequest request, AppUserDetails userDetails) {
        Vehicle vehicle = createVehicle(request);
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }
        vehicle.setCreatedBy(currentUser);
        vehicleRepository.save(vehicle);
        return convertToDto(vehicle);
    }
    //Helper method for create vehicle
    private Vehicle createVehicle(VehicleRequest request) {
        return new Vehicle(
            request.getModel(),
            request.getColor(),
            request.getPlate(),
            request.getLength_cm(),
            request.getWidth_cm()
        );
    }

    @Override
    public VehicleDto updateVehicle(VehicleRequest request, AppUserDetails userDetails, Long vehicleId) {
        Vehicle existingVehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }
        if(!existingVehicle.getCreatedBy().equals(currentUser)) {
            throw new ActionNotAllowedException("This is not current users vehicle");
        }
        updateExistingVehicle(request,existingVehicle);
        vehicleRepository.save(existingVehicle);
        return convertToDto(existingVehicle);
    }
    private void updateExistingVehicle(VehicleRequest request, Vehicle existingVehicle) {
        existingVehicle.setModel(request.getModel());
        existingVehicle.setColor(request.getColor());
        existingVehicle.setPlate(request.getPlate());
        existingVehicle.setLength_cm(request.getLength_cm());
        existingVehicle.setWidth_cm(request.getWidth_cm());
    }

    @Override
    public void deleteVehicle(AppUserDetails userDetails, Long vehicleId) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        Vehicle existingVehicle = vehicleRepository.findById(vehicleId).orElseThrow(() -> new ResourceNotFoundException("Vehicle not found"));
        if(!existingVehicle.getCreatedBy().equals(currentUser)) {
            throw new ActionNotAllowedException("This is not current users vehicle");
        }
        vehicleRepository.delete(existingVehicle);
    }

    @Override
    public List<VehicleDto> fetchVehiclesCreatedByActiveUser(AppUserDetails userDetails) {
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }
        List<Vehicle> vehicles = vehicleRepository.findByCreatedBy(currentUser);
        return getConvertedVehicles(vehicles);
    }

    /////// DTO CONVERTER METHODS /////////
    private VehicleDto convertToDto(Vehicle vehicle){
        return modelMapper.map(vehicle, VehicleDto.class);
    }

    private List<VehicleDto> getConvertedVehicles(List<Vehicle> vehicles){
        return vehicles.stream()
                .map(this :: convertToDto)
                .toList();
    }
}
