package com.paypilot.repository;

import com.paypilot.model.ScheduledPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduledPaymentRepository extends JpaRepository<ScheduledPayment, Long> {


    @Query("SELECT sp from ScheduledPayment  sp WHERE sp.userId = :userId")
    List<ScheduledPayment> findByUserId(Long userId);

    @Query("SELECT sp FROM ScheduledPayment sp WHERE sp.scheduledDate > CURRENT_DATE")
    List<ScheduledPayment> getUpcomingPayments();

    @Query("SELECT sp FROM ScheduledPayment sp WHERE sp.scheduledDate < CURRENT_DATE")
    List<ScheduledPayment> getPastPayments();
}

