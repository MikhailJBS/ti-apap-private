package apap.ti.pharmacy2206082764.service;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.beans.factory.annotation.Autowired;

import apap.ti.pharmacy2206082764.repository.MedicineDb;
import apap.ti.pharmacy2206082764.controller.PrescriptionController.PrescriptionStatus;
import apap.ti.pharmacy2206082764.model.Medicine;
import apap.ti.pharmacy2206082764.repository.PatientDb;
import apap.ti.pharmacy2206082764.model.Patient;
import apap.ti.pharmacy2206082764.model.Pharmacist;
import apap.ti.pharmacy2206082764.repository.PrescriptionDb;
import apap.ti.pharmacy2206082764.model.Prescription;
import apap.ti.pharmacy2206082764.model.MedicineQuantity;
import apap.ti.pharmacy2206082764.repository.MedicineQuantityDb;
import apap.ti.pharmacy2206082764.service.PharmacistService;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.UUID;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {

    private final MedicineDb medicineDb;
    private final PrescriptionDb prescriptionDb;
    private final MedicineQuantityDb medicineQuantityDb;
    private final PharmacistService pharmacistService;

    public PrescriptionServiceImpl(MedicineDb medicineDb, PrescriptionDb prescriptionDb,
    MedicineQuantityDb medicineQuantityDb, PharmacistService PharmacistService) {
        this.medicineDb = medicineDb;
        this.prescriptionDb = prescriptionDb;
        this.medicineQuantityDb = medicineQuantityDb;
        this.pharmacistService = PharmacistService;
    }

    @Override
    public List<Prescription> getPrescriptionList() {
        return prescriptionDb.findAll();
    }

    @Override
    public Prescription addPrescription(Prescription prescription, List<MedicineQuantity> medicineQuantities) {
        // Step 1: First three characters are 'RES'
        StringBuilder idBuilder = new StringBuilder("RES");
    
        // Step 2: Get the number of medicines and extract the last two digits (if more than 99)
        int numberOfMedicines = medicineQuantities.size();
        String medicineCount = String.format("%02d", numberOfMedicines % 100);  // Only take last 2 digits
        idBuilder.append(medicineCount);
    
        // Step 3: Get the current day of the week
        Calendar calendar = Calendar.getInstance();
        String[] dayOfWeek = {"SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT"};
        String day = dayOfWeek[calendar.get(Calendar.DAY_OF_WEEK) - 1];  // Calendar's WEEK starts from 1
        idBuilder.append(day);
    
        // Step 4: Get the current time in 'HH:mm:ss' format
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
        String currentTime = timeFormat.format(calendar.getTime());
        idBuilder.append(currentTime);
    
        // Step 5: Set the ID and save the prescription
        String newId = idBuilder.toString();
        prescription.setId(newId);
        
        // Counting Total Price
        Long totalPrice = 0L;
        for (MedicineQuantity medicineQuantity : medicineQuantities) {
            Medicine medicine = medicineDb.findById(medicineQuantity.getMedicine().getId()).get();
            totalPrice += medicine.getPrice() * medicineQuantity.getQuantity();
        }

        prescription.setTotalPrice(totalPrice);

        // Persist the prescription object
        prescription = prescriptionDb.save(prescription);

        // Step 6: Save the medicine quantities
        for (MedicineQuantity medicineQuantity : medicineQuantities) {
            medicineQuantity.setPrescription(prescription);
            medicineQuantityDb.save(medicineQuantity);
        }

        return prescription;
    }

    @Override
    public Prescription getPrescriptionById(String id) {
        return prescriptionDb.findById(id).get();
    }

    @Override
    public Prescription updatePrescription(Prescription prescription, List<MedicineQuantity> updatedMedicineQuantities) {
        // Fetch the current list of MedicineQuantities for the prescription from the DB
        List<MedicineQuantity> existingMedicineQuantities = medicineQuantityDb.findAllByPrescription_Id(prescription.getId());

        Long totalPrice = 0L;
        for (MedicineQuantity medicineQuantity : updatedMedicineQuantities) {
            Medicine medicine = medicineDb.findById(medicineQuantity.getMedicine().getId()).get();
            totalPrice += medicine.getPrice() * medicineQuantity.getQuantity();
        }

        prescription.setTotalPrice(totalPrice);

        // Step 1: Update prescription details
        Prescription updatedPrescription = prescriptionDb.save(prescription);

        // Step 2: Loop through the updatedMedicineQuantities to update existing ones or add new ones
        for (MedicineQuantity updatedMq : updatedMedicineQuantities) {
            // Check if this medicine quantity already exists
            MedicineQuantity existingMq = existingMedicineQuantities.stream()
                .filter(mq -> mq.getId().equals(updatedMq.getId()))
                .findFirst()
                .orElse(null);

            if (existingMq != null) {
                // Update the existing medicine quantity
                existingMq.setMedicine(updatedMq.getMedicine());
                existingMq.setQuantity(updatedMq.getQuantity());
                existingMq.setFulfilledQty(updatedMq.getFulfilledQty());
                medicineQuantityDb.save(existingMq);
            } else {
                // If it doesn't exist, add the new MedicineQuantity
                updatedMq.setPrescription(updatedPrescription); // Make sure to link the prescription
                medicineQuantityDb.save(updatedMq);
            }
        }

        // Step 3: Remove medicine quantities that are no longer in the updated list
        List<UUID> updatedMqIds = updatedMedicineQuantities.stream()
                .map(MedicineQuantity::getId)
                .collect(Collectors.toList());

        for (MedicineQuantity existingMq : existingMedicineQuantities) {
            if (!updatedMqIds.contains(existingMq.getId())) {
                // This MedicineQuantity no longer exists in the updated list, so delete it
                medicineQuantityDb.delete(existingMq);
            }
        }

        return updatedPrescription;
    }

    @Override
    public void deletePrescription(Prescription prescription) {
        prescriptionDb.delete(prescription);
    }

    @Override
    public Prescription updatePrescription(Prescription prescription) {
        return prescriptionDb.save(prescription);
    }

    @Override
    public Long countPrescriptionToProcess() {
        return prescriptionDb.findAll().stream()
                .filter(prescription -> prescription.getStatus() == 0 || prescription.getStatus() == 1)
                .count();
    }

    @Override
    public List<Prescription> getPrescriptionsByStatus(int status) {
        return prescriptionDb.findByStatus(status);
    }

    @Override
    public void processPrescription(String prescriptionId, UUID pharmacistId) {
        Prescription prescription = prescriptionDb.findById(prescriptionId)
                .orElseThrow(() -> new IllegalArgumentException("Prescription not found"));
    
        List<MedicineQuantity> medicineQuantities = medicineQuantityDb.findAllByPrescription_Id(prescriptionId);
        boolean allFulfilled = true;
        boolean anyStockChanged = false; // Track if any stock was reduced
    
        // Loop through each medicine quantity and update stock and fulfilled quantity
        for (MedicineQuantity mq : medicineQuantities) {
            Medicine medicine = mq.getMedicine();
            int requestedQty = mq.getQuantity();
            int currentStock = medicine.getStock();
            int fulfilledQty = mq.getFulfilledQty();
            int stockNeeded = requestedQty - fulfilledQty;
    
            if (requestedQty == fulfilledQty) {
                continue;
            } else if (stockNeeded <= currentStock) {
                mq.setFulfilledQty(requestedQty);
                medicine.setStock(currentStock - stockNeeded);
                anyStockChanged = true; // Stock was reduced
            } else if (currentStock == 0) {
                mq.setFulfilledQty(fulfilledQty);
                medicine.setStock(0);
                allFulfilled = false;
            } else {
                mq.setFulfilledQty(currentStock);
                medicine.setStock(0);
                allFulfilled = false;
                anyStockChanged = true; // Stock was partially reduced
            }
    
            medicineQuantityDb.save(mq);  // Update the MedicineQuantity in the database
            medicineDb.save(medicine);    // Update the Medicine in the database
        }
    
        // Set the prescription status
        if (allFulfilled) {
            prescription.setStatus(PrescriptionStatus.DONE.getValue()); // 2 for DONE
        } else if (!anyStockChanged) {
            prescription.setStatus(PrescriptionStatus.CREATED.getValue()); // Remain in CREATED if no stock changed
        } else {
            prescription.setStatus(PrescriptionStatus.WAITING_FOR_STOCK.getValue()); // 1 for WAITING_FOR_STOCK
        }
    
        // Set the pharmacist processing the prescription
        Pharmacist pharmacist = pharmacistService.getPharmacistById(pharmacistId);
        prescription.setPharmacist(pharmacist);
    
        // Update the prescription in the database
        prescriptionDb.save(prescription);
    }    

}
