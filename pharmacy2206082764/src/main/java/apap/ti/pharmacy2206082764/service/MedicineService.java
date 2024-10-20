package apap.ti.pharmacy2206082764.service;

import apap.ti.pharmacy2206082764.model.Medicine;
import java.util.List;

public interface MedicineService {
    
    Medicine addMedicine(Medicine medicine);
    List<Medicine> getMedicineList();
    Medicine getMedicineById(String id);
    Medicine updateMedicine(Medicine medicine);
    boolean deleteMedicine(Medicine medicine);
    Long countMedicine();
    List<Medicine> getAvailableMedicine();

}
