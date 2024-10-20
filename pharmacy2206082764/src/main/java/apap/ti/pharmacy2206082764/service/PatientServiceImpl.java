package apap.ti.pharmacy2206082764.service;
import org.springframework.stereotype.Service;

import apap.ti.pharmacy2206082764.model.Patient;
import apap.ti.pharmacy2206082764.repository.PatientDb;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Service
public class PatientServiceImpl implements PatientService {
    
    @Autowired
    PatientDb patientDb;

    @Override
    public Patient addPatient(Patient patient) {
        return patientDb.save(patient);
    }

    @Override
    public Patient getPatientByNik(String nik) {
        return patientDb.findByNik(nik);
    }

    @Override
    public Patient getPatientById(UUID id) {
        return patientDb.findById(id).get();
    }

    @Override
    public Long countPatient() {
        return patientDb.count();
    }

}
