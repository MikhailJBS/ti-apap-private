package apap.ti.pharmacy2206082764.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMedicineRequestDTO extends AddMedicineRequestDTO {
    
    @NotBlank(message = "ID obat tidak boleh kosong")
    private String id;

}
