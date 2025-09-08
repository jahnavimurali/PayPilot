package com.paypilot.model;

import com.paypilot.util.BooleanToYNConverter;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private Category category = Category.OTHER;

    private double amount;

    @Column(name = "due_date")
    private LocalDate dueDate;


    @Column(name = "user_id")
    private Long userId;


    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isPaid = false;

    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean autoPayEnabled = false;


    private String paymentMethod = String.valueOf(PaymentMethod.UPI);

    public Bill(){

    }

    public Bill(String title, Category category, double amount, LocalDate dueDate, Long userId, Boolean isPaid, Boolean autoPayEnabled, String paymentMethod){
        this.title = title;
        this.category = category;
        this.amount = amount;
        this.dueDate = dueDate;
        this.userId = userId;
        this.isPaid = isPaid;
        this.autoPayEnabled = autoPayEnabled;
        this.paymentMethod = paymentMethod;

    }

    // getters & setters...

    public String getPaymentMethod(){
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod){
        this.paymentMethod = paymentMethod;
    }

    public Boolean isAutoPayEnabled(){
        return autoPayEnabled;
    }
    public void setAutoPayEnabled(Boolean autoPayEnabled){
        this.autoPayEnabled = autoPayEnabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public void setPaid(Boolean paid) {
        this.isPaid = paid;
    }
}
