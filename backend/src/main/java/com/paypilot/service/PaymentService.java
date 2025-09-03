package com.paypilot.service;

import com.paypilot.model.Payment;
import com.paypilot.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentService {

    private final PaymentRepository repo;

    public PaymentService(PaymentRepository repo) {
        this.repo = repo;
    }

    public Payment addPayment(Payment payment) {
        return repo.save(payment);
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
