package apap.ti.pharmacy2206082764.controller;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.validation.Valid;

import apap.ti.pharmacy2206082764.model.Medicine;
import apap.ti.pharmacy2206082764.model.Prescription;
import apap.ti.pharmacy2206082764.model.Patient;
import apap.ti.pharmacy2206082764.model.Pharmacist;
import apap.ti.pharmacy2206082764.model.MedicineQuantity;
import apap.ti.pharmacy2206082764.service.MedicineService;
import apap.ti.pharmacy2206082764.service.PrescriptionService;
import apap.ti.pharmacy2206082764.service.PatientService;
import apap.ti.pharmacy2206082764.service.PharmacistService;
import apap.ti.pharmacy2206082764.service.MedicineQuantityService;
import apap.ti.pharmacy2206082764.dto.request.AddMedicineQuantityRequestDTO;
import apap.ti.pharmacy2206082764.dto.request.AddMedicineRequestDTO;
import apap.ti.pharmacy2206082764.dto.request.AddPrescriptionRequestDTO;
import apap.ti.pharmacy2206082764.dto.request.UpdateMedicineRequestDTO;
import apap.ti.pharmacy2206082764.dto.request.UpdatePrescriptionRequestDTO;
import apap.ti.pharmacy2206082764.dto.request.AddPatientRequestDTO;
import apap.ti.pharmacy2206082764.dto.request.UpdateMedicineQuantityRequestDTO;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/prescription")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;
    private final MedicineService medicineService;
    private final PatientService patientService;
    private final PharmacistService pharmacistService;
    private final MedicineQuantityService medicineQuantityService;
    
    public PrescriptionController(PrescriptionService prescriptionService, MedicineService medicineService,
        PatientService patientService, PharmacistService pharmacistService,
        MedicineQuantityService medicineQuantityService) {

        this.prescriptionService = prescriptionService;
        this.medicineService = medicineService;
        this.patientService = patientService;
        this.pharmacistService = pharmacistService;
        this.medicineQuantityService = medicineQuantityService;
    }

    public enum PrescriptionStatus {
        CREATED(0, "Created", "secondary"),
        WAITING_FOR_STOCK(1, "Waiting for stock", "warning"),
        DONE(2, "Done", "success"),
        CANCELED(3, "Canceled", "danger");

        private final int value;
        private final String label;
        private final String colorClass;

        PrescriptionStatus(int value, String label, String colorClass) {
            this.value = value;
            this.label = label;
            this.colorClass = colorClass;
        }

        public int getValue() { return value; }
        public String getLabel() { return label; }
        public String getColorClass() { return colorClass; }

        public static PrescriptionStatus fromValue(int value) {
            for (PrescriptionStatus status : values()) {
                if (status.getValue() == value) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid PrescriptionStatus value: " + value);
        }

        public static PrescriptionStatus fromString(String status) {
            try {
                return PrescriptionStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid PrescriptionStatus: " + status);
            }
        }
        
    }

    @GetMapping("")
    public String viewAllPrescriptions(@RequestParam(required = false) String status, Model model) {
        List<Prescription> prescriptions;
        if (status != null && !status.isEmpty()) {
            PrescriptionStatus prescriptionStatus = PrescriptionStatus.fromString(status);  // Convert string to enum
            prescriptions = prescriptionService.getPrescriptionsByStatus(prescriptionStatus.getValue());  // Use int value for DB query
        } else {
            prescriptions = prescriptionService.getPrescriptionList();
        }
        model.addAttribute("listPrescription", prescriptions);
        model.addAttribute("currentStatus", status);
        return "viewall-prescription";
    }     

    @GetMapping("/add/find-patient")
    public String findPatient(Model model) {
        return "form-find-patient";
    }
    
    @PostMapping("/add/find-patient")
    public String findPatientByNIK(@RequestParam("nik") String nik, Model model) {
        Patient patient = patientService.getPatientByNik(nik);
        if (patient != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
            LocalDate birthDate = patient.getBirthDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String formattedBirthDate = birthDate.format(formatter);
    
            // Calculate age
            LocalDate currentDate = LocalDate.now();
            Period age = Period.between(birthDate, currentDate);
            String ageFormatted = age.getYears() + " Years, " + age.getMonths() + " Months, " + age.getDays() + " Days";
    
            model.addAttribute("patient", patient);
            model.addAttribute("found", true);
            model.addAttribute("formattedBirthDate", formattedBirthDate);
            model.addAttribute("ageFormatted", ageFormatted);
        } else {
            model.addAttribute("found", false);
        }
        return "find-patient-result";
    }    

    @GetMapping("/add/{idPatient}")
    public String addPrescriptionFormPage(@PathVariable UUID idPatient, Model model) {
        Patient patient = patientService.getPatientById(idPatient);
        List<Medicine> listMedicine = medicineService.getAvailableMedicine();

        AddPrescriptionRequestDTO addPrescriptionRequestDTO = new AddPrescriptionRequestDTO();
        addPrescriptionRequestDTO.setPatient(patient);

        // Initialize the medicine quantity list with one row
        List<AddMedicineQuantityRequestDTO> medicineQuantityList = new ArrayList<>();
        medicineQuantityList.add(new AddMedicineQuantityRequestDTO()); // Add one default medicine row

        addPrescriptionRequestDTO.setMedicineQuantityList(medicineQuantityList);

        model.addAttribute("addPrescriptionRequestDTO", addPrescriptionRequestDTO);
        model.addAttribute("listMedicine", listMedicine);
        model.addAttribute("patientId", idPatient);
        return "form-add-prescription";
    }

    @PostMapping("/add/{idPatient}")
    public String addPrescriptionFormSubmit(@PathVariable UUID idPatient,
                                            @ModelAttribute @Valid AddPrescriptionRequestDTO addPrescriptionRequestDTO,
                                            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("listMedicine", medicineService.getAvailableMedicine());
            model.addAttribute("responseMessage", bindingResult.getAllErrors());
            return "prescription-resp";
        }
    
        // Fetch the patient entity from the database
        Patient patient = patientService.getPatientById(idPatient);
        if (patient == null) {
            model.addAttribute("responseMessage", "Patient not found.");
            return "error-page";
        }
    
        // Create the Prescription object
        Prescription prescription = new Prescription();
        prescription.setPatient(patient);
    
        // Use a Map to group medicines by their ID (String) and sum the quantities
        Map<String, MedicineQuantity> medicineQuantityMap = new HashMap<>();
        for (AddMedicineQuantityRequestDTO mqDTO : addPrescriptionRequestDTO.getMedicineQuantityList()) {
            if (mqDTO.getMedicine() != null && mqDTO.getMedicine().getId() != null && mqDTO.getQuantity() > 0) {
                String medicineId = mqDTO.getMedicine().getId();
                MedicineQuantity existingMq = medicineQuantityMap.get(medicineId);
                
                if (existingMq == null) {
                    MedicineQuantity newMq = new MedicineQuantity();
                    newMq.setMedicine(medicineService.getMedicineById(medicineId)); // Get the Medicine entity using the String ID
                    newMq.setQuantity(mqDTO.getQuantity());
                    newMq.setFulfilledQty(0); // Initially unfulfilled
                    medicineQuantityMap.put(medicineId, newMq);
                } else {
                    // Sum the quantity if the medicine already exists
                    existingMq.setQuantity(existingMq.getQuantity() + mqDTO.getQuantity());
                }
            }
        }
    
        // Add the merged medicine quantities to the prescription
        List<MedicineQuantity> medicineQuantities = new ArrayList<>(medicineQuantityMap.values());
    
        // Save the prescription and associated medicine quantities
        prescriptionService.addPrescription(prescription, medicineQuantities);
    
        model.addAttribute("prescription", prescription);
        model.addAttribute("responseMessage",
            String.format("Prescription untuk %s dengan ID %s berhasil ditambahkan.", patient.getName(), prescription.getId()));
        return "prescription-resp";
    }    

    @PostMapping(value = "/add/{idPatient}", params = {"addRow"})
    public String addRow(@ModelAttribute AddPrescriptionRequestDTO addPrescriptionRequestDTO, Model model) {
        addPrescriptionRequestDTO.getMedicineQuantityList().add(new AddMedicineQuantityRequestDTO());

        model.addAttribute("addPrescriptionRequestDTO", addPrescriptionRequestDTO);
        model.addAttribute("listMedicine", medicineService.getAvailableMedicine());
        return "form-add-prescription";
    }

    @PostMapping(value="/add/{idPatient}", params={"deleteRow"})
    public String deleteRow(@ModelAttribute AddPrescriptionRequestDTO addPrescriptionRequestDTO, @RequestParam(value = "deleteRow") int row, Model model) {
        
        addPrescriptionRequestDTO.getMedicineQuantityList().remove(row);

        model.addAttribute("addPrescriptionRequestDTO", addPrescriptionRequestDTO);
        model.addAttribute("listMedicine", medicineService.getAvailableMedicine());
        return "form-add-prescription";
    }

    @GetMapping("/add")
    public String addPrescriptionAndPatient(Model model) {
        List<Medicine> listMedicine = medicineService.getAvailableMedicine();
        AddPrescriptionRequestDTO addPrescriptionRequestDTO = new AddPrescriptionRequestDTO();
        AddPatientRequestDTO addPatientRequestDTO = new AddPatientRequestDTO();

        // Initialize the medicine quantity list with one row
        List<AddMedicineQuantityRequestDTO> medicineQuantityList = new ArrayList<>();
        medicineQuantityList.add(new AddMedicineQuantityRequestDTO()); // Add one default medicine row

        addPrescriptionRequestDTO.setMedicineQuantityList(medicineQuantityList);

        model.addAttribute("addPrescriptionRequestDTO", addPrescriptionRequestDTO);
        model.addAttribute("addPatientRequestDTO", addPatientRequestDTO);
        model.addAttribute("listMedicine", listMedicine);
        return "form-add-prescriptionpatient";
    }

    @PostMapping("/add")
    public String addPrescriptionAndPatientSubmit(@ModelAttribute @Valid AddPrescriptionRequestDTO addPrescriptionRequestDTO,
                                                  @ModelAttribute @Valid AddPatientRequestDTO addPatientRequestDTO,
                                                  BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("listMedicine", medicineService.getAvailableMedicine());
            model.addAttribute("responseMessage", bindingResult.getAllErrors());
            return "medicine-resp";
        }
    
        // Create and save the Patient object
        Patient patient = new Patient();
        patient.setNik(addPatientRequestDTO.getNik());
        patient.setName(addPatientRequestDTO.getName());
        patient.setEmail(addPatientRequestDTO.getEmail());
        patient.setGender(addPatientRequestDTO.getGender());
        patient.setBirthDate(addPatientRequestDTO.getBirthDate());
        patientService.addPatient(patient);
    
        var newPatient = patientService.getPatientByNik(addPatientRequestDTO.getNik());
    
        // Create the Prescription object
        Prescription prescription = new Prescription();
        prescription.setPatient(newPatient);
    
        // Use a Map to group medicines by their ID and sum the quantities
        Map<String, MedicineQuantity> medicineQuantityMap = new HashMap<>();
        for (AddMedicineQuantityRequestDTO mqDTO : addPrescriptionRequestDTO.getMedicineQuantityList()) {
            if (mqDTO.getMedicine() != null && mqDTO.getMedicine().getId() != null && mqDTO.getQuantity() > 0) {
                String medicineId = mqDTO.getMedicine().getId();
                MedicineQuantity existingMq = medicineQuantityMap.get(medicineId);
                
                if (existingMq == null) {
                    MedicineQuantity newMq = new MedicineQuantity();
                    newMq.setMedicine(medicineService.getMedicineById(medicineId));
                    newMq.setQuantity(mqDTO.getQuantity());
                    newMq.setFulfilledQty(0);  // Initially unfulfilled
                    medicineQuantityMap.put(medicineId, newMq);
                } else {
                    // Sum the quantity if the medicine already exists
                    existingMq.setQuantity(existingMq.getQuantity() + mqDTO.getQuantity());
                }
            }
        }
    
        // Add the merged medicine quantities to the prescription
        List<MedicineQuantity> medicineQuantities = new ArrayList<>(medicineQuantityMap.values());
    
        // Save the prescription and associated medicine quantities
        prescriptionService.addPrescription(prescription, medicineQuantities);
    
        model.addAttribute("prescription", prescription);
        model.addAttribute("responseMessage",
            String.format("Prescription untuk %s dengan ID %s berhasil ditambahkan.", patient.getName(), prescription.getId()));
        return "prescription-resp";
    }    

    @PostMapping(value = "/add", params = {"addRow"})
    public String addRow(@ModelAttribute AddPrescriptionRequestDTO addPrescriptionRequestDTO, AddPatientRequestDTO addPatientRequestDTO, Model model) {
        addPrescriptionRequestDTO.getMedicineQuantityList().add(new AddMedicineQuantityRequestDTO());

        model.addAttribute("addPatientRequestDTO", addPatientRequestDTO);
        model.addAttribute("addPrescriptionRequestDTO", addPrescriptionRequestDTO);
        model.addAttribute("listMedicine", medicineService.getAvailableMedicine());
        return "form-add-prescriptionpatient";
    }

    @PostMapping(value="/add", params={"deleteRow"})
    public String deleteRow(@ModelAttribute AddPrescriptionRequestDTO addPrescriptionRequestDTO, AddPatientRequestDTO addPatientRequestDTO, @RequestParam(value = "deleteRow") int row, Model model) {
        
        addPrescriptionRequestDTO.getMedicineQuantityList().remove(row);

        model.addAttribute("addPatientRequestDTO", addPatientRequestDTO);
        model.addAttribute("addPrescriptionRequestDTO", addPrescriptionRequestDTO);
        model.addAttribute("listMedicine", medicineService.getAvailableMedicine());
        return "form-add-prescriptionpatient";
    }

    @GetMapping("/{idPrescription}")
    public String viewPrescriptionDetail(@PathVariable String idPrescription, Model model) {
        Prescription prescription = prescriptionService.getPrescriptionById(idPrescription);
        if (prescription == null) {
            return "error/404";
        }
    
        List<MedicineQuantity> medicineQuantities = medicineQuantityService.getMedicineQuantityByPrescriptionID(idPrescription);

        Boolean deletable = (prescription.getStatus() == PrescriptionStatus.CREATED.getValue() || prescription.getStatus() == PrescriptionStatus.WAITING_FOR_STOCK.getValue());
    
        model.addAttribute("prescription", prescription);
        model.addAttribute("medicineQuantities", medicineQuantities);
        model.addAttribute("PrescriptionStatus", PrescriptionStatus.class);
        model.addAttribute("deletable", deletable);
        return "view-prescription";
    }

    @GetMapping("/{idPrescription}/update")
    public String updatePrescriptionForm(@PathVariable String idPrescription, Model model) {
        
        Prescription prescription = prescriptionService.getPrescriptionById(idPrescription);
        if (prescription == null) {
            return "error/404";
        }
    
        UpdatePrescriptionRequestDTO updatePrescriptionRequestDTO = new UpdatePrescriptionRequestDTO();
        updatePrescriptionRequestDTO.setId(prescription.getId());
        updatePrescriptionRequestDTO.setPatient(prescription.getPatient());
        updatePrescriptionRequestDTO.setStatus(prescription.getStatus());
    
        List<MedicineQuantity> medicineQuantities = medicineQuantityService.getMedicineQuantityByPrescriptionID(idPrescription);
    
        List<UpdateMedicineQuantityRequestDTO> medicineQuantityList = medicineQuantities.stream()
            .map(mq -> {
                UpdateMedicineQuantityRequestDTO mqDTO = new UpdateMedicineQuantityRequestDTO();
                mqDTO.setId(mq.getId());
                mqDTO.setMedicine(mq.getMedicine());
                mqDTO.setPrescription(mq.getPrescription());
                mqDTO.setQuantity(mq.getQuantity());
                mqDTO.setFulfilledQty(mq.getFulfilledQty());
                return mqDTO;
            })
            .collect(Collectors.toList());
    
        updatePrescriptionRequestDTO.setMedicineUpdateQuantityList(medicineQuantityList);
        
        model.addAttribute("updatePrescriptionRequestDTO", updatePrescriptionRequestDTO);
        model.addAttribute("listMedicine", medicineService.getAvailableMedicine());
        
        return "form-update-prescription";
    }

    // Update Prescription - POST method to handle the form submission
    @PostMapping("/{idPrescription}/update")
    public String updatePrescriptionSubmit(@PathVariable String idPrescription,
                                           @ModelAttribute @Valid UpdatePrescriptionRequestDTO updatePrescriptionRequestDTO,
                                           BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("listMedicine", medicineService.getAvailableMedicine());
            model.addAttribute("responseMessage", bindingResult.getAllErrors());
            return "prescription-resp";
        }
    
        // Fetch the existing prescription entity
        Prescription prescription = prescriptionService.getPrescriptionById(idPrescription);
        if (prescription == null) {
            return "error/404";
        }
    
        // Update prescription fields
        prescription.setStatus(updatePrescriptionRequestDTO.getStatus());
    
        // Use a Map to group medicines by their ID and sum the quantities
        Map<String, MedicineQuantity> medicineQuantityMap = new HashMap<>();
        for (UpdateMedicineQuantityRequestDTO mqDTO : updatePrescriptionRequestDTO.getMedicineUpdateQuantityList()) {
            if (mqDTO.getMedicine() != null && mqDTO.getMedicine().getId() != null && mqDTO.getQuantity() > 0) {
                String medicineId = mqDTO.getMedicine().getId();
                MedicineQuantity existingMq = medicineQuantityMap.get(medicineId);
                
                if (existingMq == null) {
                    // Create new MedicineQuantity if not found
                    MedicineQuantity newMq = new MedicineQuantity();
                    newMq.setMedicine(mqDTO.getMedicine());
                    newMq.setPrescription(prescription);
                    newMq.setQuantity(mqDTO.getQuantity());
                    newMq.setFulfilledQty(mqDTO.getFulfilledQty());
                    medicineQuantityMap.put(medicineId, newMq);
                } else {
                    // Sum the quantity if the medicine already exists
                    existingMq.setQuantity(existingMq.getQuantity() + mqDTO.getQuantity());
                    existingMq.setFulfilledQty(existingMq.getFulfilledQty() + mqDTO.getFulfilledQty());
                }
            }
        }
    
        // Add the updated medicine quantities to the prescription
        List<MedicineQuantity> updatedMedicineQuantities = new ArrayList<>(medicineQuantityMap.values());
    
        // Save the updated prescription and medicine quantities
        prescriptionService.updatePrescription(prescription, updatedMedicineQuantities);
    
        model.addAttribute("responseMessage", "Prescription updated successfully.");
        return "prescription-resp";
    }    

    @PostMapping(value = "/{idPrescription}/update", params = {"addRow"})
    public String addMedicineRow(@PathVariable String idPrescription, 
                                 @ModelAttribute UpdatePrescriptionRequestDTO updatePrescriptionRequestDTO, 
                                 Model model) {
        // Initialize the list if it is null
        if (updatePrescriptionRequestDTO.getMedicineUpdateQuantityList() == null) {
            updatePrescriptionRequestDTO.setMedicineUpdateQuantityList(new ArrayList<>());
        }
        // Add a new empty DTO for the new row
        updatePrescriptionRequestDTO.getMedicineUpdateQuantityList().add(new UpdateMedicineQuantityRequestDTO());
        
        model.addAttribute("updatePrescriptionRequestDTO", updatePrescriptionRequestDTO);
        model.addAttribute("listMedicine", medicineService.getAvailableMedicine());
        return "form-update-prescription";
    }

    // Remove a medicine row dynamically (similar to add in prescription)
    @PostMapping(value = "/{idPrescription}/update", params = {"deleteRow"})
    public String deleteMedicineRow(@ModelAttribute UpdatePrescriptionRequestDTO updatePrescriptionRequestDTO,
                                    @RequestParam("deleteRow") int row, Model model) {
        // Ensure the list is initialized before trying to remove
        if (updatePrescriptionRequestDTO.getMedicineUpdateQuantityList() == null) {
            updatePrescriptionRequestDTO.setMedicineUpdateQuantityList(new ArrayList<>());
        }
        
        // Check if the row index is valid before removing
        if (row >= 0 && row < updatePrescriptionRequestDTO.getMedicineUpdateQuantityList().size()) {
            updatePrescriptionRequestDTO.getMedicineUpdateQuantityList().remove(row);
        }
        
        model.addAttribute("updatePrescriptionRequestDTO", updatePrescriptionRequestDTO);
        model.addAttribute("listMedicine", medicineService.getAvailableMedicine());
        return "form-update-prescription";
    }    

    @GetMapping("/{idPrescription}/mark-as-done")
    public String showMarkAsDoneForm(@PathVariable String idPrescription, Model model) {
        Prescription prescription = prescriptionService.getPrescriptionById(idPrescription);
        if (prescription == null) {
            return "error/404";
        }

        List<MedicineQuantity> medicineQuantities = medicineQuantityService.getMedicineQuantityByPrescriptionID(idPrescription);
        List<Pharmacist> pharmacists = pharmacistService.getPharmacistList();

        UpdatePrescriptionRequestDTO updateDto = new UpdatePrescriptionRequestDTO();
        updateDto.setId(idPrescription);
        updateDto.setPharmacist(prescription.getPharmacist());

        model.addAttribute("prescription", prescription);
        model.addAttribute("medicineQuantities", medicineQuantities);
        model.addAttribute("pharmacists", pharmacists);
        model.addAttribute("updateDto", updateDto);

        return "form-changestatus-perscription";
    }

    @PostMapping("/{idPrescription}/mark-as-done")
    public String markPrescriptionAsDone(@PathVariable String idPrescription,
                                         @ModelAttribute UpdatePrescriptionRequestDTO updateDto, Model model) {
        try {
            // Delegate the prescription processing to the service
            prescriptionService.processPrescription(idPrescription, updateDto.getPharmacist().getId());
    
            model.addAttribute("responseMessage", "Prescription has been successfully processed.");
            return "prescription-resp";
        } catch (IllegalArgumentException e) {
            model.addAttribute("responseMessage", e.getMessage());
            return "error/404";
        }
    }
    

    @PostMapping("/{idPrescription}/delete")
    public String cancelPrescription(@PathVariable String idPrescription, Model model) {
        Prescription prescription = prescriptionService.getPrescriptionById(idPrescription);
        
        if (prescription == null) {
            model.addAttribute("responseMessage", "Prescription not found.");
            return "prescription-resp"; // Redirect to response page with message
        }
        
        // Only allow cancellation for prescriptions in "created" (0) or "waiting for stock" (1) statuses
        if (prescription.getStatus() != PrescriptionStatus.CREATED.getValue() 
                && prescription.getStatus() != PrescriptionStatus.WAITING_FOR_STOCK.getValue()) {
            model.addAttribute("responseMessage", "Cannot cancel prescription in the current status.");
            return "prescription-resp"; // Redirect to response page with message
        }
    
        List<MedicineQuantity> medicineQuantities = medicineQuantityService.getMedicineQuantityByPrescriptionID(idPrescription);
    
        if (prescription.getStatus() == PrescriptionStatus.WAITING_FOR_STOCK.getValue()) {
            for (MedicineQuantity mq : medicineQuantities) {
                Medicine medicine = mq.getMedicine();
                int currentStock = medicine.getStock();
                int fulfilledQty = mq.getFulfilledQty();
    
                medicine.setStock(currentStock + fulfilledQty);
                mq.setFulfilledQty(0); // Reset fulfilled quantity to zero
                medicineService.updateMedicine(medicine);
                medicineQuantityService.updateMedicineQuantity(mq); // Update to reflect zero fulfillment
            }
        }
    
        // Set prescription status to canceled
        prescription.setStatus(PrescriptionStatus.CANCELED.getValue());
        prescriptionService.updatePrescription(prescription);
    
        // Send a success message to the response page
        model.addAttribute("responseMessage", "Prescription has been successfully canceled.");
        
        return "prescription-resp"; // Redirect to the response page with the message
    }

    @GetMapping("/statistic")
    public String viewPrescriptionStatistic(Model model) {
        return "statistics-prescription";
    }

}
