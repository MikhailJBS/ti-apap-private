package apap.ti.pharmacy2206082764.dto.request;

import java.util.List;
import apap.ti.pharmacy2206082764.model.Medicine;
import apap.ti.pharmacy2206082764.model.Pharmacist;
import apap.ti.pharmacy2206082764.model.Patient;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPrescriptionRequestDTO {

    private Pharmacist pharmacist;

    private Patient patient;

    private Long totalPrice;

    @NotNull
    private int status;

    // Add a list for medicine quantities
    private List<AddMedicineQuantityRequestDTO> medicineQuantityList;

}
