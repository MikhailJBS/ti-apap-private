package apap.ti.pharmacy2206082764.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import apap.ti.pharmacy2206082764.model.Prescription;
import java.util.UUID;
import java.util.List;

@Repository
public interface PrescriptionDb extends JpaRepository<Prescription, String> {
    List<Prescription> findByStatus(int status);
}
