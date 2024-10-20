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
import apap.ti.pharmacy2206082764.service.MedicineService;
import apap.ti.pharmacy2206082764.dto.request.AddMedicineRequestDTO;
import apap.ti.pharmacy2206082764.dto.request.UpdateMedicineRequestDTO;
import apap.ti.pharmacy2206082764.dto.request.RestockMultipleMedicineRequestDTO;
import apap.ti.pharmacy2206082764.dto.request.RestockMedicineRequestDTO;

@Controller
@RequestMapping("/medicine")
public class MedicineController {
    
    @Autowired
    private MedicineService medicineService;

    @GetMapping("")
    public String viewAllMedicine(Model model) {
        List<Medicine> listMedicine = medicineService.getMedicineList();
        List<Medicine> medicineToView = new ArrayList<>();

        for (Medicine medicine : listMedicine) {
            if (!medicine.isDeleted()) {
                medicineToView.add(medicine);
            }
        }

        model.addAttribute("listMedicine", medicineToView);
        return "viewall-medicine";
    }

    @GetMapping("/add")
    public String addMedicineFormPage(Model model) {
        AddMedicineRequestDTO medicineDTO = new AddMedicineRequestDTO();
        model.addAttribute("medicineDTO", medicineDTO);
        return "form-add-medicine";
    }

