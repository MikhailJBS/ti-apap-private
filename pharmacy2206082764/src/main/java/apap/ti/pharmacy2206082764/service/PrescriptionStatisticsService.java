package apap.ti.pharmacy2206082764.service;

import apap.ti.pharmacy2206082764.dto.request.MedicationUsageStatisticDTO;

import java.time.YearMonth;
import java.util.List;

public interface PrescriptionStatisticsService {
    List<MedicationUsageStatisticDTO> getMedicationUsageStatistics(YearMonth yearMonth);
}