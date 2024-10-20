package apap.ti.pharmacy2206082764.service;

import org.springframework.stereotype.Service;
import apap.ti.pharmacy2206082764.model.Pharmacist;
import apap.ti.pharmacy2206082764.repository.PharmacistDb;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@Service
public class PharmacistServiceImpl implements PharmacistService {
    
    @Autowired
    PharmacistDb pharmacistDb;

    @Override
    public Pharmacist addPharmacist(Pharmacist pharmacist) {
        return pharmacistDb.save(pharmacist);
    }

    @Override
    public List<Pharmacist> getPharmacistList() {
        return pharmacistDb.findAll();
    }

    @Override
    public Pharmacist getPharmacistById(UUID id) {
        return pharmacistDb.findById(id).get();
    }

}
