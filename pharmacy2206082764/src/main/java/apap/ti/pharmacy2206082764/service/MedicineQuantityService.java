package apap.ti.pharmacy2206082764.service;

import org.springframework.stereotype.Service;
import apap.ti.pharmacy2206082764.model.MedicineQuantity;
import java.util.List;
import java.util.UUID;

public interface MedicineQuantityService {
    
    List<MedicineQuantity> getMedicineQuantityByPrescriptionID(String idPerscription);
    MedicineQuantity getMedicineQuantityById(UUID id);
    MedicineQuantity updateMedicineQuantity(MedicineQuantity medicineQuantity);

}
