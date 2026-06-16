package salon_queue.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "barbers")
public class Barber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "salon_id")
    private Salon salon;

    public enum Status {
        AVAILABLE, BUSY, OFF_DUTY
    }
}