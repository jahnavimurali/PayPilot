package com.paypilot.service;

import com.paypilot.model.Bill;
import com.paypilot.repository.BillRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BillService {

    @Autowired
    private BillRepository billRepository;

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
        System.out.println("Category in service: '" + category + "'");
        List<Bill> results = billRepository.findByCategoryIgnoreCase(category);
        System.out.println("Results from DB: " + results.size());
        for (Bill b : results) {
            System.out.println("➡️ Found: " + b.getTitle() + " | " + b.getCategory());
        }
        return results;

    }

    public List<Bill> getBillsByCategoryAndDateRange(String category, LocalDate startDate, LocalDate endDate) {
        return billRepository.findByCategoryAndDueDateBetween(category, startDate, endDate);
    }

    public List<Bill> getBillsByUserIdAndCategoryAndDateRange(Long userId, String category, LocalDate startDate, LocalDate endDate) {
        return billRepository.findByUserIdAndCategoryAndDueDateBetween(userId, category, startDate, endDate);
    }


}
