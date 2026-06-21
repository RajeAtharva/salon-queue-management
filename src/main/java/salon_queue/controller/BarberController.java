package salon_queue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import salon_queue.model.Barber;
import salon_queue.repository.BarberRepository;

import java.util.Locale;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/barbers")
public class BarberController {

    @Autowired
    private BarberRepository barberRepository;

    // Salon ke saare barbers
    @GetMapping("/salon/{salonId}")
    public List<Barber> getBarbersBySalon(@PathVariable Long salonId) {
        return barberRepository.findBySalonId(salonId);
    }

    // Naya barber add karo
    @PostMapping
    public Barber addBarber(@RequestBody Barber barber) {
        return barberRepository.save(barber);
    }

    // Barber ka status update karo (Available/Busy)
    @PatchMapping("/{id}/status")
    public Barber updateStatus(@PathVariable Long id, @RequestParam String status) {
        Barber barber = barberRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Barber not found"));
        barber.setStatus(parseStatus(status));
        return barberRepository.save(barber);
    }

    private Barber.Status parseStatus(String status) {
        try {
            return Barber.Status.valueOf(status.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException | NullPointerException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid barber status");
        }
    }
}
