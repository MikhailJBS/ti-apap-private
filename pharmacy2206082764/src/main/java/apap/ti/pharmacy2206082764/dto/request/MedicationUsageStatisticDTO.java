package apap.ti.pharmacy2206082764.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicationUsageStatisticDTO {
    private String medicineName;
    private Long usageQuantity;
}