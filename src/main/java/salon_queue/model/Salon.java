package salon_queue.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "salons")
public class Salon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String area;
    private String address;
    private String contact;
    private Double rating;
}