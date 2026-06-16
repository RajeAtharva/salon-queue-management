package salon_queue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import salon_queue.model.Barber;
import java.util.List;

@Repository
public interface BarberRepository extends JpaRepository<Barber, Long> {
    List<Barber> findBySalonId(Long salonId);
    List<Barber> findBySalonIdAndStatus(Long salonId, Barber.Status status);
}