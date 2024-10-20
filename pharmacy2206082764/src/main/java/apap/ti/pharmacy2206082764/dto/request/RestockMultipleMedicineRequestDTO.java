package apap.ti.pharmacy2206082764.dto.request;

import java.util.List;

import apap.ti.pharmacy2206082764.model.Medicine;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestockMultipleMedicineRequestDTO {
    
    private List<Medicine> restockMedicineList;

}
