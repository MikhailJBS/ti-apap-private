package apap.ti.pharmacy2206082764.model;

import lombok.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicine {
    @Id
    private String id;

    @NotBlank(message = "Nama obat tidak boleh kosong")
    @Size(max = 30, message = "Nama obat tidak boleh lebih dari 30 karakter")
    private String name;

    @NotNull(message = "Harga obat tidak boleh kosong")
    @Min(value = 1000, message = "Harga obat tidak boleh kurang dari 1000")
    private Long price;

    @Min(value = 0, message = "Stok obat tidak boleh kurang dari 0")
    private int stock;

    @NotBlank(message = "Deskripsi obat tidak boleh kosong")
    @Size(max = 100, message = "Deskripsi obat tidak boleh lebih dari 100 karakter")
    private String description;

    private boolean isDeleted;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedDate;

    @PrePersist
    protected void onCreate() {
        this.createdDate = new Date();
        this.updatedDate = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedDate = new Date();
    }
}
