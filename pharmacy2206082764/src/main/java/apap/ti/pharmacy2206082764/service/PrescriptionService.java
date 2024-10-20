package apap.ti.pharmacy2206082764.service;

import java.util.List;
import apap.ti.pharmacy2206082764.model.Prescription;
import apap.ti.pharmacy2206082764.controller.PrescriptionController.PrescriptionStatus;
import apap.ti.pharmacy2206082764.model.MedicineQuantity;
import java.util.UUID;

public interface PrescriptionService {
    
    public List<Prescription> getPrescriptionList();
    public Prescription addPrescription(Prescription prescription, List<MedicineQuantity> medicineQuantityList);
    public Prescription getPrescriptionById(String id);
    public Prescription updatePrescription(Prescription prescription, List<MedicineQuantity> medicineQuantityList);
    public void deletePrescription(Prescription prescription);
    public Prescription updatePrescription(Prescription prescription);
    public Long countPrescriptionToProcess();
    List<Prescription> getPrescriptionsByStatus(int status);
    public void processPrescription(String idPrescription, UUID pharmacistId); 

}
