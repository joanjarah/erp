package com.erp.accounting.repository;

import com.erp.accounting.entity.JournalEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JournalEntryRepository extends JpaRepository<JournalEntry, Long> {

    Optional<JournalEntry> findByEntryNumber(String entryNumber);

    List<JournalEntry> findByStatus(JournalEntry.JournalEntryStatus status);

    @Query("SELECT j FROM JournalEntry j WHERE j.entryDate BETWEEN :startDate AND :endDate " +
           "ORDER BY j.entryDate DESC")
    List<JournalEntry> findByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    @Query("SELECT j FROM JournalEntry j WHERE j.isBalanced = :isBalanced ORDER BY j.entryDate DESC")
    List<JournalEntry> findByBalanceStatus(@Param("isBalanced") Boolean isBalanced);

    boolean existsByEntryNumber(String entryNumber);
}
