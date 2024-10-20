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
public class Patient {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true, nullable = false)
    private String nik;

    private String name;
    private String email;
    private Boolean gender; // true: Perempuan, false: Laki-Laki
    private Date birthDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @PrePersist
    public void prePersist() {
        this.createdDate = new Date();
        this.updatedDate = new Date();
    }
}

