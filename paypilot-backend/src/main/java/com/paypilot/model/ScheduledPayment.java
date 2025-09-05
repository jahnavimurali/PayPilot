package com.paypilot.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "scheduled_payments")
public class ScheduledPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Long billId;
    private Double amount;
    @Column(name = "payment_date")
    private LocalDate scheduledDate;
    private String paymentMethod = String.valueOf(PaymentMethod.UPI);
    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isPaid = false;

    public ScheduledPayment(){

    }
    public ScheduledPayment(Long userId, Long billId, Double amount, LocalDate scheduledDate, String paymentMethod) {
        this.userId = userId;
        this.billId = billId;
        this.amount = amount;
        this.scheduledDate = scheduledDate;
        this.paymentMethod = paymentMethod;
        this.isPaid = false;
    }

    public Boolean getIsPaid(){
        return isPaid;
    }

    public void setIsPaid(Boolean status){
        this.isPaid = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getBillId() {
        return billId;
    }

    public void setBillId(Long billId) {
        this.billId = billId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public LocalDate getScheduledDate() {
        return scheduledDate;
    }

    public void setScheduledDate(LocalDate scheduledDate) {
        this.scheduledDate = scheduledDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

}