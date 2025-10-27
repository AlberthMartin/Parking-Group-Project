package com.example.demo.controller;

import com.example.demo.exeptions.ActionNotAllowedException;
import com.example.demo.exeptions.ResourceNotFoundException;
import com.example.demo.model.Booking;
import com.example.demo.requests.booking.CreateBookingRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.responseDtos.BookingResponseDto;
import com.example.demo.security.user.AppUserDetails;
import com.example.demo.service.booking.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/bookings")
public class BookingController {

    private final BookingService IBookingService;

    /** CREATE a booking
     *
     * @param request
     * @param userDetails
     * @return ApiResponse with created booking details
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> createBooking(@RequestBody CreateBookingRequest request,
                                                     @AuthenticationPrincipal AppUserDetails userDetails) {
        try{
            BookingResponseDto bookingResponseDto = IBookingService.createBooking(userDetails, request);
            return ResponseEntity.ok(new ApiResponse("Booking Created", bookingResponseDto));
        } catch (ResourceNotFoundException | ActionNotAllowedException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("Something went wrong when creating a booking", e.getMessage()));
        }
    }

    /** CANCEL booking 1 hour before start time
     *
     * @param bookingId
     * @param userDetails
     * @return ApiResponse with cancelled booking details
     */
    @PutMapping("/booking/{bookingId}/cancel")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> cancelBooking(@PathVariable Long bookingId,
                                                     @AuthenticationPrincipal AppUserDetails userDetails) {
        try{
            BookingResponseDto bookingResponseDto = IBookingService.cancelBooking(userDetails, bookingId);
            return ResponseEntity.ok(new ApiResponse("Booking cancelled", bookingResponseDto));
        } catch (ResourceNotFoundException | ActionNotAllowedException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("Something went wrong when cancelling booking: " + bookingId.toString(), e.getMessage()));
        }
    }

    /**
     * UPDATE booking details at least 1 hour
     * @param bookingId
     * @param request
     * @param userDetails
     * @return ApiResponse with updated booking details
     */
    @PutMapping("/{bookingId}/update")
    @PreAuthorize("hasRole('ROLE_USER')")
     public ResponseEntity<ApiResponse> updateBooking(@PathVariable Long bookingId,
                                                     @RequestBody CreateBookingRequest request,
                                                     @AuthenticationPrincipal AppUserDetails userDetails) {
        try {
            BookingResponseDto bookingResponseDto = IBookingService.updateBooking(userDetails, bookingId, request);
            return ResponseEntity.ok(new ApiResponse("Booking updated successfully", bookingResponseDto));
        } catch (ResourceNotFoundException | ActionNotAllowedException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("Error updating booking", e.getMessage()));
        }
    }

    /**
     * GET bookings by renter
     * @param renterId
     * @return ApiResponse with list of bookings for the renter
     */
    @GetMapping("/renter/{renterId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> getBookingsByRenter(@PathVariable Long renterId) {
        try {
            List<BookingResponseDto> bookings = IBookingService.getBookingsByRenter(renterId);
            return ResponseEntity.ok(new ApiResponse("Bookings fetched successfully", bookings));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("Error fetching bookings by renter", e.getMessage()));
        }
    }

    /**
     * GET bookings by parking spot
     * @param spotId
     * @return ApiResponse with list of bookings for the parking spot
     */
    @GetMapping("/spot/{spotId}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApiResponse> getBookingsBySpot(@PathVariable Long spotId) {
        try {
            List<BookingResponseDto> bookings = IBookingService.getBookingsBySpot(spotId);
            return ResponseEntity.ok(new ApiResponse("Bookings fetched successfully", bookings));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiResponse("Error fetching bookings by spot", e.getMessage()));
        }
    }

    /**
     * ADMIN: GET all bookings
     * @return ApiResponse with list of all bookings
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse> getAllBookings() {
        List<BookingResponseDto> bookings = IBookingService.getAllBookings();
        return ResponseEntity.ok(new ApiResponse("All bookings fetched successfully", bookings));
    }






}
