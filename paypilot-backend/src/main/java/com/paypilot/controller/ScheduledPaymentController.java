package com.paypilot.controller;

import com.paypilot.model.ScheduledPayment;
import com.paypilot.service.ScheduledPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scheduled-payments")
@CrossOrigin(origins = "*")
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

    @GetMapping("/upcoming/{userId}")
    public ResponseEntity<List<ScheduledPayment>> getUpcomingPayments(@PathVariable Long userId) {
        return ResponseEntity.ok(scheduledPaymentService.getUpcomingPayments(userId));
    }

    @GetMapping("/history/{userId}")
    public ResponseEntity<List<ScheduledPayment>> getPastPayments(@PathVariable Long userId) {
        return ResponseEntity.ok(scheduledPaymentService.getPastPayments(userId));
    }

    // PUT: Update scheduled payment
    @PutMapping("/{id}")
    public ResponseEntity<ScheduledPayment> updatePayment(@PathVariable Long id, @RequestBody ScheduledPayment updated) {
        ScheduledPayment payment = scheduledPaymentService.updatePayment(id, updated);
        return ResponseEntity.ok(payment);
    }

    // PUT: Mark as paid
    @PutMapping("/markPaid/{id}")
    public ResponseEntity<?> markScheduledPaymentAsPaid(@PathVariable Long id) {
        try {
            scheduledPaymentService.setScheduledPaymentStatusPaid(id);
            return ResponseEntity.ok("Scheduled Payment has been paid.");
        } catch (RuntimeException e) {
            // server logs should have stack; return concise reason here
            return ResponseEntity.badRequest().body("Failed to mark as paid: " + e.getMessage());
        }
    }

    /**
     * DELETE from Scheduled Payments: delete the ENTIRE bill everywhere.
     * This will delete:
     *  - all Payments for the bill
     *  - all ScheduledPayments for the bill
     *  - the Bill itself
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePayment(@PathVariable Long id) {
        ScheduledPayment sp = scheduledPaymentService.getById(id);
        if (sp == null) {
            return ResponseEntity.status(404).body("Scheduled payment not found: id=" + id);
        }

        try {
            // cascades: payments -> scheduled -> bill
            scheduledPaymentService.deletePaymentAndBill(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(500).body("Failed to delete bill and related records: " + e.getMessage());
        }
    }
}
