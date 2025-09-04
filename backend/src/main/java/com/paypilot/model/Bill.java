package com.paypilot.model;

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
    private Boolean isRecurring = false;

    @Enumerated(EnumType.STRING)
    private Frequency frequency = Frequency.ONCE;

    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean isPaid = false;

    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean snoozeReminders = false;

    @Column(nullable = false, columnDefinition = "CHAR(1) DEFAULT 'N'")
    @Convert(converter = BooleanToYNConverter.class)
    private Boolean autoPayEnabled = false;

    private LocalDate nextDueDate;

    private String paymentMethod = String.valueOf(PaymentMethod.UPI);

    public Bill(){

    }
    public Bill(String title, Category category, double amount, LocalDate dueDate, Long userId, Boolean isRecurring, Frequency frequency, Boolean isPaid, Boolean snoozeReminders, Boolean autoPayEnabled, String paymentMethod) {
        this.title = title;
        this.category = category;
        this.amount = amount;
        this.dueDate = dueDate;
        this.userId = userId;
        this.isRecurring = isRecurring;
        this.frequency = frequency;
        this.isPaid = isPaid;
        this.snoozeReminders = snoozeReminders;
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

    public Boolean isRecurring() {
        return isRecurring;
    }


    public void setRecurring(boolean recurring) {
        isRecurring = recurring;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public Boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public Boolean isSnoozeReminders() {
        return snoozeReminders;
    }

    public void setSnoozeReminders(boolean snoozeReminders) {
        this.snoozeReminders = snoozeReminders;
    }

    public LocalDate getNextDueDate() {
        return nextDueDate;
    }

    public void setNextDueDate(LocalDate nextDueDate) {
        this.nextDueDate = nextDueDate;
    }
}
