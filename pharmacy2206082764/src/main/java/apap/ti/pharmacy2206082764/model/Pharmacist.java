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
public class Pharmacist {
    @Id
    @GeneratedValue
    private UUID id;

    private String name;
    private String email;
    private Boolean gender; // true: Perempuan, false: Laki-Laki
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;
}

