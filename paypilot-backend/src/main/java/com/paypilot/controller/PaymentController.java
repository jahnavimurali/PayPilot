package com.paypilot.controller;

import com.paypilot.model.Payment;
import com.paypilot.service.PaymentService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service) {
        this.service = service;
    }

    @PostMapping
    public Payment add(@RequestBody Payment payment) {
        return service.addPayment(payment);
    }


    @GetMapping
    public List<Payment> getAll() {
        return service.getAllPayments();
    }
    @GetMapping("/user/{userId}")
    public List<Payment> getByUser(@PathVariable Long userId) {
        return service.getPaymentsByUserId(userId);
    }
    @PutMapping("/{id}")
    public Payment update(@PathVariable Long id, @RequestBody Payment payment) {
        return service.updatePayment(id, payment);
    }
    @DeleteMapping("/{id}")
    public String deletePayment(@PathVariable Long id) {
        service.deletePayment(id);
        return "Payment deleted successfully";
    }
}