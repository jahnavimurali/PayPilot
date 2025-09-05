package com.paypilot.repository;

import com.paypilot.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List; 

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    // existing:
     List<Payment> findByUserIdOrderByScheduledDateAsc(Long userId);

    // ✅ new:
    void deleteByBillId(Long billId);
}
