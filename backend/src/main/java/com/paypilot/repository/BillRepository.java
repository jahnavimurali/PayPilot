package com.paypilot.repository;

import com.paypilot.model.Bill;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface BillRepository extends JpaRepository<Bill, Long> {
    @Query("SELECT b FROM Bill b WHERE TRIM(UPPER(b.category)) = TRIM(UPPER(:category))")
    List<Bill> findByCategoryIgnoreCase(String category);

    @Query("SELECT b from Bill b WHERE b.userId = :userId")
    List<Bill> findByUserId(Long userId);

    @Query("Select b from Bill b WHERE b.userId = :userId and TRIM(UPPER(b.category))=TRIM(UPPER(:category))")
    List<Bill> findByUserIdAndCategory(Long userId, String category);

    @Query("SELECT b FROM Bill b WHERE TRIM(UPPER(b.category)) = TRIM(UPPER(:category)) " +
            "AND b.dueDate BETWEEN :startDate AND :endDate")
    List<Bill> findByCategoryAndDueDateBetween(String category, LocalDate startDate, LocalDate endDate);

    @Query("SELECT b FROM Bill b WHERE b.userId = :userId " +
            "AND TRIM(UPPER(b.category)) = TRIM(UPPER(:category)) " +
            "AND b.dueDate BETWEEN :startDate AND :endDate")
    List<Bill> findByUserIdAndCategoryAndDueDateBetween(Long userId, String category, LocalDate startDate, LocalDate endDate);

}
