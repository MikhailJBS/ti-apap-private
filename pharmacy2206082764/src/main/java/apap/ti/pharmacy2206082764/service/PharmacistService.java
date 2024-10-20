package apap.ti.pharmacy2206082764.service;

import org.springframework.stereotype.Service;
import apap.ti.pharmacy2206082764.model.Pharmacist;

import java.util.List;
import java.util.UUID;

public interface PharmacistService {
    
    Pharmacist addPharmacist(Pharmacist pharmacist);
    List<Pharmacist> getPharmacistList();
    Pharmacist getPharmacistById(UUID id);

}
