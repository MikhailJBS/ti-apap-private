package apap.ti.pharmacy2206082764.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedicineQuantity {
    @Id
    @GeneratedValue
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "medicine_id")
    private Medicine medicine;

    @ManyToOne
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;

    private int quantity;
    private int fulfilledQty;
}
