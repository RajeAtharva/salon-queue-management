package salon_queue.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import salon_queue.model.Review;
import salon_queue.model.Salon;
import salon_queue.repository.ReviewRepository;
import salon_queue.repository.SalonRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewRepository reviewRepository;
    private final SalonRepository salonRepository;

    public ReviewController(ReviewRepository reviewRepository, SalonRepository salonRepository) {
        this.reviewRepository = reviewRepository;
        this.salonRepository = salonRepository;
    }

    @GetMapping("/salon/{salonId}")
    public List<Review> getReviewsBySalon(@PathVariable Long salonId) {
        return reviewRepository.findBySalonIdOrderByCreatedAtDesc(salonId);
    }

    @GetMapping("/salon/{salonId}/average")
    public Map<String, Object> getAverageRating(@PathVariable Long salonId) {
        Double average = reviewRepository.findAverageRatingBySalonId(salonId);
        long count = reviewRepository.countBySalonId(salonId);

        return Map.of(
                "average", average == null ? 0.0 : average,
                "count", count
        );
    }

    @PostMapping
    public Review createReview(@RequestBody ReviewRequest request) {
        if (request == null || request.salonId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Salon id is required");
        }

        if (request.rating == null || request.rating < 1 || request.rating > 5) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rating must be between 1 and 5");
        }

        Salon salon = salonRepository.findById(request.salonId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Salon not found"));

        Review review = new Review();
        review.setSalon(salon);
        review.setCustomerName(request.customerName);
        review.setRating(request.rating);
        review.setComment(request.comment);
        review.setCreatedAt(LocalDateTime.now());

        return reviewRepository.save(review);
    }

    public static class ReviewRequest {
        public Long salonId;
        public String customerName;
        public Integer rating;
        public String comment;
    }
}
