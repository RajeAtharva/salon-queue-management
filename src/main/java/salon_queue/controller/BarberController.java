package salon_queue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import salon_queue.model.Barber;
import salon_queue.repository.BarberRepository;
import java.util.List;

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
                .orElseThrow(() -> new RuntimeException("Barber nahi mila"));
        barber.setStatus(Barber.Status.valueOf(status));
        return barberRepository.save(barber);
    }
}