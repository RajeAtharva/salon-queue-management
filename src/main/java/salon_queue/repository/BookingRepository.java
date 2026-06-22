package salon_queue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import salon_queue.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBySalonId(Long salonId);
    List<Booking> findByCustomerPhoneOrderByBookingTimeDesc(String customerPhone);
    List<Booking> findBySalonIdAndStatus(Long salonId, Booking.BookingStatus status);
    List<Booking> findBySalonIdAndBookingTimeGreaterThanEqualAndBookingTimeLessThan(
            Long salonId, LocalDateTime start, LocalDateTime end);
    long countBySalonIdAndStatus(Long salonId, Booking.BookingStatus status);
}
