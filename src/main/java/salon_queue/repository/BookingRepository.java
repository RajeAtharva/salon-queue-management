package salon_queue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import salon_queue.model.Booking;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findBySalonId(Long salonId);
    List<Booking> findBySalonIdAndStatus(Long salonId, Booking.BookingStatus status);
    long countBySalonIdAndStatus(Long salonId, Booking.BookingStatus status);
}