package com.paypilot.controller;

import com.paypilot.model.ScheduledPayment;
import com.paypilot.service.ScheduledPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scheduled-payments")
public class ScheduledPaymentController {

    @Autowired
    private ScheduledPaymentService scheduledPaymentService;

    // POST: Schedule a new payment
    @PostMapping
    public ResponseEntity<ScheduledPayment> schedulePayment(@RequestBody ScheduledPayment payment) {
        ScheduledPayment savedPayment = scheduledPaymentService.schedulePayment(payment);
        return ResponseEntity.ok(savedPayment);
    }

    // GET: All scheduled payments
    @GetMapping
    public ResponseEntity<List<ScheduledPayment>> getAll() {
        return ResponseEntity.ok(scheduledPaymentService.getAllScheduledPayments());
    }

    // GET: Payments by userId
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ScheduledPayment>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(scheduledPaymentService.getPaymentsByUserId(userId));
    }

    @GetMapping("/upcoming")
    public List<ScheduledPayment> getUpcomingPayments() {
        return scheduledPaymentService.getUpcomingPayments();
    }

    @GetMapping("/history")
    public List<ScheduledPayment> getPastPayments() {
        return scheduledPaymentService.getPastPayments();
    }

    // PUT: Update payment
    @PutMapping("/{id}")
    public ResponseEntity<ScheduledPayment> updatePayment(@PathVariable Long id, @RequestBody ScheduledPayment updated) {
        ScheduledPayment payment = scheduledPaymentService.updatePayment(id, updated);
        return ResponseEntity.ok(payment);
    }

    // DELETE: Delete payment
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        scheduledPaymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("markPaid/{id}")
    public ResponseEntity<?> markScheduledPaymentAsPaid(@PathVariable Long id){
        try {
            scheduledPaymentService.setScheduledPaymentStatusPaid(id);
            return ResponseEntity.ok("Scheduled Payment has been Paid");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body("An error Occurred !");
        }
    }
}

