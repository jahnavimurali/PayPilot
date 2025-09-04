package com.paypilot.repository;

import com.paypilot.model.Payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    List<Payment> findByUserIdOrderByScheduledDateAsc(Long userId);

    @Query("DELETE FROM Payment p WHERE p.billId = :billId")
    void deletePaymentByBillId(Long billId);

}
