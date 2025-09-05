package com.paypilot.service;

import com.paypilot.model.Bill;
import com.paypilot.model.Frequency;           // <-- enum
import com.paypilot.model.Payment;
import com.paypilot.model.ScheduledPayment;
import com.paypilot.repository.ScheduledPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class ScheduledPaymentService {

    @Autowired
    private ScheduledPaymentRepository scheduledPaymentRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private BillService billService;

    // -------------------------
    // Basic CRUD / Queries
    // -------------------------
    public ScheduledPayment schedulePayment(ScheduledPayment payment) {
        return scheduledPaymentRepository.save(payment);
    }

    public List<ScheduledPayment> getAllScheduledPayments() {
        return scheduledPaymentRepository.findAll();
    }

    public List<ScheduledPayment> getPaymentsByUserId(Long userId) {
        return scheduledPaymentRepository.getPaymentsByUserSorted(userId);
    }

    public List<ScheduledPayment> getUpcomingPayments(Long userId) {
        return scheduledPaymentRepository.getUpcomingPayments(userId);
    }

    public List<ScheduledPayment> getPastPayments(Long userId) {
        return scheduledPaymentRepository.getPastPayments(userId);
    }

    public ScheduledPayment getById(Long id) {
        return scheduledPaymentRepository.findById(id).orElse(null);
    }

    // -------------------------
    // Business logic
    // -------------------------

    /** Create a scheduled payment from a bill when auto-pay is enabled. */
    public void createScheduledPaymentFromBill(Bill bill) {
        if (bill == null) return;
        if (bill.isAutoPayEnabled()) {
            ScheduledPayment sp = new ScheduledPayment(
                    bill.getUserId(),
                    bill.getId(),
                    bill.getAmount(),
                    bill.getDueDate(),
                    bill.getPaymentMethod()
            );
            schedulePayment(sp);
        }
        // else: do nothing (manual pay)
    }

    /** Build a Payment from an existing ScheduledPayment (for "mark as paid"). */
    public Payment getPaymentFromScheduledPayment(Long id) {
        ScheduledPayment sp = scheduledPaymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scheduled payment not found with id " + id));
        return new Payment(
                sp.getBillId(),
                sp.getUserId(),
                sp.getAmount(),
                sp.getPaymentMethod(),
                LocalDate.now()
        );
    }

    /**
     * Mark a scheduled payment as paid:
     *  1) add a Payment entry (today),
     *  2) mark this ScheduledPayment as paid,
     *  3) if recurring, create a next ScheduledPayment and advance Bill.nextDueDate.
     */
    @Transactional
    public void setScheduledPaymentStatusPaid(Long id) {
        ScheduledPayment sp = scheduledPaymentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scheduled payment not found with id " + id));

        // (1) Add a Payment record for today
        Payment payment = new Payment(
                sp.getBillId(),
                sp.getUserId(),
                sp.getAmount(),
                sp.getPaymentMethod(),
                LocalDate.now()
        );
        paymentService.addPayment(payment);

        // (2) Mark this scheduled payment as paid
        sp.setIsPaid(true);
        scheduledPaymentRepository.save(sp);

        // (3) Schedule next one if the bill is recurring
        Bill bill = billService.getBillById(sp.getBillId());
        if (bill == null) return;

        Frequency freq = bill.getFrequency(); // enum
        if (freq == null || freq == Frequency.ONCE) {
            // one-time bill -> nothing more to schedule
            return;
        }

        // Compute the next due date from bill.nextDueDate if present, otherwise from this schedule date
        LocalDate base = (bill.getNextDueDate() != null) ? bill.getNextDueDate() : sp.getScheduledDate();
        LocalDate nextDate = computeNextDate(base, freq);

        // persist updated next due date on the bill
        bill.setNextDueDate(nextDate);
        billService.addBill(bill);

        // create & persist the next schedule
        ScheduledPayment next = new ScheduledPayment(
                sp.getUserId(),
                sp.getBillId(),
                sp.getAmount(),
                nextDate,
                sp.getPaymentMethod()
        );
        scheduledPaymentRepository.save(next);
    }

    /** helper: roll the base date forward by the enum frequency. */
    private LocalDate computeNextDate(LocalDate base, Frequency freq) {
        switch (freq) {
            case WEEKLY:
                return base.plusWeeks(1);
            case MONTHLY:
                return base.plusMonths(1);
            case YEARLY:
                return base.plusYears(1);
            default:
                // ONCE or unknown -> return base (caller guards ONCE)
                return base;
        }
    }

    /** Update an existing scheduled payment. */
    public ScheduledPayment updatePayment(Long id, ScheduledPayment updated) {
        return scheduledPaymentRepository.findById(id).map(existing -> {
            existing.setBillId(updated.getBillId());
            existing.setAmount(updated.getAmount());
            existing.setPaymentMethod(updated.getPaymentMethod());
            existing.setScheduledDate(updated.getScheduledDate());
            return scheduledPaymentRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Scheduled payment not found with id " + id));
    }

    /** Delete only this scheduled payment row. */
    public void deletePayment(Long id) {
        scheduledPaymentRepository.deleteById(id);
    }

    /**
     * Delete the ENTIRE bill and all related rows, by scheduled payment id:
     *  - all Payments for that bill
     *  - all ScheduledPayments for that bill
     *  - the Bill itself
     */
    @Transactional
    public void deletePaymentAndBill(Long id) {
        ScheduledPayment sp = getById(id);
        if (sp == null) {
            throw new RuntimeException("Scheduled payment not found with id " + id);
        }
        billService.deleteBillById(sp.getBillId());
    }
}
