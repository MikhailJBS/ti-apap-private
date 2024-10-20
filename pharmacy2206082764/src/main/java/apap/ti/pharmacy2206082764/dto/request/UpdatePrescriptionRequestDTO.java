package apap.ti.pharmacy2206082764.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePrescriptionRequestDTO extends AddPrescriptionRequestDTO {
    
    private String id;
    private List<UpdateMedicineQuantityRequestDTO> medicineUpdateQuantityList;
}
