package com.example.demo.service.parkingspot;

import com.example.demo.dto.ParkingSpotDto;
import com.example.demo.exeptions.ActionNotAllowedException;
import com.example.demo.exeptions.ResourceNotFoundException;
import com.example.demo.model.ParkingSpot;
import com.example.demo.model.User;
import com.example.demo.repository.ParkingSpotRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.requests.parkingspot.CreateParkingSpotRequest;
import com.example.demo.requests.parkingspot.UpdateParkingSpotRequest;
import com.example.demo.security.user.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ParkingSpotService implements IParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    // Fetch all active spots
    @Override
    public List<ParkingSpotDto> fetchAllActiveParkingSpots(){
         List<ParkingSpot> parkingSpots = parkingSpotRepository.findByIsActiveTrue();
         return getConvertedParkingSpots(parkingSpots);
    }

    // Fetch all spots created by current user
    @Override
    public List<ParkingSpotDto> fetchParkingSpotsByUserId(AppUserDetails userDetails){

        User currentUser = userRepository.findByEmail(userDetails.getUsername());

        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }
         List<ParkingSpot> parkingSpots = parkingSpotRepository.findByCreatedBy(currentUser);
         return getConvertedParkingSpots(parkingSpots);
    }

    //Fetch all active spots in specific city
    @Override
    public List<ParkingSpotDto> fetchAllActiveParkingSpotsInGivenCity(String city){
         List<ParkingSpot> parkingSpots = parkingSpotRepository.findByCityIgnoreCaseAndIsActiveTrue(city);
         return getConvertedParkingSpots(parkingSpots);
    }

    //Fetch available spots in a city with given data range
    //Fetch all active spots in specific city
    @Override
    public List<ParkingSpotDto> fetchAllActiveParkingSpotsInGivenCityAndTimePeriod(String city, LocalDateTime startDate, LocalDateTime endDate){
         List<ParkingSpot> parkingSpots = parkingSpotRepository.findAvailableSpotsInCity(city, startDate, endDate);
         return getConvertedParkingSpots(parkingSpots);
    }

    @Override
    public ParkingSpotDto createParkingSpot(CreateParkingSpotRequest request, AppUserDetails userDetails) {
        //Create and fill in the needed info for a parking spot
        ParkingSpot parkingSpot = createParkingSpot(request);
        //fetch current user
        User currentUser = userRepository.findByEmail(userDetails.getUsername());

        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }
        //set curr user
        parkingSpot.setCreatedBy(currentUser);

        //Save to database
        parkingSpotRepository.save(parkingSpot);
        //return dto to controller
        return convertToDto(parkingSpot);
    }
    //Helper method to create values for a parking spot
    private ParkingSpot createParkingSpot(CreateParkingSpotRequest request) {
        return new ParkingSpot(
                request.getTitle(),
                request.getDescription(),
                request.getAddress(),
                request.getCity(),
                request.getPostal_code(),
                request.getCountry(),
                request.getLatitude(),
                request.getLongitude(),
                request.getAvailable_from(),
                request.getAvailable_to(),
                request.getPrice_per_hour(),
                request.getPrice_per_day()
        );
    }

    @Override
    public ParkingSpotDto updateParkingSpotById(UpdateParkingSpotRequest request, Long ParkingSpotId, AppUserDetails userDetails) {
        //get existing parking spot, see if it exists
        ParkingSpot existingParkingSpot = parkingSpotRepository.findById(ParkingSpotId)
                .orElseThrow(() -> new ResourceNotFoundException("ParkingSpot not found"));

        //get current user
        User currentUser = userRepository.findByEmail(userDetails.getUsername());
        //check that current user is not null
        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }
        //Check that the parking spot belongs to the user
        if(!existingParkingSpot.getCreatedBy().equals(currentUser)){
            throw new ActionNotAllowedException("This is not current users parking spot");
        }
        //Updates the parking spot values
        updateExistingParkingSpot(request, existingParkingSpot);

        //Save it to the database
        parkingSpotRepository.save(existingParkingSpot);

        //Return the dto to the parking spot controller
        return convertToDto(existingParkingSpot);
    }

    //Helper method for update parking
    private void updateExistingParkingSpot(UpdateParkingSpotRequest request, ParkingSpot existingParkingSpot) {
        existingParkingSpot.setTitle(request.getTitle());
        existingParkingSpot.setDescription(request.getDescription());
        existingParkingSpot.setAddress(request.getAddress());
        existingParkingSpot.setCity(request.getCity());
        existingParkingSpot.setPostal_code(request.getPostal_code());
        existingParkingSpot.setCountry(request.getCountry());
        existingParkingSpot.setLatitude(request.getLatitude());
        existingParkingSpot.setLongitude(request.getLongitude());
        existingParkingSpot.setAvailable_from(request.getAvailable_from());
        existingParkingSpot.setAvailable_to(request.getAvailable_to());
        existingParkingSpot.setPrice_per_hour(request.getPrice_per_hour());
        existingParkingSpot.setPrice_per_day(request.getPrice_per_day());
    }

    @Override
    public void deleteParkingSpotById(Long parkingSpotId, AppUserDetails userDetails) {

        User currentUser = userRepository.findByEmail(userDetails.getUsername());

        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        ParkingSpot existingParkingSpot = parkingSpotRepository.findById(parkingSpotId)
                .orElseThrow(() -> new ResourceNotFoundException("ParkingSpot not found"));

        if(existingParkingSpot.getCreatedBy().equals(currentUser)){
            parkingSpotRepository.delete(existingParkingSpot);
        } else{
            throw new ActionNotAllowedException("This is not current users parking spot");
        }
    }

    public void deactivateParkingSpotById(Long parkingSpotId, AppUserDetails userDetails) {
        ParkingSpot parkingSpot = parkingSpotRepository.findById(parkingSpotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking spot not found"));

        User currentUser = userRepository.findByEmail(userDetails.getUsername());

        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        if(!parkingSpot.getCreatedBy().equals(currentUser)){
            throw new ActionNotAllowedException("This is not current users parking spot");
        }

        if(!parkingSpot.isActive()){
            throw new ActionNotAllowedException("This parking spot is already deactivated");
        }

        parkingSpot.setActive(false);
        parkingSpotRepository.save(parkingSpot);
    }

    public void activateParkingSpotById(Long parkingSpotId, AppUserDetails userDetails) {
        ParkingSpot parkingSpot = parkingSpotRepository.findById(parkingSpotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking spot not found"));

        User currentUser = userRepository.findByEmail(userDetails.getUsername());

        if (currentUser == null) {
            throw new ResourceNotFoundException("User not found");
        }

        if(!parkingSpot.getCreatedBy().equals(currentUser)){
            throw new ActionNotAllowedException("This is not current parking spot");
        }

        if(parkingSpot.isActive()){
            throw new ActionNotAllowedException("This parking spot is already active");
        }
        //Activate parking spot
        parkingSpot.setActive(true);
        parkingSpotRepository.save(parkingSpot);

    }

    //Method to convert parking spot objects into parkingspot dtos
    private ParkingSpotDto convertToDto(ParkingSpot parkingSpot){
        return modelMapper.map(parkingSpot, ParkingSpotDto.class);
    }
    /** TODO: later add for images, ex :
     *public ProductDto convertToDto(Product product) {
     *         ProductDto productDto = modelMapper.map(product, ProductDto.class);
     *         List<Image> images = imageRepository.findByproductId(product.getId());
     *         List<ImageDto> imagesDto = images.stream()
     *                 .map(image -> modelMapper.map(image, ImageDto.class))
     *                 .toList();
     *         productDto.setImages(imagesDto);
     *         return productDto;
     *     }
     */

    private List<ParkingSpotDto> getConvertedParkingSpots(List<ParkingSpot> parkingSpots){
        return parkingSpots.stream()
                .map(this :: convertToDto)
                .toList();
    }


}
