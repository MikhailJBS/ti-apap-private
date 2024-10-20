package apap.ti.pharmacy2206082764.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import apap.ti.pharmacy2206082764.model.MedicineQuantity;
import apap.ti.pharmacy2206082764.repository.MedicineQuantityDb;

import java.util.List;
import java.util.UUID;

@Service
public class MedicineQuantityServiceImpl implements MedicineQuantityService {
    
    @Autowired
    MedicineQuantityDb medicineQuantityDb;

    @Override
    public List<MedicineQuantity> getMedicineQuantityByPrescriptionID(String idPerscription) {
        return medicineQuantityDb.findAllByPrescription_Id(idPerscription);
    }

    @Override
    public MedicineQuantity getMedicineQuantityById(UUID id) {
        return medicineQuantityDb.findById(id).get();
    }

    @Override
    public MedicineQuantity updateMedicineQuantity(MedicineQuantity medicineQuantity) {
        return medicineQuantityDb.save(medicineQuantity);
    }

}
