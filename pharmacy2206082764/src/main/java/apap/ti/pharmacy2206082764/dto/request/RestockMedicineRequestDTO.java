package apap.ti.pharmacy2206082764.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestockMedicineRequestDTO {
    
    private String id;
    private String name;
    private int stock;

    @NotNull(message = "Jumlah obat tidak boleh kosong")
    @Min(value = 1, message = "Jumlah obat tidak boleh kurang dari 1")
    @Positive(message = "Jumlah obat tidak boleh negatif")
    private int addStock;

}
