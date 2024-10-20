package apap.ti.pharmacy2206082764.dto.request;

import java.util.List;

import apap.ti.pharmacy2206082764.model.Medicine;
import apap.ti.pharmacy2206082764.model.Prescription;
import jakarta.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMedicineQuantityRequestDTO {
    
    @NotNull(message = "Medicine tidak boleh kosong")
    private Medicine medicine;

    @NotNull(message = "Prescription tidak boleh kosong")
    private Prescription prescription;

    private int quantity;
    private int fulfilledQty;

}
