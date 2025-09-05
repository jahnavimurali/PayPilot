package com.paypilot.service;

import com.paypilot.model.Bill;
import com.paypilot.repository.BillRepository;
import com.paypilot.repository.PaymentRepository;
import com.paypilot.repository.ScheduledPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

    @Autowired
    private ScheduledPaymentRepository scheduledPaymentRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    public Bill addBill(Bill bill) {
        return billRepository.save(bill);
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Bill getBillById(Long billId){
        return billRepository.findById(billId).orElse(null);
    }

    public List<Bill> getAllBillsByUserId(Long userId) {
        return billRepository.findByUserId(userId);
    }

    // Optional alias for readability
    public List<Bill> getBillsByUserId(Long userId) {
        return getAllBillsByUserId(userId);
    }

    public List<Bill> getBillsByUserIdAndCategory(Long userId, String category) {
        return billRepository.findByUserIdAndCategory(userId, category);
    }

    public List<Bill> getBillsByCategory(String category) {
        System.out.println("Category in service: '" + category + "'");
        List<Bill> results = billRepository.findByCategoryIgnoreCase(category);
        System.out.println("Results from DB: " + results.size());
        for (Bill b : results) {
            System.out.println("➡️ Found: " + b.getTitle() + " | " + b.getCategory());
        }
        return results;
    }

    public Bill updateBill(Bill bill) {
        Long billId = bill.getId();
        try {
            Bill original = getBillById(billId);
            if (original == null) {
                throw new RuntimeException("Bill not found with id: " + billId);
            }

            original.setTitle(bill.getTitle());
            original.setAmount(bill.getAmount());
            original.setCategory(bill.getCategory());
            original.setDueDate(bill.getDueDate());
            original.setRecurring(bill.isRecurring());
            original.setFrequency(bill.getFrequency());
            original.setPaid(bill.isPaid());
            original.setSnoozeReminders(bill.isSnoozeReminders());

            // Preserve incoming nextDueDate if provided
            if (bill.getNextDueDate() != null) {
                original.setNextDueDate(bill.getNextDueDate());
            }

            return billRepository.save(original);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update bill with id: " + billId, e);
        }
    }

    /**
     * Delete a bill by ID, removing dependent rows first (Payments, ScheduledPayments), then the Bill.
     * Transactional = all or nothing.
     */
    @Transactional
    public void deleteBillById(Long billId) {
        try {
            // 1) direct payments referencing this bill
            paymentRepository.deleteByBillId(billId);
            // 2) scheduled payments referencing this bill
            scheduledPaymentRepository.deleteByBillId(billId);
            // 3) the bill itself
            billRepository.deleteById(billId);
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete bill and related records for billId=" + billId, e);
        }
    }

    /**
     * Delete a bill by entity, removing dependents first.
     */
    @Transactional
    public void deleteBill(Bill bill){
        if (bill == null || bill.getId() == null) {
            throw new RuntimeException("Bill is null or has no id; cannot delete.");
        }
        Long billId = bill.getId();
        try {
            // dependents first
            paymentRepository.deleteByBillId(billId);
            scheduledPaymentRepository.deleteByBillId(billId);
            // then bill
            billRepository.delete(bill);
        } catch (RuntimeException e) {
            throw new RuntimeException("Failed to delete bill and related records for billId=" + billId, e);
        }
    }
}
