package com.paypilot.repository;

import com.paypilot.model.Payment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByUserId(Long userId);
    List<Payment> findByUserIdOrderByScheduledDateAsc(Long userId);

    void deleteByBillId(Long billId);

}
