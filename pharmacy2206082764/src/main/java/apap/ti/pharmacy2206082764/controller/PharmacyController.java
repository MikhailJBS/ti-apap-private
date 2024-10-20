package apap.ti.pharmacy2206082764.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import apap.ti.pharmacy2206082764.service.*;

@Controller
public class PharmacyController {

    @Autowired
    private MedicineService medicineService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private PrescriptionService prescriptionService;
    
    @GetMapping("/")
    public String home(Model model) {

        Long medicineCount = medicineService.countMedicine();
        Long patientCount = patientService.countPatient();
        Long prescriptionCount = prescriptionService.countPrescriptionToProcess();

        model.addAttribute("medicineCount", medicineCount);
        model.addAttribute("patientCount", patientCount);
        model.addAttribute("prescriptionCount", prescriptionCount);
        return "home";
    }

}
