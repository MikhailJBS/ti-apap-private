package apap.ti.pharmacy2206082764.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import apap.ti.pharmacy2206082764.model.MedicineQuantity;
import apap.ti.pharmacy2206082764.model.Medicine;
import java.util.List;

import java.time.LocalDateTime;


public interface MedicineQuantityDb extends JpaRepository<MedicineQuantity, UUID> {

    List<MedicineQuantity> findAllByPrescription_Id(String idPrescription);
    List<MedicineQuantity> findByPrescriptionCreatedDateBetween(LocalDateTime start, LocalDateTime end);
    List<MedicineQuantity> findByMedicineAndPrescriptionStatusIn(Medicine medicine, List<Integer> status);

}
