package salon_queue.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import salon_queue.model.Barber;
import salon_queue.model.Booking;
import salon_queue.model.ServicePrice;
import salon_queue.repository.BookingRepository;
import salon_queue.repository.ServicePriceRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final BookingRepository bookingRepository;
    private final ServicePriceRepository servicePriceRepository;

    public AnalyticsController(BookingRepository bookingRepository, ServicePriceRepository servicePriceRepository) {
        this.bookingRepository = bookingRepository;
        this.servicePriceRepository = servicePriceRepository;
    }

    @GetMapping("/salon/{salonId}")
    public Map<String, Object> getSalonAnalytics(@PathVariable Long salonId) {
        LocalDate today = LocalDate.now();
        LocalDateTime startOfToday = today.atStartOfDay();
        LocalDateTime startOfTomorrow = today.plusDays(1).atStartOfDay();

        List<Booking> todaysBookings = bookingRepository.findBySalonIdAndBookingTimeGreaterThanEqualAndBookingTimeLessThan(
                salonId, startOfToday, startOfTomorrow);
        List<Booking> completedToday = todaysBookings.stream()
                .filter(booking -> booking.getStatus() == Booking.BookingStatus.COMPLETED)
                .toList();
        long pendingNow = bookingRepository.countBySalonIdAndStatus(salonId, Booking.BookingStatus.PENDING);

        Map<String, Double> servicePrices = servicePriceRepository.findBySalonId(salonId).stream()
                .filter(servicePrice -> servicePrice.getPrice() != null)
                .collect(Collectors.toMap(
                        servicePrice -> normalizeServiceName(servicePrice.getServiceName()),
                        ServicePrice::getPrice,
                        (existing, replacement) -> replacement
                ));

        double totalRevenue = completedToday.stream()
                .map(Booking::getService)
                .filter(Objects::nonNull)
                .map(AnalyticsController::normalizeServiceName)
                .mapToDouble(serviceName -> servicePrices.getOrDefault(serviceName, 0.0))
                .sum();

        List<Map<String, Object>> peakHours = IntStream.range(8, 20)
                .mapToObj(hour -> Map.<String, Object>of(
                        "hour", formatHour(hour),
                        "bookingCount", countBookingsForHour(todaysBookings, hour)
                ))
                .toList();

        String topService = findMostBookedValue(todaysBookings, Booking::getService).orElse(null);
        String topBarber = findMostBookedValue(completedToday, booking -> {
            Barber barber = booking.getBarber();
            return barber == null ? null : barber.getName();
        }).orElse(null);

        Map<String, Object> analytics = new LinkedHashMap<>();
        analytics.put("totalBookingsToday", todaysBookings.size());
        analytics.put("completedToday", completedToday.size());
        analytics.put("pendingNow", pendingNow);
        analytics.put("totalRevenue", totalRevenue);
        analytics.put("peakHours", peakHours);
        analytics.put("topService", topService);
        analytics.put("topBarber", topBarber);
        return analytics;
    }

    private static long countBookingsForHour(List<Booking> bookings, int hour) {
        return bookings.stream()
                .filter(booking -> booking.getBookingTime() != null)
                .filter(booking -> booking.getBookingTime().getHour() == hour)
                .count();
    }

    private static Optional<String> findMostBookedValue(List<Booking> bookings, Function<Booking, String> valueExtractor) {
        return bookings.stream()
                .map(valueExtractor)
                .filter(Objects::nonNull)
                .filter(value -> !value.isBlank())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .max(Comparator.comparingLong(Map.Entry::getValue))
                .map(Map.Entry::getKey);
    }

    private static String normalizeServiceName(String serviceName) {
        return serviceName == null ? "" : serviceName.trim().toLowerCase(Locale.ROOT);
    }

    private static String formatHour(int hour) {
        return DateTimeFormatter.ofPattern("h a", Locale.ENGLISH).format(LocalDate.now().atTime(hour, 0));
    }
}
