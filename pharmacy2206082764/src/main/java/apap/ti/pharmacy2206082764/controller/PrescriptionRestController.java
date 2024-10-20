package apap.ti.pharmacy2206082764.controller;

import apap.ti.pharmacy2206082764.dto.request.MedicationUsageStatisticDTO;
import apap.ti.pharmacy2206082764.service.PrescriptionStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/prescription")
public class PrescriptionRestController {

    @Autowired
    private PrescriptionStatisticsService prescriptionStatisticsService;

    @GetMapping("/statistics")
    public ResponseEntity<List<MedicationUsageStatisticDTO>> getMedicationUsageStatistics(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        List<MedicationUsageStatisticDTO> statistics = prescriptionStatisticsService.getMedicationUsageStatistics(yearMonth);
        return ResponseEntity.ok(statistics);
    }
}