    @PostMapping("/add")
    public String addMedicineSubmit(
        @ModelAttribute @Valid AddMedicineRequestDTO medicineDTO,
        BindingResult bindingResult,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "form-add-medicine";
        } else {
            var newMedicine = new Medicine();
            newMedicine.setName(medicineDTO.getName());
            newMedicine.setPrice(medicineDTO.getPrice());
            newMedicine.setStock(medicineDTO.getStock());
            newMedicine.setDescription(medicineDTO.getDescription());
            medicineService.addMedicine(newMedicine);
            model.addAttribute("responseMessage",
                String.format("Medicine %s dengan ID %s berhasil ditambahkan.", newMedicine.getName(), newMedicine.getId()));
            return "medicine-resp";
        }
    }

    @GetMapping("/{id}")
    public String viewDetailMedicine(
        @PathVariable String id,
        Model model
    ) {
        Medicine medicine = medicineService.getMedicineById(id);
        String formattedPrice = String.format("Rp %,d.00", medicine.getPrice());
        model.addAttribute("medicine", medicine);
        model.addAttribute("formattedPrice", formattedPrice);
        return "view-medicine";
    }

    @GetMapping("/{id}/update")
    public String updateMedicineFormPage(
        @PathVariable String id,
        Model model
    ) {
        Medicine medicine = medicineService.getMedicineById(id);
        UpdateMedicineRequestDTO medicineDTO = new UpdateMedicineRequestDTO();
        medicineDTO.setId(medicine.getId());
        medicineDTO.setName(medicine.getName());
        medicineDTO.setPrice(medicine.getPrice());
        medicineDTO.setStock(medicine.getStock());
        medicineDTO.setDescription(medicine.getDescription());
        model.addAttribute("medicineDTO", medicineDTO);
        return "form-update-medicine";
    }

    @PostMapping("/update")
    public String updateMedicineSubmit(
        @ModelAttribute @Valid UpdateMedicineRequestDTO medicineDTO,
        BindingResult bindingResult,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            return "form-update-medicine";
        } else {
            var medicine = medicineService.getMedicineById(medicineDTO.getId());
            medicine.setName(medicineDTO.getName());
            medicine.setPrice(medicineDTO.getPrice());
            medicine.setStock(medicineDTO.getStock());
            medicine.setDescription(medicineDTO.getDescription());
            medicineService.updateMedicine(medicine);
            model.addAttribute("responseMessage",
                String.format("Medicine %s dengan ID %s berhasil diubah.", medicine.getName(), medicine.getId()));
            return "medicine-resp";
        }
    }

    @GetMapping("/{id}/delete")
    public String deleteMedicine(
        @PathVariable String id,
        Model model
    ) {
        var medicine = medicineService.getMedicineById(id);
        if (!medicineService.deleteMedicine(medicine)) {
            model.addAttribute("responseMessage",
                String.format("Gagal menghapus obat %s dengan ID %s karena masih terdapat resep yang menggunakan obat ini.", medicine.getName(), medicine.getId()));
            return "medicine-resp";
        }
        
        model.addAttribute("responseMessage",
            String.format("Medicine %s dengan ID %s berhasil dihapus.", medicine.getName(), medicine.getId()));
        return "medicine-resp";
    }

    @GetMapping("/restock")
    public String restockMedicinePage(Model model) {
        RestockMultipleMedicineRequestDTO restockDTO = new RestockMultipleMedicineRequestDTO();
        restockDTO.setRestockMedicineList(new ArrayList<>());
        restockDTO.getRestockMedicineList().add(new Medicine());  // Add an initial empty row
        
        model.addAttribute("restockDTO", restockDTO);
        model.addAttribute("listMedicine", medicineService.getMedicineList());
        
        return "form-restock-medicine";
    }

    @PostMapping("/restock")
    public String restockMultipleMedicine(
        @ModelAttribute RestockMultipleMedicineRequestDTO restockDTO,
        Model model
    ) {
        List<Medicine> updatedMedicines = new ArrayList<>();
        for (Medicine medicine : restockDTO.getRestockMedicineList()) {
            if (!medicine.getId().isEmpty() && medicine.getStock() > 0) {
                var existingMedicine = medicineService.getMedicineById(String.valueOf(medicine.getId()));
                existingMedicine.setStock(existingMedicine.getStock() + medicine.getStock());
                medicineService.updateMedicine(existingMedicine);
                updatedMedicines.add(existingMedicine);
            }
        }
        model.addAttribute("responseMessage", "Stok obat berhasil ditambahkan.");
        model.addAttribute("updatedMedicines", updatedMedicines);
        return "medicine-resp";
    }

    @PostMapping("/restock/add-row")
    public String addRowRestockMedicine(@ModelAttribute RestockMultipleMedicineRequestDTO restockDTO, Model model) {
        if (restockDTO.getRestockMedicineList() == null) {
            restockDTO.setRestockMedicineList(new ArrayList<>());
        }
        restockDTO.getRestockMedicineList().add(new Medicine());

        model.addAttribute("restockDTO", restockDTO);
        model.addAttribute("listMedicine", medicineService.getMedicineList());
        return "form-restock-medicine";
    }

    @PostMapping("/restock/delete-row")
    public String deleteRowRestockMedicine(@ModelAttribute RestockMultipleMedicineRequestDTO restockDTO, @RequestParam int index, Model model) {
        restockDTO.getRestockMedicineList().remove(index);

        model.addAttribute("restockDTO", restockDTO);
        model.addAttribute("listMedicine", medicineService.getMedicineList());
        return "form-restock-medicine";
    }

    @GetMapping("{id}/update-stock")
    public String updateStockMedicinePage(
        @PathVariable String id,
        Model model
    ) {
        Medicine medicine = medicineService.getMedicineById(id);
    
        var restockMedicineRequestDTO = new RestockMedicineRequestDTO();
        restockMedicineRequestDTO.setId(medicine.getId());
        restockMedicineRequestDTO.setName(medicine.getName());
        restockMedicineRequestDTO.setStock(medicine.getStock());
        restockMedicineRequestDTO.setAddStock(1);  // Set initial added stock to 1
    
        model.addAttribute("restockMedicineRequestDTO", restockMedicineRequestDTO);
        return "form-update-stock";
    }    

    @PostMapping("{id}/update-stock")
    public String updateStockMedicine(
        @PathVariable String id,
        @ModelAttribute @Valid RestockMedicineRequestDTO restockMedicineRequestDTO,
        BindingResult bindingResult,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("responseMessage", bindingResult.getAllErrors());
            return "medicine-resp";
        } else {
            var medicine = medicineService.getMedicineById(id);

            int oldStock = medicine.getStock();
            int additionStock = restockMedicineRequestDTO.getAddStock();
            int newStock = oldStock + additionStock;

            medicine.setStock(newStock);
            medicineService.updateMedicine(medicine);
            model.addAttribute("responseMessage",
                String.format("Stok obat %s dengan ID %s berhasil diubah.", medicine.getName(), medicine.getId()));
            return "medicine-resp";
        }
    }

}
