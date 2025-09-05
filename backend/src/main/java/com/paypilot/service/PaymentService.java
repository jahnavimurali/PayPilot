package com.paypilot.service;

import com.paypilot.model.Payment;
import com.paypilot.model.ScheduledPayment;
import com.paypilot.repository.PaymentRepository;
import com.paypilot.repository.ScheduledPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository repo;

    public PaymentService(PaymentRepository repo) {
        this.repo = repo;
    }

    @Autowired
    private ScheduledPaymentRepository scheduledPaymentRepository;

    public Payment addPayment(Payment payment) {
        Payment saved = repo.save(payment);

        // Mark related scheduled payments as paid
        List<ScheduledPayment> scheduledPayments = scheduledPaymentRepository
                .findByUserId(payment.getUserId()).stream()
                .filter(sp -> sp.getBillId().equals(payment.getBillId()))
                .toList(); // or use collect(Collectors.toList()) for Java 8

        for (ScheduledPayment sp : scheduledPayments) {
            sp.setIsPaid(true);
            scheduledPaymentRepository.save(sp);
        }

        return saved;
    }

    public List<Payment> getAllPayments() {
        return repo.findAll();
    }
    public List<Payment> getPaymentsByUserId(Long userId) {
        return repo.findByUserId(userId);
    }
    public Payment updatePayment(Long id, Payment payment) {
        Payment existing = repo.findById(id).orElseThrow(() -> new RuntimeException("Payment not found"));
        existing.setAmount(payment.getAmount());
        existing.setMethod(payment.getMethod());
        existing.setScheduledDate(payment.getScheduledDate());
        // update other fields as needed
        return repo.save(existing);
    }
    public void deletePayment(Long id) {
        if (!repo.existsById(id)) {
            throw new RuntimeException("Payment not found with id: " + id);
        }
        repo.deleteById(id);
    }

}
