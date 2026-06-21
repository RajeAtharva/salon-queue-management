package salon_queue.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import salon_queue.model.Salon;
import salon_queue.repository.SalonRepository;
import java.util.List;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/salons")
public class SalonController {

    @Autowired
    private SalonRepository salonRepository;

    @GetMapping
    public List<Salon> getAllSalons() {
        return salonRepository.findAll();
    }

    @GetMapping("/area/{area}")
    public List<Salon> getSalonsByArea(@PathVariable String area) {
        return salonRepository.findByArea(area);
    }

    @PostMapping
    public Salon addSalon(@RequestBody Salon salon) {
        return salonRepository.save(salon);
    }
}