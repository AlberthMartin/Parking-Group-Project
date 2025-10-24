package com.example.demo.controller;

import com.example.demo.model.Booking;
import com.example.demo.requests.booking.CreateBookingRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.responseDtos.BookingResponseDto;
import com.example.demo.security.user.AppUserDetails;
import com.example.demo.service.booking.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("${api.prefix}/bookings")
public class BookingController {

    private final BookingService IBookingService;

    /*
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createBooking(@RequestBody CreateBookingRequest request, @AuthenticationPrincipal AppUserDetails userDetails) {
        try{
            BookingResponseDto  bookingResponseDto = IBookingService.createBooking(request, userDetails)
        }
    }

     */


}
