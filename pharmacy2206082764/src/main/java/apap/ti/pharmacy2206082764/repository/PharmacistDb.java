package apap.ti.pharmacy2206082764.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import apap.ti.pharmacy2206082764.model.Pharmacist;
import java.util.UUID;

@Repository
public interface PharmacistDb extends JpaRepository<Pharmacist, UUID> {

}
