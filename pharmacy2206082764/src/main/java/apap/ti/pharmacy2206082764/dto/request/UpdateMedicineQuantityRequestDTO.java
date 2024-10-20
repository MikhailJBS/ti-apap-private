package apap.ti.pharmacy2206082764.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateMedicineQuantityRequestDTO extends AddMedicineQuantityRequestDTO {
    
    private UUID id;
    
}
