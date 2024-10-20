package apap.ti.pharmacy2206082764;

import java.util.List;
import java.util.ArrayList;

import apap.ti.pharmacy2206082764.model.Medicine;
import apap.ti.pharmacy2206082764.service.MedicineService;
import apap.ti.pharmacy2206082764.model.Patient;
import apap.ti.pharmacy2206082764.model.Prescription;
import apap.ti.pharmacy2206082764.service.PatientService;
import apap.ti.pharmacy2206082764.service.PrescriptionService;
import apap.ti.pharmacy2206082764.model.Pharmacist;
import apap.ti.pharmacy2206082764.service.PharmacistService;
import apap.ti.pharmacy2206082764.model.MedicineQuantity;

import com.github.javafaker.Faker;

import jakarta.transaction.Transactional;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
public class Pharmacy2206082764Application {

	public static void main(String[] args) {
		SpringApplication.run(Pharmacy2206082764Application.class, args);
	}
	@Bean
	@Transactional
	CommandLineRunner run(MedicineService medicineService, PatientService patientService,
		PrescriptionService prescriptionService, PharmacistService pharmacistService) {

		return args -> {
			var faker = new Faker(new Locale("in-ID"));

			var medicine = new Medicine();
			var fakeMedicine = faker.leagueOfLegends();
			var fakeDate = faker.date();

			medicine.setName(fakeMedicine.champion());
			medicine.setPrice(1000000L);
			medicine.setStock(faker.number().numberBetween(1, 100));
			medicine.setDescription(fakeMedicine.quote());

			medicineService.addMedicine(medicine);

			var patient = new Patient();
			patient.setName(faker.name().fullName());
			patient.setNik(faker.number().digits(16));
			patient.setEmail(faker.internet().emailAddress());
			patient.setGender(true);
			patient.setBirthDate(faker.date().birthday());

			patientService.addPatient(patient);

			var pharmacist = new Pharmacist();
			pharmacist.setName(faker.name().fullName());
			pharmacist.setEmail(faker.internet().emailAddress());
			pharmacist.setGender(true);

			pharmacistService.addPharmacist(pharmacist);

			var prescription = new Prescription();
			prescription.setPatient(patient);
			prescription.setTotalPrice(10000L);
			prescription.setStatus(0);

			List<MedicineQuantity> medicines = new ArrayList<>();
			var medicineQuantity = new MedicineQuantity();
			medicineQuantity.setMedicine(medicine);
			medicineQuantity.setQuantity(1);
			medicines.add(medicineQuantity);

			prescriptionService.addPrescription(prescription, medicines);


		};
	}

}
