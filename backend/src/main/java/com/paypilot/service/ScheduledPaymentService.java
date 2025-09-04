package com.paypilot.service;

import com.paypilot.model.Bill;
import com.paypilot.model.Payment;
import com.paypilot.model.ScheduledPayment;
import com.paypilot.repository.ScheduledPaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ScheduledPaymentService {

    @Autowired
    private ScheduledPaymentRepository scheduledPaymentRepository;

    @Autowired
    private BillService billService;

    @Autowired
    private PaymentService paymentService;

    public ScheduledPayment schedulePayment(ScheduledPayment payment) {
        return scheduledPaymentRepository.save(payment);
    }

    public List<ScheduledPayment> getAllScheduledPayments() {
        return scheduledPaymentRepository.findAll();
    }

    public List<ScheduledPayment> getPaymentsByUserId(Long userId) {
        return scheduledPaymentRepository.findByUserId(userId);
    }

    public List<ScheduledPayment> getUpcomingPayments() {
        return scheduledPaymentRepository.getUpcomingPayments();
    }

    public List<ScheduledPayment> getPastPayments() {
        return scheduledPaymentRepository.getPastPayments();
    }

    public ScheduledPayment updatePayment(Long id, ScheduledPayment updated) {
        return scheduledPaymentRepository.findById(id).map(existing -> {
            existing.setBillId(updated.getBillId());
            existing.setAmount(updated.getAmount());
            existing.setPaymentMethod(updated.getPaymentMethod());
            existing.setScheduledDate(updated.getScheduledDate());
            return scheduledPaymentRepository.save(existing);
        }).orElseThrow(() -> new RuntimeException("Payment not found with id " + id));
    }

    public void deletePayment(Long id) {
        scheduledPaymentRepository.deleteById(id);
    }

    public void createScheduledPaymentFromBill(Bill bill){
        try{
            if(bill.isAutoPayEnabled()==true){
                ScheduledPayment scheduledPayment = new ScheduledPayment(bill.getUserId(), bill.getId(), bill.getAmount(), bill.getDueDate(),bill.getPaymentMethod());
                schedulePayment(scheduledPayment);
            }else {
                throw new RuntimeException("Auto pay is disabled !");
            }

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public Payment getPaymentFromScheduledPayment(Long id){
        Optional<ScheduledPayment> scheduledPayment = scheduledPaymentRepository.findById(id);
        try{
            ScheduledPayment thisPayment = scheduledPayment.get();
            LocalDate todayDate = LocalDate.now();
            Payment payment = new Payment();
            payment.setBillId(thisPayment.getBillId());
            payment.setUserId(thisPayment.getUserId());
            payment.setAmount(thisPayment.getAmount());
            payment.setMethod(thisPayment.getPaymentMethod());
            payment.setScheduledDate(todayDate);
            return payment;
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

    public void setScheduledPaymentStatusPaid(Long id){
        Optional<ScheduledPayment> scheduledPayment = scheduledPaymentRepository.findById(id);
        try{
            ScheduledPayment thisPayment = scheduledPayment.get();
            ScheduledPayment newScheduledPayment = new ScheduledPayment(thisPayment.getUserId(), thisPayment.getBillId(), thisPayment.getAmount(),thisPayment.getScheduledDate(), thisPayment.getPaymentMethod());

            // adding to completed payments
            Payment payment = getPaymentFromScheduledPayment(id);
            paymentService.addPayment(payment);

            // updating the scheduled payment and setting new schedule for the next billing cycle
            Bill bill = billService.getBillById(thisPayment.getBillId());
            LocalDate dueDate = bill.getNextDueDate();
            String frequency = String.valueOf(bill.getFrequency());
            thisPayment.setIsPaid(true);
            if(frequency.equalsIgnoreCase("ONCE")){
                return;
            }
            else if(frequency.equalsIgnoreCase("WEEKLY")) {
                newScheduledPayment.setScheduledDate(dueDate.plusDays(7));
                bill.setNextDueDate(dueDate.plusDays(7));
            }
            else if(frequency.equalsIgnoreCase("MONTHLY")){
                newScheduledPayment.setScheduledDate(dueDate.plusMonths(1));
                bill.setNextDueDate(dueDate.plusMonths(1));
            }
            else if(frequency.equalsIgnoreCase("YEARLY")){
                newScheduledPayment.setScheduledDate(dueDate.plusYears(1));
                bill.setNextDueDate(dueDate.plusYears(1));
            }
            billService.addBill(bill);
            schedulePayment(newScheduledPayment);

        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }

}

