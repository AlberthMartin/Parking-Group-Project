package com.example.demo.service.booking;

import com.example.demo.enums.BookingStatus;
import com.example.demo.model.Booking;
import com.example.demo.responseDtos.BookingResponseDto;
import com.example.demo.exeptions.ActionNotAllowedException;
import com.example.demo.exeptions.ResourceNotFoundException;
import com.example.demo.model.ParkingSpot;
import com.example.demo.model.User;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.ParkingSpotRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.security.user.AppUserDetails;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService implements IBookingService {
    private final BookingRepository bookingRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    //Rent the spot
    @Override
    public BookingResponseDto createBooking(AppUserDetails userDetails, Long spotId, LocalDateTime start, LocalDateTime end) {
        //Find renter and parking spot
        User renter = userRepository.findByEmail(userDetails.getUsername());
        if(renter == null){
            throw new ResourceNotFoundException("User not found");
        }

        ParkingSpot spot = parkingSpotRepository.findById(spotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking Spot not found"));

        //Prevent double booking
        boolean overlapping = bookingRepository.existsBySpotAndStart_timeLessThanEqualAndEnd_timeGreaterThanEqual(spot, start, end);
        if (overlapping) {
            throw new ActionNotAllowedException("Parking spot is already booked during this period");
        }

        //Calculate total price
        long hours = Duration.between(start, end).toHours();
        if (hours == 0) hours = 1; //minimum charge 1 hour

        double price;
        //Price per day
        if (hours > 24) {
            int days = (int) (hours / 24);
            price = spot.getPrice_per_day() * days;
        } else {
            price = spot.getPrice_per_hour() * hours;
        }
        Booking booking = new Booking();
        booking.setRenter(renter);
        booking.setSpot(spot);
        booking.setStart_time(start);
        booking.setEnd_time(end);
        booking.setTotalAmount(price);
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        return convertToDto(savedBooking);
    }


    //TODO: Maybe this is needed for payment
    /*
    public BookingResponseDto confirmBooking(AppUserDetails userDetails, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        User owner = booking.getSpot().getCreatedBy();
        User currentUser = userRepository.findByEmail(userDetails.getUsername());

        if(currentUser.equals(owner)){
            booking.setStatus(BookingStatus.CONFIRMED);
            return convertToDto(bookingRepository.save(booking));
        }else{
            throw new ActionNotAllowedException("Only the owner can confirm booking");
        }
    }
*/
    //TODO: The booking can be cancelled if it is more than 1 hour until its active
    //only the one renting should be able to cancel othervise it will not be so user friendly if the hoster can
    //Candel it, because if they agreed to the spot being able to be rented then they should not be able to cancel a booking
    //they need to delete the whole spot before it is rented
    @Override
    public BookingResponseDto cancelBooking(AppUserDetails userDetails, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        User renter = userRepository.findByEmail(userDetails.getUsername());

        if(!booking.getRenter().equals(renter)){
            throw new ResourceNotFoundException("Only renter can cancel its own booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return convertToDto(bookingRepository.save(booking));
    }

    /* 1. The time of the booking runs out
    2. Review booking?
      Completed booking should have a review that
    After Booking is completed the host gets the money
    TODO: Finish this complete booking
     */

    public BookingResponseDto completeBooking(AppUserDetails userDetails, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        User renter = userRepository.findByEmail(userDetails.getUsername());

        if(!booking.getRenter().equals(renter)){
            throw new ResourceNotFoundException("Only renter can cancel its own booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        return convertToDto(bookingRepository.save(booking));
    }

    @Override
    public List<BookingResponseDto> getBookingsByRenter(Long renterId) {
        User renter = userRepository.findById(renterId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Booking> bookings = bookingRepository.findByRenter(renter);
        return getConvertedBookings(bookings);

    }

    @Override
    public List<BookingResponseDto> getBookingsBySpot(Long spotId) {
        ParkingSpot spot = parkingSpotRepository.findById(spotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking Spot not found"));
        List<Booking> bookings = bookingRepository.findBySpot(spot);
        return getConvertedBookings(bookings);
    }

    @Override
    public List<BookingResponseDto> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return getConvertedBookings(bookings);
    }

    // Dto converter Helper
    private BookingResponseDto convertToDto(Booking booking) {
        BookingResponseDto dto = modelMapper.map(booking, BookingResponseDto.class);
        dto.setRenterId(booking.getRenter().getId());
        dto.setRenterName(booking.getRenter().getEmail());
        dto.setSpotId(booking.getSpot().getId());
        dto.setSpotLocation(booking.getSpot().getAddress() + " " + booking.getSpot().getCity());
        dto.setStatus(booking.getStatus().name());
        return dto;
    }

    private List<BookingResponseDto> getConvertedBookings(List<Booking> bookings) {
        return bookings.stream()
                .map(this::convertToDto)
                .toList();
    }
}
