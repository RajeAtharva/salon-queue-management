package salon_queue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import salon_queue.model.Booking;
import salon_queue.repository.BookingRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    // Naya booking karo
    @PostMapping
    public Booking createBooking(@RequestBody Booking booking) {
        booking.setBookingTime(LocalDateTime.now());
        booking.setStatus(Booking.BookingStatus.PENDING);
        return bookingRepository.save(booking);
    }

    // Salon ki saari bookings dekho
    @GetMapping("/salon/{salonId}")
    public List<Booking> getSalonBookings(@PathVariable Long salonId) {
        return bookingRepository.findBySalonId(salonId);
    }

    // Sirf queue mein kitne hain
    @GetMapping("/salon/{salonId}/queue")
    public Map<String, Object> getQueueInfo(@PathVariable Long salonId) {
        long pendingCount = bookingRepository.countBySalonIdAndStatus(
                salonId, Booking.BookingStatus.PENDING);
        List<Booking> queue = bookingRepository.findBySalonIdAndStatus(
                salonId, Booking.BookingStatus.PENDING);
        return Map.of(
                "totalInQueue", pendingCount,
                "estimatedWaitMinutes", pendingCount * 15,
                "queue", queue
        );
    }

    // Booking status update karo
    @PatchMapping("/{id}/status")
    public Booking updateStatus(@PathVariable Long id, @RequestParam String status) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Booking nahi mili"));
        booking.setStatus(Booking.BookingStatus.valueOf(status));
        return bookingRepository.save(booking);
    }
}