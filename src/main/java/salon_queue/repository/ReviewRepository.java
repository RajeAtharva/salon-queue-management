package salon_queue.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import salon_queue.model.Review;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findBySalonIdOrderByCreatedAtDesc(Long salonId);

    long countBySalonId(Long salonId);

    @Query("select avg(r.rating) from Review r where r.salon.id = :salonId")
    Double findAverageRatingBySalonId(@Param("salonId") Long salonId);
}
