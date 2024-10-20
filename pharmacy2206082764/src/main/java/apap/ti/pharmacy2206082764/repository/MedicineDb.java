package apap.ti.pharmacy2206082764.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import apap.ti.pharmacy2206082764.model.Medicine;

import java.util.List;


@Repository
public interface MedicineDb extends JpaRepository<Medicine, String> {

    List<Medicine> findByIsDeletedIsFalse();
    
}
