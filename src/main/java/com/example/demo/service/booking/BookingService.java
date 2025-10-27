package com.example.demo.service.booking;

import com.example.demo.enums.BookingStatus;
import com.example.demo.model.Booking;
import com.example.demo.requests.booking.CreateBookingRequest;
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
import org.apache.coyote.BadRequestException;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
/**
Service for managing bookings of parking spots.
Provides methods to create, cancel, update, and retrieve bookings.
Handles business logic such as preventing double bookings and calculating total price.

Booking status explained:
PENDING: The booking has been created but not yet confirmed or paid for.
CONFIRMED: The payment is done
ACTIVE: The booking is currently active (the parking spot is being used).
COMPLETED: The booking has ended successfully.
CANCELLED: The booking has been cancelled by the renter.

Transitions
PENDING -> CONFIRMED (if payment is approved)
PENDING -> CANCELLED (if renter cancels before confirmation)
CONFIRMED -> ACTIVE (when the booking start time is reached)
ACTIVE -> COMPLETED (when the booking end time is reached)


 TODO: Integrate with payment gateway (e.g., Stripe) for handling payments.
    Inject a PaymentService and call:
    paymentService.preAuthorize(booking) inside createBooking()
    paymentService.refund(booking) inside cancelBooking()
    paymentService.adjust(booking) inside updateBooking()
    paymentService.capture(booking) inside your automated completion job
    */

@Service
@RequiredArgsConstructor
@Transactional
public class BookingService implements IBookingService {
    private final BookingRepository bookingRepository;
    private final ParkingSpotRepository parkingSpotRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    /** Create a new booking for a parking spot.
     * Validates the booking request to prevent double bookings and ensure proper time frames.
     * Calculates the total price based on the duration of the booking.
     */
    @Override
    public BookingResponseDto createBooking(AppUserDetails userDetails, CreateBookingRequest request) {

        Long spotId = request.getSpotId();
        LocalDateTime start = request.getStartTime();
        LocalDateTime end = request.getEndTime();

        //Find renter and parking spot
        User renter = userRepository.findByEmail(userDetails.getUsername());
        if (renter == null) {
            throw new ResourceNotFoundException("User not found");
        }

        ParkingSpot spot = parkingSpotRepository.findById(spotId)
                .orElseThrow(() -> new ResourceNotFoundException("Parking Spot not found"));

        //Simple validation for booking times
        validateBookingTimes(start, end);

        //Prevent double booking, with 5 minutes buffer
        boolean overlapping = bookingRepository.existsBySpotAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                spot,
                end.plusMinutes(5),
                start.minusMinutes(5)
        );
        if (overlapping) {
            throw new ActionNotAllowedException("Parking spot is already booked during this period (5 min buffer included)");
        }

        //Calculate total price
        BigDecimal price = calculatePrice(spot, start, end);

        Booking booking = new Booking();
        booking.setRenter(renter);
        booking.setSpot(spot);
        booking.setStartTime(start);
        booking.setEndTime(end);
        booking.setTotalAmount(price.doubleValue());
        booking.setStatus(BookingStatus.PENDING);

        Booking savedBooking = bookingRepository.save(booking);

        //TODO: Stripe pre-auth payment could be initiated here
        return convertToDto(savedBooking);
    }

    /**
    Cancel an existing booking.
    Only the renter who created the booking can cancel it.
    Bookings cannot be cancelled less than 1 hour before the start time.
     */
    @Override
    public BookingResponseDto cancelBooking(AppUserDetails userDetails, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User renter = userRepository.findByEmail(userDetails.getUsername());
        if (!booking.getRenter().equals(renter)) {
            throw new ActionNotAllowedException("Only renter can cancel its own booking");
        }

        if (Duration.between(LocalDateTime.now(), booking.getStartTime()).toMinutes() < 60) {
            throw new ActionNotAllowedException("Cannot cancel booking less than 1 hour before start");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.COMPLETED) {
            throw new ActionNotAllowedException("This booking cannot be cancelled anymore (already completed or cancelled)");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        //TODO: Refund logic (stripe) could be initiated here
        return convertToDto(booking);
    }

    /**
    Update an existing booking.
    Only the renter who created the booking can update it.
    Bookings cannot be updated less than 1 hour before the start time.
    Validates new booking times to prevent overlaps and ensure proper time frames.
    Recalculates the total price based on the new duration.
     */
    @Override
    public BookingResponseDto updateBooking(AppUserDetails userDetails, Long bookingId, CreateBookingRequest request) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        User renter = userRepository.findByEmail(userDetails.getUsername());
        if (!booking.getRenter().equals(renter)) {
            throw new ActionNotAllowedException("Only the renter can update their own booking");
        }

        // Ensure booking hasn't started yet
        if (Duration.between(LocalDateTime.now(), booking.getStartTime()).toMinutes() < 60) {
            throw new ActionNotAllowedException("Cannot update booking less than 1 hour before start");
        }

        if (booking.getStatus() != BookingStatus.PENDING && booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ActionNotAllowedException("Only pending or confirmed bookings can be updated");
        }

        LocalDateTime newStart = request.getStartTime();
        LocalDateTime newEnd = request.getEndTime();

        validateBookingTimes(newStart, newEnd);

        // Prevent overlap with other bookings on same spot
        boolean overlapping = bookingRepository.existsBySpotAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(
                booking.getSpot(),
                newEnd.plusMinutes(5),
                newStart.minusMinutes(5)
        );
        if (overlapping) {
            throw new ActionNotAllowedException("Parking spot is already booked during the new time period");
        }

        BigDecimal newPrice = calculatePrice(booking.getSpot(), newStart, newEnd);

        booking.setStartTime(newStart);
        booking.setEndTime(newEnd);
        booking.setTotalAmount(newPrice.doubleValue());
        bookingRepository.save(booking);

        // TODO: adjust payment pre-authorization amount in Stripe if changed
        return convertToDto(booking);
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

/**
     * Retrieve all bookings (admin use).
     */
@Override
public List<BookingResponseDto> getAllBookings() {
    List<Booking> bookings = bookingRepository.findAll();
    return getConvertedBookings(bookings);
}

 // Private Helper Methods

private void validateBookingTimes(LocalDateTime start, LocalDateTime end) {
    if (start.isAfter(end) || start.isEqual(end)) {
        throw new ActionNotAllowedException("Start time must be before end time");
    }
    if (start.isBefore(LocalDateTime.now())) {
        throw new ActionNotAllowedException("Cannot book in the past");
    }
    if (Duration.between(start, end).toMinutes() < 30) {
        throw new ActionNotAllowedException("Minimum booking time is 30 minutes");
    }
}

private BigDecimal calculatePrice(ParkingSpot spot, LocalDateTime start, LocalDateTime end) {
        long totalHours = Duration.between(start, end).toHours();
        if (totalHours == 0) totalHours = 1;

        long days = totalHours / 24;
        long hours = totalHours % 24;

        BigDecimal price = BigDecimal.ZERO;

        if (days > 0) {
            price = price.add(BigDecimal.valueOf(days)
                    .multiply(BigDecimal.valueOf(spot.getPrice_per_day())));
        }
        if (hours > 0) {
            price = price.add(BigDecimal.valueOf(hours)
                    .multiply(BigDecimal.valueOf(spot.getPrice_per_hour())));
        }

        return price.setScale(2, RoundingMode.HALF_UP);
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
