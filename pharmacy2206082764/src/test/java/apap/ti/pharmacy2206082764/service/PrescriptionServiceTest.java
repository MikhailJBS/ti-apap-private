package apap.ti.pharmacy2206082764.service;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.UUID;
import java.util.List;

import apap.ti.pharmacy2206082764.model.Medicine;
import apap.ti.pharmacy2206082764.model.Prescription;
import apap.ti.pharmacy2206082764.model.MedicineQuantity;
import apap.ti.pharmacy2206082764.model.Pharmacist;
import apap.ti.pharmacy2206082764.repository.MedicineDb;
import apap.ti.pharmacy2206082764.repository.PrescriptionDb;
import apap.ti.pharmacy2206082764.repository.MedicineQuantityDb;
import apap.ti.pharmacy2206082764.service.PharmacistService; // Add this import

class PrescriptionServiceImplTest {

    @Mock
    private MedicineDb medicineDb;

    @Mock
    private PrescriptionDb prescriptionDb;

    @Mock
    private MedicineQuantityDb medicineQuantityDb;

    @Mock
    private PharmacistService pharmacistService; // Add this mock

    @InjectMocks
    private PrescriptionServiceImpl prescriptionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks before each test
    }

    @Test
    void testMarkAsDoneUpdatesMedicineStockAndPrescriptionStatus() {

        UUID pharmacistId = UUID.randomUUID();
        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(pharmacistId);
        pharmacist.setName("John Doe");
        pharmacist.setEmail("afas@gmail.com");

        // Arrange: Prepare the prescription and medicines
        String prescriptionId = "RES123";
        Prescription prescription = new Prescription();
        prescription.setId(prescriptionId);
        prescription.setStatus(0); // Created status

        // Mocking medicines and their quantities
        Medicine medicine1 = new Medicine();
        medicine1.setId("MED001");
        medicine1.setStock(50); // Initial stock of medicine1

        MedicineQuantity medicineQuantity1 = new MedicineQuantity();
        medicineQuantity1.setMedicine(medicine1);
        medicineQuantity1.setPrescription(prescription);
        medicineQuantity1.setQuantity(10); // 10 units requested
        medicineQuantity1.setFulfilledQty(0); // Initial fulfilled quantity is 0

        Medicine medicine2 = new Medicine();
        medicine2.setId("MED002");
        medicine2.setStock(5); // Initial stock of medicine2 (insufficient stock)

        MedicineQuantity medicineQuantity2 = new MedicineQuantity();
        medicineQuantity2.setMedicine(medicine2);
        medicineQuantity2.setPrescription(prescription);
        medicineQuantity2.setQuantity(10); // 10 units requested
        medicineQuantity2.setFulfilledQty(0); // Initial fulfilled quantity is 0

        List<MedicineQuantity> medicineQuantities = List.of(medicineQuantity1, medicineQuantity2);

        // Mock repository responses
        when(prescriptionDb.findById(prescriptionId)).thenReturn(Optional.of(prescription));
        when(medicineQuantityDb.findAllByPrescription_Id(prescriptionId)).thenReturn(medicineQuantities);
        when(medicineDb.findById("MED001")).thenReturn(Optional.of(medicine1));
        when(medicineDb.findById("MED002")).thenReturn(Optional.of(medicine2));
        
        // Mock pharmacistService to return the pharmacist
        when(pharmacistService.getPharmacistById(pharmacistId)).thenReturn(pharmacist);

        // Act: Mark the prescription as done
        prescriptionService.processPrescription(prescriptionId, pharmacistId);

        // Assert: Check the stock update and fulfilled quantities
        assertEquals(40, medicine1.getStock()); // 50 - 10 = 40
        assertEquals(10, medicineQuantity1.getFulfilledQty()); // Fully fulfilled

        assertEquals(0, medicine2.getStock()); // 5 - 5 = 0 (partial fulfillment)
        assertEquals(5, medicineQuantity2.getFulfilledQty()); // Partially fulfilled

        // Assert: Check prescription status
        assertEquals(1, prescription.getStatus()); // Waiting for stock
        verify(prescriptionDb, times(1)).save(prescription);
    }

    @Test
    void testMarkAsDoneWhenAllStockAvailable() {

        UUID pharmacistId = UUID.randomUUID();
        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(pharmacistId);
        pharmacist.setName("John Doe");
        pharmacist.setEmail("afas@gmail.com");

        // Arrange: Prepare the prescription and medicines
        String prescriptionId = "RES124";
        Prescription prescription = new Prescription();
        prescription.setId(prescriptionId);
        prescription.setStatus(0); // Created status

        // Mocking medicines and their quantities
        Medicine medicine1 = new Medicine();
        medicine1.setId("MED003");
        medicine1.setStock(100); // Sufficient stock

        MedicineQuantity medicineQuantity1 = new MedicineQuantity();
        medicineQuantity1.setMedicine(medicine1);
        medicineQuantity1.setPrescription(prescription);
        medicineQuantity1.setQuantity(20); // 20 units requested
        medicineQuantity1.setFulfilledQty(0); // Initial fulfilled quantity is 0

        List<MedicineQuantity> medicineQuantities = List.of(medicineQuantity1);

        // Mock repository responses
        when(prescriptionDb.findById(prescriptionId)).thenReturn(Optional.of(prescription));
        when(medicineQuantityDb.findAllByPrescription_Id(prescriptionId)).thenReturn(medicineQuantities);
        when(medicineDb.findById("MED003")).thenReturn(Optional.of(medicine1));

        // Act: Mark the prescription as done
        prescriptionService.processPrescription(prescriptionId, pharmacistId);

        // Assert: Check the stock update and fulfilled quantities
        assertEquals(80, medicine1.getStock()); // 100 - 20 = 80
        assertEquals(20, medicineQuantity1.getFulfilledQty()); // Fully fulfilled

        // Assert: Check prescription status is "done"
        assertEquals(2, prescription.getStatus()); // Done
        verify(prescriptionDb, times(1)).save(prescription);
    }

    @Test
    void testPrescriptionStatusRemainsCreatedIfNoStockReduction() {

        UUID pharmacistId = UUID.randomUUID();
        Pharmacist pharmacist = new Pharmacist();
        pharmacist.setId(pharmacistId);
        pharmacist.setName("John Doe");
        pharmacist.setEmail("afas@gmail.com");

        // Arrange: Prepare the prescription and medicines
        String prescriptionId = "RES125";
        Prescription prescription = new Prescription();
        prescription.setId(prescriptionId);
        prescription.setStatus(0); // Created status

        // Mocking medicines and their quantities
        Medicine medicine1 = new Medicine();
        medicine1.setId("MED004");
        medicine1.setStock(0); // No stock available

        MedicineQuantity medicineQuantity1 = new MedicineQuantity();
        medicineQuantity1.setMedicine(medicine1);
        medicineQuantity1.setPrescription(prescription);
        medicineQuantity1.setQuantity(5); // 5 units requested
        medicineQuantity1.setFulfilledQty(0); // Initial fulfilled quantity is 0

        List<MedicineQuantity> medicineQuantities = List.of(medicineQuantity1);

        // Mock repository responses
        when(prescriptionDb.findById(prescriptionId)).thenReturn(Optional.of(prescription));
        when(medicineQuantityDb.findAllByPrescription_Id(prescriptionId)).thenReturn(medicineQuantities);
        when(medicineDb.findById("MED004")).thenReturn(Optional.of(medicine1));

        // Act: Attempt to mark the prescription as done
        prescriptionService.processPrescription(prescriptionId, pharmacistId);

        // Assert: Check that stock remains unchanged
        assertEquals(0, medicine1.getStock()); // No stock reduction
        assertEquals(0, medicineQuantity1.getFulfilledQty()); // No quantity fulfilled

        // Assert: Check that prescription status does not change
        assertEquals(0, prescription.getStatus()); // Status remains "created"
        verify(prescriptionDb, times(1)).save(prescription);
    }
}
