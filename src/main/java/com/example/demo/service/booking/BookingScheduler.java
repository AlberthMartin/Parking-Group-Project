package com.example.demo.service.booking;
import com.example.demo.enums.BookingStatus;
import com.example.demo.model.Booking;
import com.example.demo.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/*
    Scheduler to update booking statuses based on time.
    - Activates bookings when start time is reached.
    - Completes bookings when end time is reached.
    - Cancels unpaid pending bookings after a timeout period 15 minutes.
 */

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingScheduler {
    private final BookingRepository bookingRepository;

    /**
     * Runs every 5 minutes to keep booking states in sync with time.
     */
    @Scheduled(fixedRate = 300_000) // 5 minutes in milliseconds
    @Transactional
    public void updateBookingStatuses() {
        LocalDateTime now = LocalDateTime.now();

        activateBookings(now);
        completeBookings(now);
        cancelUnpaidPendingBookings(now);
    }

    private void activateBookings(LocalDateTime now) {
        List<Booking> toActivate = bookingRepository.findByStatusAndStartTimeBefore(
                BookingStatus.CONFIRMED, now
        );

        for (Booking booking : toActivate) {
            booking.setStatus(BookingStatus.ACTIVE);
            log.info("Booking {} activated (spot {})", booking.getId(), booking.getSpot().getId());
        }
        bookingRepository.saveAll(toActivate);
    }


    private void completeBookings(LocalDateTime now) {
        List<Booking> toComplete = bookingRepository.findByStatusAndEndTimeBefore(
                BookingStatus.ACTIVE, now
        );

        for (Booking booking : toComplete) {
            booking.setStatus(BookingStatus.COMPLETED);
            log.info("Booking {} completed (spot {})", booking.getId(), booking.getSpot().getId());
            // TODO: trigger payment release to owner
        }
        bookingRepository.saveAll(toComplete);
    }

    private void cancelUnpaidPendingBookings(LocalDateTime now) {
        LocalDateTime cutoff = now.minusMinutes(15); // configurable timeout
        List<Booking> toCancel = bookingRepository.findByStatusAndCreatedAtBefore(
                BookingStatus.PENDING, cutoff
        );

        for (Booking booking : toCancel) {
            booking.setStatus(BookingStatus.CANCELLED);
            log.info("Booking {} cancelled due to inactivity", booking.getId());
            // TODO: trigger refund if payment was pre-authorized
        }
        bookingRepository.saveAll(toCancel);
    }
}
