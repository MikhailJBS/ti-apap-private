package apap.ti.pharmacy2206082764.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import apap.ti.pharmacy2206082764.repository.MedicineDb;
import apap.ti.pharmacy2206082764.controller.PrescriptionController.PrescriptionStatus;
import apap.ti.pharmacy2206082764.model.Medicine;
import apap.ti.pharmacy2206082764.model.MedicineQuantity;
import apap.ti.pharmacy2206082764.repository.MedicineQuantityDb;

import java.util.List;
import java.util.Arrays;

import java.util.Date;

@Service
public class MedicineServiceImpl implements MedicineService {
    
    @Autowired
    MedicineDb medicineDb;

    @Autowired
    MedicineQuantityDb medicineQuantityDb;

    @Override
    public List<Medicine> getMedicineList() {
        return medicineDb.findAll();
    }

    @Override
    public Medicine addMedicine(Medicine medicine) {
        long medicineCount = medicineDb.count();
        String newId = String.format("MED%03d", medicineCount + 1);
        medicine.setId(newId);
        return medicineDb.save(medicine);
    }

    @Override
    public Medicine getMedicineById(String id) {
        return medicineDb.findById(id).get();
    }

    @Override
    public boolean deleteMedicine(Medicine medicine) {
        // Check if there are any prescriptions with status 'created' or 'waiting for stock' containing this medicine

        List<MedicineQuantity> activeQuantities = medicineQuantityDb.findByMedicineAndPrescriptionStatusIn(
            medicine, 
            Arrays.asList(PrescriptionStatus.CREATED.getValue(), PrescriptionStatus.WAITING_FOR_STOCK.getValue())
        );

        if (!activeQuantities.isEmpty()) {
            // Cannot delete the medicine as it's part of active prescriptions
            return false;
        }

        // Implement soft delete
        medicine.setDeleted(true);
        medicineDb.save(medicine);
        return true;
    }

    @Override
    public Medicine updateMedicine(Medicine medicine) {
        return medicineDb.save(medicine);
    }

    @Override
    public Long countMedicine() {
        return medicineDb.count();
    }

    @Override
    public List<Medicine> getAvailableMedicine() {
        return medicineDb.findByIsDeletedIsFalse();
    }

}
