package com.erp.accounting.service;

import com.erp.accounting.dto.FinancialSummaryDTO;
import com.erp.accounting.exception.ValidationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class ReportingService {

    private final StoredProcedureService storedProcedureService;

    public ReportingService(StoredProcedureService storedProcedureService) {
        this.storedProcedureService = storedProcedureService;
    }

    public List<FinancialSummaryDTO> getMonthlySummary(Integer year, Integer month) {
        LocalDate now = LocalDate.now();
        int resolvedYear = year != null ? year : now.getYear();
        int resolvedMonth = month != null ? month : now.getMonthValue();

        if (resolvedMonth < 1 || resolvedMonth > 12) {
            throw new ValidationException("Mois invalide: " + resolvedMonth);
        }
        if (resolvedYear < 2000 || resolvedYear > now.getYear() + 5) {
            throw new ValidationException("Année invalide: " + resolvedYear);
        }

        return storedProcedureService.getMonthlyFinancialSummary(resolvedYear, resolvedMonth);
    }
}
