package apap.ti.pharmacy2206082764.service;

import org.springframework.stereotype.Service;
import apap.ti.pharmacy2206082764.model.Patient;

import java.util.UUID;

public interface PatientService {

    Patient addPatient(Patient patient);
    Patient getPatientByNik(String nik);
    Patient getPatientById(UUID id);
    Long countPatient();
    
}
