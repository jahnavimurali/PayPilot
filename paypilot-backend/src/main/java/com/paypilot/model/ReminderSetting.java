package com.paypilot.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "reminder_settings")
public class ReminderSetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // will use trigger + sequence
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "bill_id")
    private Long billId;

    @Column(name = "reminder_days_before")
    private int reminderDaysBefore=2;

    @Convert(converter = BooleanToYNConverter.class)
    @Column(length = 1)
    private Boolean enabled;

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

    public int getReminderDaysBefore() {
        return reminderDaysBefore;
    }

    public void setReminderDaysBefore(int reminderDaysBefore) {
        this.reminderDaysBefore = reminderDaysBefore;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }



}
