package apap.ti.pharmacy2206082764.service;

import apap.ti.pharmacy2206082764.dto.request.MedicationUsageStatisticDTO;
import apap.ti.pharmacy2206082764.model.MedicineQuantity;
import apap.ti.pharmacy2206082764.repository.MedicineQuantityDb;
import apap.ti.pharmacy2206082764.service.PrescriptionStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PrescriptionStatisticsServiceImpl implements PrescriptionStatisticsService {

    @Autowired
    private MedicineQuantityDb medicineQuantityDb;

    @Override
    public List<MedicationUsageStatisticDTO> getMedicationUsageStatistics(YearMonth yearMonth) {
        List<MedicineQuantity> medicineQuantities = medicineQuantityDb.findByPrescriptionCreatedDateBetween(
                yearMonth.atDay(1).atStartOfDay(),
                yearMonth.atEndOfMonth().plusDays(1).atStartOfDay()
        );

        return medicineQuantities.stream()
                .collect(Collectors.groupingBy(
                        mq -> mq.getMedicine().getName(),
                        Collectors.summingLong(MedicineQuantity::getQuantity)
                ))
                .entrySet().stream()
                .map(entry -> new MedicationUsageStatisticDTO(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.getUsageQuantity().compareTo(a.getUsageQuantity()))
                .limit(10)
                .collect(Collectors.toList());
    }
}