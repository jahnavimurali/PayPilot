package com.paypilot.repository;

import com.paypilot.model.ScheduledPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ScheduledPaymentRepository extends JpaRepository<ScheduledPayment, Long> {


    @Query("SELECT sp from ScheduledPayment  sp WHERE sp.userId = :userId")
    List<ScheduledPayment> findByUserId(Long userId);

    @Query("SELECT sp FROM ScheduledPayment sp WHERE sp.scheduledDate > CURRENT_DATE")
    List<ScheduledPayment> getUpcomingPayments();

    @Query("SELECT sp FROM ScheduledPayment sp WHERE sp.scheduledDate < CURRENT_DATE")
    List<ScheduledPayment> getPastPayments();

    @Query("""
           SELECT sp FROM ScheduledPayment sp WHERE sp.userId = :userId ORDER BY sp.isPaid ASC, sp.scheduledDate ASC
      """)
    List<ScheduledPayment> getPaymentsByUserSorted(@Param("userId") Long userId);

    @Query("""
    SELECT
        SUM(CASE WHEN sp.isPaid = 'N' AND sp.scheduledDate < CURRENT_DATE THEN 1 ELSE 0 END),
        SUM(CASE WHEN sp.isPaid = 'N' AND sp.scheduledDate BETWEEN CURRENT_DATE AND :reminderDate THEN 1 ELSE 0 END)
    FROM ScheduledPayment sp
    WHERE sp.userId = :userId
    """)
    Object[] getOverdueAndUpcomingCounts(@Param("userId") Long userId,
                                         @Param("reminderDate") LocalDate reminderDate);

    @Query("DELETE FROM ScheduledPayment sp WHERE sp.billId = :billId")
    void deleteScheduledPaymentByBillId(Long billId);

    List<ScheduledPayment> findByScheduledDate(LocalDate scheduledDate);





}

