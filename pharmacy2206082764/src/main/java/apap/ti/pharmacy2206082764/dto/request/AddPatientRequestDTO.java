package apap.ti.pharmacy2206082764.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddPatientRequestDTO {
    @NotBlank(message = "NIK tidak boleh kosong")
    @Size(max = 16, message = "NIK tidak boleh lebih dari 16 karakter")
    private String nik;

    @NotBlank(message = "Nama tidak boleh kosong")
    @Size(max = 255, message = "Nama tidak boleh lebih dari 255 karakter")
    private String name;

    @NotBlank(message = "Email tidak boleh kosong")
    @Email(message = "Email tidak valid")
    private String email;

    private Boolean gender;

    @DateTimeFormat(pattern = "yyyy-MM-dd")  // Add this annotation to specify the expected date format
    private Date birthDate;
}
