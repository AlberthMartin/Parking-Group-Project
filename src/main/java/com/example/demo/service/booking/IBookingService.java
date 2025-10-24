package com.example.demo.service.booking;

import com.example.demo.responseDtos.BookingResponseDto;
import com.example.demo.security.user.AppUserDetails;

import java.time.LocalDateTime;
import java.util.List;

public interface IBookingService {

    //When created then the booking is PENDING then CONFIRMED or CANCELLED,
    //   then COMPLETED
    //public BookingResponseDto confirmBooking (Long bookingId);

    BookingResponseDto createBooking(AppUserDetails userDetails, Long spotId, LocalDateTime start, LocalDateTime end);

    //Cancel a booking can do 1 h before booking
    public BookingResponseDto cancelBooking (AppUserDetails userDetails, Long bookingId);

    //Get booking by renter
    public List<BookingResponseDto> getBookingsByRenter(Long renterId);

    //Get bookings by spot
    public List<BookingResponseDto> getBookingsBySpot(Long spotId);

    List<BookingResponseDto> getAllBookings();
}
