package salon_queue.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import salon_queue.model.ServicePrice;
import salon_queue.repository.ServicePriceRepository;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/services")
public class ServicePriceController {

    private final ServicePriceRepository servicePriceRepository;

    public ServicePriceController(ServicePriceRepository servicePriceRepository) {
        this.servicePriceRepository = servicePriceRepository;
    }

    @GetMapping("/salon/{salonId}")
    public List<ServicePrice> getServicesBySalon(@PathVariable Long salonId) {
        return servicePriceRepository.findBySalonId(salonId);
    }

    @PostMapping
    public ServicePrice addServicePrice(@RequestBody ServicePrice servicePrice) {
        return servicePriceRepository.save(servicePrice);
    }

    @PutMapping("/{id}")
    public ServicePrice updateServicePrice(@PathVariable Long id, @RequestBody ServicePrice request) {
        ServicePrice servicePrice = servicePriceRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service price not found"));

        servicePrice.setPrice(request.getPrice());

        return servicePriceRepository.save(servicePrice);
    }
}
