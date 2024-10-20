package apap.ti.pharmacy2206082764.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import apap.ti.pharmacy2206082764.model.Patient;
import java.util.UUID;

@Repository
public interface PatientDb extends JpaRepository<Patient, UUID> {
    Patient findByNik(String nik);
}
