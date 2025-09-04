package com.paypilot.controller;

import com.paypilot.model.ReminderSetting;
import com.paypilot.model.Bill;
import com.paypilot.model.ScheduledPayment;
import com.paypilot.service.BillService;
import com.paypilot.service.ReminderSettingService;
import com.paypilot.repository.ReminderSettingRepository;
import com.paypilot.service.ScheduledPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reminders")
public class ReminderSettingController {

    @Autowired
    private ReminderSettingService service;

    @Autowired
    private ReminderSettingRepository reminderSettingRepository;

    @Autowired
    private ScheduledPaymentService scheduledPaymentService;

    @Autowired
    private BillService billService;

    @PostMapping
    public ReminderSetting add(@RequestBody ReminderSetting setting) {
        return service.add(setting);
    }

    @GetMapping
    public List<ReminderSetting> getAll() {
        return service.getAll();
    }

    // FIXED: Always return latest settings first
    @GetMapping("/user/{userId}")
    public List<ReminderSetting> getByUser(@PathVariable Long userId) {
        return reminderSettingRepository.findByUserIdOrderByIdDesc(userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.delete(id);
    }

    // GET /api/reminders/due/{userId}
    @GetMapping("/due/{userId}")
    public List<String> getUpcomingReminders(@PathVariable Long userId) {
        // Always use the latest setting (newest row)
        List<ReminderSetting> settings = reminderSettingRepository.findByUserIdOrderByIdDesc(userId);
        int n = (settings == null || settings.isEmpty())
                ? 2
                : settings.get(0).getReminderDaysBefore(); // 0 is latest

        LocalDate today = LocalDate.now();

        List<ScheduledPayment> bills = scheduledPaymentService.getPaymentsByUserId(userId);
        List<String> reminders = new ArrayList<>();

        for (ScheduledPayment bill : bills) {
            if (bill.getScheduledDate() == null) continue;
            if (bill.getIsPaid() == true) continue;
            LocalDate due = bill.getScheduledDate(); // Bill#getDueDate() should return LocalDate
            long daysLeft = ChronoUnit.DAYS.between(today, due);
            Bill thisBill = billService.getBillById(bill.getBillId());
            // Show reminder if due in exactly n, 1, or 0 days
            if(daysLeft < 0 ){
                String msg = String.format(
                        "⚠️ Your %s (Amount: ₹%.2f) was due on %s and is still unpaid!",
                        thisBill.getTitle(),
                        thisBill.getAmount(),
                        due
                );
                reminders.add(msg);
            }
            else if (daysLeft <= n ) {
                String msg = String.format(
                        "You have %d day%s left to pay your %s (Amount: ₹%.2f, Due: %s)",
                        daysLeft,
                        daysLeft == 1 ? "" : "s",
                        thisBill.getTitle(),
                        thisBill.getAmount(),
                        due
                );
                reminders.add(msg);
            }
        }
        return reminders;
    }

    //    notification controller
    @GetMapping("/notifications/{userId}")
    public Map<String, Integer> getNotificationSummary(@PathVariable Long userId) {
        // Always use the latest setting (newest row)
        List<ReminderSetting> settings = reminderSettingRepository.findByUserIdOrderByIdDesc(userId);
        int n = (settings == null || settings.isEmpty())
                ? 2
                : settings.get(0).getReminderDaysBefore(); // 0 is latest

        LocalDate today = LocalDate.now();

        List<ScheduledPayment> bills = scheduledPaymentService.getPaymentsByUserId(userId);
        int upcoming = 0;
        int overdue = 0;
        Map<String,Integer> notification = new HashMap<>();
        for (ScheduledPayment bill : bills) {
            if (bill.getScheduledDate() == null) continue;
            if (bill.getIsPaid()==true) continue;
            LocalDate due = bill.getScheduledDate();
            long daysLeft = ChronoUnit.DAYS.between(today, due);

            // Show reminder if due in exactly n, 1, or 0 days
            if(daysLeft < 0 ){
                overdue += 1;
            }
            else if (daysLeft <= n ) {
                upcoming += 1;
            }
        }
        notification.put("Overdue", overdue);
        notification.put("Upcoming", upcoming);

        return notification;
    }
}