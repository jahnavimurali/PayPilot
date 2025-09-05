package com.paypilot.repository;

import com.paypilot.model.ScheduledPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduledPaymentRepository extends JpaRepository<ScheduledPayment, Long> {

    List<ScheduledPayment> findByUserId(Long userId);

    List<ScheduledPayment> findByScheduledDate(LocalDate scheduledDate);

    void deleteByBillId(Long billId);

    @Query("""
           SELECT sp
           FROM ScheduledPayment sp
           WHERE sp.userId = :userId
             AND sp.isPaid = false
             AND sp.scheduledDate > CURRENT_DATE
           ORDER BY sp.scheduledDate
           """)
    List<ScheduledPayment> getUpcomingPayments(@Param("userId") Long userId);

    @Query("""
           SELECT sp
           FROM ScheduledPayment sp
           WHERE sp.userId = :userId
             AND (sp.scheduledDate < CURRENT_DATE OR sp.isPaid = true)
           ORDER BY sp.isPaid ASC, sp.scheduledDate ASC
           """)
    List<ScheduledPayment> getPastPayments(@Param("userId") Long userId);

    @Query("""
           SELECT sp
           FROM ScheduledPayment sp
           WHERE sp.userId = :userId
           ORDER BY sp.isPaid ASC, sp.scheduledDate ASC
           """)
    List<ScheduledPayment> getPaymentsByUserSorted(@Param("userId") Long userId);

    @Query("""
           SELECT
             SUM(CASE WHEN sp.isPaid = false AND sp.scheduledDate < CURRENT_DATE THEN 1 ELSE 0 END),
             SUM(CASE WHEN sp.isPaid = false AND sp.scheduledDate BETWEEN CURRENT_DATE AND :reminderDate THEN 1 ELSE 0 END)
           FROM ScheduledPayment sp
           WHERE sp.userId = :userId
           """)
    Object[] getOverdueAndUpcomingCounts(@Param("userId") Long userId,
                                         @Param("reminderDate") LocalDate reminderDate);

    // delete exactly ONE scheduled-payment row by composite
    @Modifying
    @Transactional
    int deleteByBillIdAndAmountAndPaymentMethodAndScheduledDate(
            Long billId,
            Double amount,
            String paymentMethod,
            LocalDate scheduledDate
    );
}
