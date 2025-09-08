package com.paypilot.service;

import com.paypilot.model.Bill;
import com.paypilot.repository.BillRepository;
import com.paypilot.repository.PaymentRepository;
import com.paypilot.repository.ReminderSettingRepository;
import com.paypilot.repository.ScheduledPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;
    @Autowired
    private ScheduledPaymentRepository scheduledPaymentRepository;
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private ReminderSettingRepository reminderSettingRepository;

    public Bill addBill(Bill bill) {
        return billRepository.save(bill);
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Bill getBillById(Long billId){
        return billRepository.findById(billId).orElse(null);
    }


    public List<Bill> getAllBillsByUserId(long userId) {
        List<Bill> results = billRepository.findByUserId(userId);
        return results;
    }

    public List<Bill> getBillsByUserIdAndCategory(long userId, String category) {
        List<Bill> results = billRepository.findByUserIdAndCategory(userId, category);
        return results;
    }

    public List<Bill> getBillsByCategory(String category) {
        List<Bill> results = billRepository.findByCategoryIgnoreCase(category);
        return results;
    }

    public List<Bill> getBillsByCategoryAndDateRange(String category, LocalDate startDate, LocalDate endDate) {
        return billRepository.findByCategoryAndDueDateBetween(category, startDate, endDate);
    }

    public List<Bill> getBillsByUserIdAndCategoryAndDateRange(Long userId, String category, LocalDate startDate, LocalDate endDate) {
        return billRepository.findByUserIdAndCategoryAndDueDateBetween(userId, category, startDate, endDate);
    }

    public Bill updateBill(Bill bill) {
        Long billId = bill.getId();
        try {
            Bill original = getBillById(billId);

            original.setTitle(bill.getTitle());
            original.setAmount(bill.getAmount());
            original.setCategory(bill.getCategory());
            original.setDueDate(bill.getDueDate());
            original.setPaid(bill.getIsPaid());

            // ✅ Persist changes to DB
            return billRepository.save(original);
        } catch (Exception e) {
            throw new RuntimeException("Failed to update bill with id: " + billId, e);
        }
    }

    public void payBill(Bill bill){
        Long id = bill.getId();
        try {
            Bill original = getBillById(id);
            original.setPaid(true);
            billRepository.save(original);
        } catch (Exception e) {
            throw new RuntimeException("Failed to pay bill with id: " + id, e);
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
