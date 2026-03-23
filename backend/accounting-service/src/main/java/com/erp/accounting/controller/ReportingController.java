package com.erp.accounting.controller;

import com.erp.accounting.dto.ApiResponseDTO;
import com.erp.accounting.dto.FinancialSummaryDTO;
import com.erp.accounting.service.ReportingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/reports", "/api/reports", "/api/v1/reports"})
@CrossOrigin(
        origins = {
                "http://localhost:3000",
                "http://127.0.0.1:3000",
                "http://localhost:3001",
                "http://127.0.0.1:3001",
                "http://localhost:5173",
                "http://127.0.0.1:5173",
                "http://localhost:4173",
                "http://127.0.0.1:4173"
        },
        allowCredentials = "true"
)
public class ReportingController {

    private static final Logger log = LoggerFactory.getLogger(ReportingController.class);

    private final ReportingService reportingService;

    public ReportingController(ReportingService reportingService) {
        this.reportingService = reportingService;
    }

    /**
     * GET /reports/monthly - Résumé financier mensuel
     */
    @GetMapping("/monthly")
    public ResponseEntity<ApiResponseDTO<List<FinancialSummaryDTO>>> getMonthlySummary(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month) {
        log.info("GET /reports/monthly - Résumé financier mensuel");

        List<FinancialSummaryDTO> summary = reportingService.getMonthlySummary(year, month);

        return ResponseEntity.ok(
                ApiResponseDTO.success(summary, "Résumé financier récupéré avec succès"));
    }
}
