package apap.ti.pharmacy2206082764.dto.request;

import java.util.List;

import apap.ti.pharmacy2206082764.model.Medicine;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMedicineRequestDTO {
    @NotBlank(message = "Nama obat tidak boleh kosong")
    @Size(max = 30, message = "Nama obat tidak boleh lebih dari 30 karakter")
    private String name;

    @NotNull(message = "Harga obat tidak boleh kosong")
    @Min(value = 1000, message = "Harga obat tidak boleh kurang dari 1000")
    private Long price;

    private int stock;

    @NotBlank(message = "Deskripsi obat tidak boleh kosong")
    private String description;
}
