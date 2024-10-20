package apap.ti.pharmacy2206082764.model;

import lombok.*;
import jakarta.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prescription {
    @Id
    private String id;

    @ManyToOne
    @JoinColumn(name = "pharmacist_id")
    private Pharmacist pharmacist;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    private Long totalPrice;

    private int status; // 0: Created, 1: Waiting for stock, 2: Done, 3: Cancelled

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = new Date();
        this.updatedDate = new Date();
    }

    @PreUpdate
    public void preUpdate() {
        if (this.createdDate == null) {
            this.createdDate = new Date();
        }
        this.updatedDate = new Date();
    }
}
